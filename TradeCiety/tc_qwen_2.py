#!/usr/bin/env python3
"""
Enhanced Intraday Trading Strategy Backtester with NIFTY50 Market Context
--------------------------------------------------------------------------
This script implements an enhanced intraday trading strategy that incorporates
NIFTY50 market direction to filter high-quality trades. By only taking positions
that align with the broader market trend, we aim to improve average PPNL and win rate.

Strategy Enhancements:
- Added NIFTY50 trend filtering to avoid counter-trend trades
- Only take long positions when NIFTY50 is in uptrend
- Only take short positions when NIFTY50 is in downtrend
- Calculate NIFTY50 EMA(20) for trend determination
- Added volume confirmation for breakout validity
- Refined entry thresholds based on market volatility

Strategy Overview:
- Uses first 30 minutes (6 bars) to establish baseline range for the stock
- Long entry: Price breaks above highest high of first 6 bars + 0.3 * average range
  AND NIFTY50 is above its 20-period EMA
- Short entry: Price breaks below lowest low of first 6 bars - 0.3 * average range
  AND NIFTY50 is below its 20-period EMA
- Stop loss: 1.2 * average range from entry (tighter due to better filtering)
- Take profit: 2.5 * average range from entry (better reward due to higher quality trades)
- Volume confirmation: Entry volume must be above 1.2x average volume of first 6 bars
- Maximum one trade per day to avoid overtrading
- Entry time: After 09:20:00, Exit time: By 15:15:00

Target Performance:
- Win rate: 50-60%
- Average ppnl: 0.5-0.6%

Database Schema Requirements:
- Source table: market_data (with OHLCV data for both stock and NIFTY50)
- Target table: trades (will be created if not exists)
"""

import pandas as pd
import numpy as np
import mysql.connector
from mysql.connector import Error
from datetime import datetime, time, timedelta
import logging
import sys

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('enhanced_intraday_backtest.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

class EnhancedIntradayBacktester:
    def __init__(self, symbol='AXISBANK', nifty_symbol='NIFTY50', duration='5minute'):
        """
        Initialize the enhanced backtester with database connection and parameters.
        
        Args:
            symbol (str): Trading symbol to backtest (e.g., 'AXISBANK')
            nifty_symbol (str): NIFTY50 symbol for market context
            duration (str): Time duration for data (e.g., '5minute')
        """
        self.symbol = symbol
        self.nifty_symbol = nifty_symbol
        self.duration = duration
        self.db_config = {
            'host': 'localhost',
            'user': 'root',
            'password': 'root',
            'database': 'market'
        }
        self.conn = None
        self.cursor = None
        self.trades = []
        
    def connect_to_database(self):
        """Establish connection to MySQL database."""
        try:
            self.conn = mysql.connector.connect(**self.db_config)
            self.cursor = self.conn.cursor()
            logger.info("Successfully connected to MySQL database")
            return True
        except Error as e:
            logger.error(f"Error connecting to MySQL database: {e}")
            return False
    
    def disconnect_from_database(self):
        """Close database connection."""
        if self.cursor:
            self.cursor.close()
        if self.conn and self.conn.is_connected():
            self.conn.close()
            logger.info("MySQL connection closed")
    
    def create_trades_table(self):
        """Create trades table if it doesn't exist."""
        create_table_query = """
        CREATE TABLE IF NOT EXISTS trades (
            id INT AUTO_INCREMENT PRIMARY KEY,
            symbol VARCHAR(20) NOT NULL,
            entry_timestamp TIMESTAMP NOT NULL,
            entry_price DECIMAL(16,4) NOT NULL,
            exit_timestamp TIMESTAMP NOT NULL,
            exit_price DECIMAL(16,4) NOT NULL,
            stoploss DECIMAL(16,4) NOT NULL,
            target DECIMAL(16,4) NOT NULL,
            position_type ENUM('long', 'short') NOT NULL,
            pnl DECIMAL(16,4) NOT NULL,
            ppnl DECIMAL(8,4) NOT NULL,
            trade_date DATE NOT NULL,
            nifty_direction VARCHAR(10) NOT NULL,
            volume_ratio DECIMAL(8,2) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
        """
        try:
            self.cursor.execute(create_table_query)
            self.conn.commit()
            logger.info("Trades table created or verified")
        except Error as e:
            logger.error(f"Error creating trades table: {e}")
            raise
    
    def fetch_symbol_data(self, symbol):
        """
        Fetch historical 5-minute data for a specific symbol.
        
        Args:
            symbol (str): Symbol to fetch data for
            
        Returns:
            pandas.DataFrame: OHLCV data with proper datetime index
        """
        query = """
        SELECT start_timestamp, open, high, low, close, volume 
        FROM market_data 
        WHERE symbol = %s AND duration = %s AND is_correct = 1
        AND date(start_timestamp) >= '2024-04-01'
        ORDER BY start_timestamp ASC;
        """
        
        try:
            logger.info(f"Fetching data for {symbol} with duration {self.duration}")
            self.cursor.execute(query, (symbol, self.duration))
            results = self.cursor.fetchall()
            
            if not results:
                logger.warning(f"No data found for {symbol} with duration {self.duration}")
                return pd.DataFrame()
            
            # Create DataFrame
            df = pd.DataFrame(results, columns=['start_timestamp', 'open', 'high', 'low', 'close', 'volume'])
            
            # Convert timestamps and set index
            df['start_timestamp'] = pd.to_datetime(df['start_timestamp'])
            df.set_index('start_timestamp', inplace=True)
            
            logger.info(f"Fetched {len(df)} data points for {symbol}")
            return df
            
        except Error as e:
            logger.error(f"Error fetching data for {symbol}: {e}")
            raise
    
    def calculate_nifty_trend(self, nifty_df, current_timestamp):
        """
        Calculate NIFTY50 trend direction at a specific timestamp.
        
        Args:
            nifty_df (pd.DataFrame): NIFTY50 historical data
            current_timestamp (datetime): Timestamp to check trend at
            
        Returns:
            str: 'uptrend', 'downtrend', or 'neutral'
        """
        # Get data up to current timestamp
        current_data = nifty_df[nifty_df.index <= current_timestamp].copy()
        
        if len(current_data) < 20:  # Need at least 20 bars for EMA
            return 'neutral'
        
        # Calculate 20-period EMA
        current_data['ema_20'] = current_data['close'].ewm(span=20, adjust=False).mean()
        
        # Get the most recent values
        latest_close = current_data.iloc[-1]['close']
        latest_ema = current_data.iloc[-1]['ema_20']
        
        # Also check recent momentum (last 5 bars)
        if len(current_data) >= 5:
            recent_closes = current_data['close'].tail(5).values
            momentum = (recent_closes[-1] - recent_closes[0]) / recent_closes[0] * 100
            
            # Strong uptrend conditions
            if latest_close > latest_ema and momentum > 0.1:
                return 'uptrend'
            # Strong downtrend conditions
            elif latest_close < latest_ema and momentum < -0.1:
                return 'downtrend'
        
        # Default trend determination
        if latest_close > latest_ema * 1.001:  # 0.1% buffer
            return 'uptrend'
        elif latest_close < latest_ema * 0.999:  # 0.1% buffer
            return 'downtrend'
        
        return 'neutral'
    
    def calculate_baseline_range(self, df, current_idx):
        """
        Calculate baseline range using first 6 bars (30 minutes) of the day.
        This avoids look-ahead bias by only using data available at the time.
        
        Args:
            df (pd.DataFrame): Historical data
            current_idx (int): Current index in the DataFrame
            
        Returns:
            tuple: (highest_high, lowest_low, avg_range, avg_volume) or (None, None, None, None) if insufficient data
        """
        current_time = df.index[current_idx].time()
        current_date = df.index[current_idx].date()
        
        # We need at least 6 bars (30 minutes) from market open
        if current_time < time(9, 50):  # 9:50 AM (6 bars after 9:20)
            return None, None, None, None
        
        # Get all data points for the current day up to current time
        day_data = df[df.index.date == current_date]
        
        # We need at least 6 bars for calculation
        if len(day_data) < 6:
            return None, None, None, None
        
        # Get first 6 bars (from 9:20 to 9:50)
        first_six_bars = day_data.iloc[:6]
        
        highest_high = first_six_bars['high'].max()
        lowest_low = first_six_bars['low'].min()
        avg_range = (first_six_bars['high'] - first_six_bars['low']).mean()
        avg_volume = first_six_bars['volume'].mean()
        
        return highest_high, lowest_low, avg_range, avg_volume
    
    def check_entry_conditions(self, df, nifty_df, current_idx, highest_high, lowest_low, avg_range, avg_volume):
        """
        Check if entry conditions are met for long or short position with NIFTY50 filtering.
        
        Args:
            df (pd.DataFrame): Stock historical data
            nifty_df (pd.DataFrame): NIFTY50 historical data
            current_idx (int): Current index in the DataFrame
            highest_high (float): Highest high from first 6 bars
            lowest_low (float): Lowest low from first 6 bars
            avg_range (float): Average range from first 6 bars
            avg_volume (float): Average volume from first 6 bars
            
        Returns:
            tuple: (position_type, entry_price, nifty_direction, volume_ratio) or (None, None, None, None) if no entry
        """
        current_row = df.iloc[current_idx]
        current_timestamp = df.index[current_idx]
        current_time = current_timestamp.time()
        
        # Don't enter before 9:20 AM
        if current_time < time(9, 20):
            return None, None, None, None
        
        # Don't enter after 2:30 PM to ensure exit by 3:15 PM
        if current_time > time(14, 30):
            return None, None, None, None
        
        # Calculate NIFTY50 trend at current timestamp
        nifty_direction = self.calculate_nifty_trend(nifty_df, current_timestamp)
        
        # Calculate entry thresholds with tighter parameters for higher quality trades
        long_threshold = highest_high + 0.3 * avg_range
        short_threshold = lowest_low - 0.3 * avg_range
        
        # Volume confirmation - must be 1.2x average volume
        volume_ratio = current_row['volume'] / avg_volume if avg_volume > 0 else 0
        
        # Long entry conditions: breakout + NIFTY uptrend + volume confirmation
        if (current_row['high'] > long_threshold and 
            # current_row['close'] > long_threshold and 
            nifty_direction == 'uptrend' and 
            volume_ratio > 1.2):
            
            entry_price = max(long_threshold, current_row['open'])
            logger.info(f"Long entry signal at {current_timestamp}: NIFTY={nifty_direction}, " 
                       f"Volume ratio={volume_ratio:.2f}, Threshold={long_threshold:.4f}")
            return 'long', entry_price, nifty_direction, volume_ratio
        
        # Short entry conditions: breakdown + NIFTY downtrend + volume confirmation  
        if (current_row['low'] < short_threshold and 
            current_row['close'] < short_threshold and 
            nifty_direction == 'downtrend' and 
            volume_ratio > 1.2):
            
            entry_price = min(short_threshold, current_row['open'])
            logger.info(f"Short entry signal at {current_timestamp}: NIFTY={nifty_direction}, " 
                       f"Volume ratio={volume_ratio:.2f}, Threshold={short_threshold:.4f}")
            return 'short', entry_price, nifty_direction, volume_ratio
        
        return None, None, None, None
    
    def simulate_trade(self, df, current_idx, position_type, entry_price, highest_high, lowest_low, avg_range, nifty_direction, volume_ratio):
        """
        Simulate a trade from entry to exit with enhanced parameters.
        
        Args:
            df (pd.DataFrame): Historical data
            current_idx (int): Current index (entry point)
            position_type (str): 'long' or 'short'
            entry_price (float): Entry price
            highest_high (float): Highest high from first 6 bars
            lowest_low (float): Lowest low from first 6 bars
            avg_range (float): Average range from first 6 bars
            nifty_direction (str): NIFTY50 direction at entry
            volume_ratio (float): Volume ratio at entry
            
        Returns:
            dict: Trade details including exit information
        """
        entry_timestamp = df.index[current_idx]
        current_date = entry_timestamp.date()
        
        # Calculate stop loss and target with improved risk-reward ratio
        if position_type == 'long':
            stoploss = entry_price - 1.2 * avg_range  # Tighter stop loss
            target = entry_price + 2.5 * avg_range   # Better reward
        else:  # short
            stoploss = entry_price + 1.2 * avg_range  # Tighter stop loss
            target = entry_price - 2.5 * avg_range    # Better reward
        
        exit_timestamp = None
        exit_price = None
        exit_reason = 'time'
        
        # Simulate trade bar by bar
        for i in range(current_idx + 1, len(df)):
            current_time = df.index[i].time()
            current_date_i = df.index[i].date()
            
            # Exit by 3:15 PM regardless of other conditions
            if current_time >= time(15, 15) or current_date_i != current_date:
                exit_timestamp = df.index[i]
                exit_price = df.iloc[i]['close']
                exit_reason = 'time'
                break
            
            row = df.iloc[i]
            
            # Check stop loss hit
            if position_type == 'long':
                if row['low'] <= stoploss:
                    exit_timestamp = df.index[i]
                    exit_price = min(stoploss, row['open'])  # Assume we get filled at stoploss
                    exit_reason = 'stoploss'
                    break
                # Check target hit
                if row['high'] >= target:
                    exit_timestamp = df.index[i]
                    exit_price = max(target, row['open'])
                    exit_reason = 'target'
                    break
            else:  # short
                if row['high'] >= stoploss:
                    exit_timestamp = df.index[i]
                    exit_price = max(stoploss, row['open'])
                    exit_reason = 'stoploss'
                    break
                # Check target hit
                if row['low'] <= target:
                    exit_timestamp = df.index[i]
                    exit_price = min(target, row['open'])
                    exit_reason = 'target'
                    break
        
        # If no exit found, use last available price (shouldn't happen due to time exit)
        if exit_timestamp is None:
            exit_timestamp = df.index[-1]
            exit_price = df.iloc[-1]['close']
            exit_reason = 'forced'
        
        # Calculate PnL
        if position_type == 'long':
            pnl = exit_price - entry_price
        else:  # short
            pnl = entry_price - exit_price
        
        ppnl = (pnl / entry_price) * 100
        
        trade = {
            'symbol': self.symbol,
            'entry_timestamp': entry_timestamp,
            'entry_price': round(entry_price, 4),
            'exit_timestamp': exit_timestamp,
            'exit_price': round(exit_price, 4),
            'stoploss': round(stoploss, 4),
            'target': round(target, 4),
            'position_type': position_type,
            'pnl': round(pnl, 4),
            'ppnl': round(ppnl, 4),
            'trade_date': current_date,
            'exit_reason': exit_reason,
            'nifty_direction': nifty_direction,
            'volume_ratio': round(volume_ratio, 2)
        }
        
        logger.info(f"Trade executed: {position_type} at {entry_timestamp}, "
                   f"NIFTY direction: {nifty_direction}, Volume ratio: {volume_ratio:.2f}, "
                   f"exit at {exit_timestamp}, ppnl: {ppnl:.4f}%, reason: {exit_reason}")
        
        return trade
    
    def backtest_strategy(self, df, nifty_df):
        """
        Main backtesting function that iterates through data and executes trades with NIFTY50 filtering.
        
        Args:
            df (pd.DataFrame): Stock historical data
            nifty_df (pd.DataFrame): NIFTY50 historical data
            
        Returns:
            list: List of all executed trades
        """
        if df.empty or nifty_df.empty:
            logger.warning("Insufficient data for backtesting")
            return []
        
        logger.info("Starting enhanced backtest with NIFTY50 filtering...")
        
        trades = []
        active_trade = False
        last_trade_date = None
        
        for i in range(len(df)):
            current_time = df.index[i].time()
            current_date = df.index[i].date()
            
            # Skip if before market hours or after market close
            if current_time < time(9, 0) or current_time > time(15, 30):
                continue
            
            # Reset active trade flag at new day
            if current_date != last_trade_date:
                active_trade = False
                last_trade_date = current_date
            
            # Skip if we already have an active trade for this day (avoid overtrading)
            if active_trade:
                continue
            
            # Calculate baseline range (first 30 minutes data)
            highest_high, lowest_low, avg_range, avg_volume = self.calculate_baseline_range(df, i)
            
            # Skip if insufficient data for baseline calculation
            if highest_high is None or lowest_low is None or avg_range is None or avg_volume is None:
                continue
            
            # Check entry conditions with NIFTY50 filtering
            position_type, entry_price, nifty_direction, volume_ratio = self.check_entry_conditions(
                df, nifty_df, i, highest_high, lowest_low, avg_range, avg_volume
            )
            
            # Execute trade if conditions met
            if position_type and entry_price:
                trade = self.simulate_trade(
                    df, i, position_type, entry_price, highest_high, lowest_low, 
                    avg_range, nifty_direction, volume_ratio
                )
                trades.append(trade)
                active_trade = True  # Mark trade as active for this day
        
        logger.info(f"Enhanced backtest completed. Total trades: {len(trades)}")
        return trades
    
    def save_trades_to_database(self, trades):
        """Save executed trades to database."""
        if not trades:
            logger.warning("No trades to save to database")
            return
        
        insert_query = """
        INSERT INTO trades (
            symbol, entry_timestamp, entry_price, exit_timestamp, exit_price,
            stoploss, target, position_type, pnl, ppnl, trade_date,
            nifty_direction, volume_ratio
        ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);
        """
        
        try:
            for trade in trades:
                self.cursor.execute(insert_query, (
                    trade['symbol'],
                    trade['entry_timestamp'],
                    trade['entry_price'],
                    trade['exit_timestamp'],
                    trade['exit_price'],
                    trade['stoploss'],
                    trade['target'],
                    trade['position_type'],
                    trade['pnl'],
                    trade['ppnl'],
                    trade['trade_date'],
                    trade['nifty_direction'],
                    trade['volume_ratio']
                ))
            self.conn.commit()
            logger.info(f"Successfully saved {len(trades)} trades to database")
        except Error as e:
            logger.error(f"Error saving trades to database: {e}")
            self.conn.rollback()
            raise
    
    def calculate_performance_metrics(self, trades):
        """
        Calculate performance metrics from executed trades.
        
        Args:
            trades (list): List of trade dictionaries
            
        Returns:
            dict: Performance metrics
        """
        if not trades:
            return {
                'total_trades': 0,
                'winning_trades': 0,
                'win_rate': 0.0,
                'avg_ppnl': 0.0,
                'max_win': 0.0,
                'max_loss': 0.0,
                'total_pnl': 0.0,
                'avg_volume_ratio': 0.0,
                'nifty_aligned_trades': 0
            }
        
        ppnls = [trade['ppnl'] for trade in trades]
        winning_trades = sum(1 for ppnl in ppnls if ppnl > 0)
        total_trades = len(trades)
        
        # Calculate NIFTY alignment success
        nifty_aligned_trades = sum(1 for trade in trades 
                                 if (trade['position_type'] == 'long' and trade['nifty_direction'] == 'uptrend') or
                                    (trade['position_type'] == 'short' and trade['nifty_direction'] == 'downtrend'))
        
        metrics = {
            'total_trades': total_trades,
            'winning_trades': winning_trades,
            'win_rate': (winning_trades / total_trades) * 100 if total_trades > 0 else 0,
            'avg_ppnl': sum(ppnls) / total_trades,
            'max_win': max(ppnls),
            'max_loss': min(ppnls),
            'total_pnl': sum(trade['pnl'] for trade in trades),
            'avg_volume_ratio': sum(trade['volume_ratio'] for trade in trades) / total_trades,
            'nifty_aligned_trades': (nifty_aligned_trades / total_trades) * 100 if total_trades > 0 else 0
        }
        
        return metrics
    
    def print_performance_report(self, metrics):
        """Print formatted performance report."""
        print("\n" + "="*70)
        print("ENHANCED INTRADAY TRADING STRATEGY PERFORMANCE REPORT")
        print("="*70)
        print(f"Total Trades: {metrics['total_trades']}")
        print(f"Winning Trades: {metrics['winning_trades']}")
        print(f"Win Rate: {metrics['win_rate']:.2f}%")
        print(f"Average PPNL: {metrics['avg_ppnl']:.4f}%")
        print(f"Maximum Win: {metrics['max_win']:.4f}%")
        print(f"Maximum Loss: {metrics['max_loss']:.4f}%")
        print(f"Average Volume Ratio: {metrics['avg_volume_ratio']:.2f}x")
        print(f"NIFTY Aligned Trades: {metrics['nifty_aligned_trades']:.2f}%")
        print("="*70)
        
        # Check if targets are met
        win_rate_target = 50 <= metrics['win_rate'] <= 60
        avg_ppnl_target = 0.5 <= metrics['avg_ppnl'] <= 0.6
        
        print("\nTARGET ASSESSMENT:")
        print(f"Win Rate Target (50-60%): {'✓ MET' if win_rate_target else '✗ NOT MET'} ({metrics['win_rate']:.2f}%)")
        print(f"Avg PPNL Target (0.5-0.6%): {'✓ MET' if avg_ppnl_target else '✗ NOT MET'} ({metrics['avg_ppnl']:.4f}%)")
        
        if win_rate_target and avg_ppnl_target:
            print("\nSTRATEGY PERFORMANCE: EXCELLENT")
            print("The enhanced strategy with NIFTY50 filtering and volume confirmation")
            print("is meeting the target performance metrics. Key improvements:")
            print("- Market context filtering improves trade quality")
            print("- Tighter stop losses with better reward ratios")
            print("- Volume confirmation ensures institutional participation")
        else:
            print("\nSTRATEGY OPTIMIZATION RECOMMENDATIONS:")
            if not win_rate_target:
                print("- Adjust NIFTY50 EMA period (currently 20) for better trend detection")
                print("- Consider adding RSI filter for overbought/oversold conditions")
                print("- Review volume ratio threshold (currently 1.2x)")
            if not avg_ppnl_target:
                print("- Optimize position sizing based on volatility")
                print("- Consider trailing stop losses for better profit capture")
                print("- Review target multiplier (currently 2.5x avg_range)")
        print("="*70)
    
    def run_backtest(self):
        """Main function to run the complete backtest."""
        try:
            # Connect to database
            if not self.connect_to_database():
                return
            
            # Create trades table
            self.create_trades_table()
            
            # Fetch historical data for both stock and NIFTY50
            stock_df = self.fetch_symbol_data(self.symbol)
            nifty_df = self.fetch_symbol_data(self.nifty_symbol)
            
            if stock_df.empty or nifty_df.empty:
                logger.warning("Insufficient data available for backtesting")
                return
            
            # Align the dataframes to ensure we have NIFTY50 data for all stock timestamps
            # This is crucial to avoid look-ahead bias
            common_index = stock_df.index.intersection(nifty_df.index)
            if len(common_index) < len(stock_df) * 0.8:  # At least 80% overlap
                logger.warning("Insufficient NIFTY50 data coverage for stock timestamps")
                return
            
            stock_df = stock_df.loc[common_index]
            nifty_df = nifty_df.loc[common_index]
            
            logger.info(f"Data alignment complete. Common timestamps: {len(common_index)}")
            
            # Run backtest with NIFTY50 filtering
            trades = self.backtest_strategy(stock_df, nifty_df)
            
            if not trades:
                logger.warning("No trades were executed during backtest")
                return
            
            # Save trades to database
            self.save_trades_to_database(trades)
            
            # Calculate and display performance
            metrics = self.calculate_performance_metrics(trades)
            self.print_performance_report(metrics)
            
            # Store trades for external access
            self.trades = trades
            
        except Exception as e:
            logger.error(f"Backtest failed: {e}", exc_info=True)
            raise
        finally:
            self.disconnect_from_database()

def main():
    """Main entry point for the script."""
    # Initialize enhanced backtester for AXISBANK with NIFTY50 filtering
    backtester = EnhancedIntradayBacktester(
        symbol='AXISBANK', 
        nifty_symbol='NIFTY50', 
        duration='5minute'
    )
    
    # Run the enhanced backtest
    backtester.run_backtest()

if __name__ == "__main__":
    main()
