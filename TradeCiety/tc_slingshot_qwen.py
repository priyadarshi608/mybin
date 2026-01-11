#!/usr/bin/env python3
"""
Intraday Trading Strategy Backtester
-------------------------------------
This script implements and backtests an intraday trading strategy on 5-minute data.
It avoids look-ahead bias by only using data available up to each point in time.

Strategy Overview:
- Uses first 30 minutes (6 bars) to establish baseline range
- Long entry: Price breaks above highest high of first 6 bars + 0.5 * average range
- Short entry: Price breaks below lowest low of first 6 bars - 0.5 * average range
- Stop loss: 1.5 * average range from entry
- Take profit: 2.0 * average range from entry
- Maximum one trade per day to avoid overtrading
- Entry time: After 09:20:00, Exit time: By 15:15:00

Target Performance:
- Win rate: 50-60%
- Average ppnl: 0.5-0.6%

Database Schema Requirements:
- Source table: market_data (with OHLCV data)
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
        logging.FileHandler('intraday_backtest.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

class IntradayBacktester:
    def __init__(self, symbol='AXISBANK', duration='5minute'):
        """
        Initialize the backtester with database connection and parameters.
        
        Args:
            symbol (str): Trading symbol to backtest
            duration (str): Time duration for data (e.g., '5minute')
        """
        self.symbol = symbol
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
    
    def fetch_historical_data(self):
        """
        Fetch historical 5-minute data for the specified symbol and duration.
        
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
            logger.info(f"Fetching data for {self.symbol} with duration {self.duration}")
            self.cursor.execute(query, (self.symbol, self.duration))
            results = self.cursor.fetchall()
            
            if not results:
                logger.warning(f"No data found for {self.symbol} with duration {self.duration}")
                return pd.DataFrame()
            
            # Create DataFrame
            df = pd.DataFrame(results, columns=['start_timestamp', 'open', 'high', 'low', 'close', 'volume'])
            
            # Convert timestamps and set index
            df['start_timestamp'] = pd.to_datetime(df['start_timestamp'])
            df.set_index('start_timestamp', inplace=True)
            
            logger.info(f"Fetched {len(df)} data points")
            return df
            
        except Error as e:
            logger.error(f"Error fetching historical data: {e}")
            raise
    
    def calculate_baseline_range(self, df, current_idx):
        """
        Calculate baseline range using first 6 bars (30 minutes) of the day.
        This avoids look-ahead bias by only using data available at the time.
        
        Args:
            df (pd.DataFrame): Historical data
            current_idx (int): Current index in the DataFrame
            
        Returns:
            tuple: (highest_high, lowest_low, avg_range) or (None, None, None) if insufficient data
        """
        current_time = df.index[current_idx].time()
        current_date = df.index[current_idx].date()
        
        # We need at least 6 bars (30 minutes) from market open
        if current_time < time(9, 50):  # 9:50 AM (6 bars after 9:20)
            return None, None, None
        
        # Get all data points for the current day up to current time
        day_data = df[df.index.date == current_date]
        
        # We need at least 6 bars for calculation
        if len(day_data) < 6:
            return None, None, None
        
        # Get first 6 bars (from 9:20 to 9:50)
        first_six_bars = day_data.iloc[:6]
        
        highest_high = first_six_bars['high'].max()
        lowest_low = first_six_bars['low'].min()
        avg_range = (first_six_bars['high'] - first_six_bars['low']).mean()
        
        return highest_high, lowest_low, avg_range
    
    def check_entry_conditions(self, df, current_idx, highest_high, lowest_low, avg_range):
        """
        Check if entry conditions are met for long or short position.
        
        Args:
            df (pd.DataFrame): Historical data
            current_idx (int): Current index in the DataFrame
            highest_high (float): Highest high from first 6 bars
            lowest_low (float): Lowest low from first 6 bars
            avg_range (float): Average range from first 6 bars
            
        Returns:
            tuple: (position_type, entry_price) or (None, None) if no entry
        """
        current_row = df.iloc[current_idx]
        current_time = df.index[current_idx].time()
        
        # Don't enter before 9:20 AM
        if current_time < time(9, 20):
            return None, None
        
        # Don't enter after 2:30 PM to ensure exit by 3:15 PM
        if current_time > time(14, 30):
            return None, None
        
        # Calculate entry thresholds
        long_threshold = highest_high + 0.5 * avg_range
        short_threshold = lowest_low - 0.5 * avg_range
        
        # Check long entry: breakout above threshold
        if current_row['high'] > long_threshold and current_row['close'] > long_threshold:
            return 'long', max(long_threshold, current_row['close'])
        
        # Check short entry: breakout below threshold
        if current_row['low'] < short_threshold and current_row['close'] < short_threshold:
            return 'short', min(short_threshold, current_row['close'])
        
        return None, None
    
    def simulate_trade(self, df, current_idx, position_type, entry_price, highest_high, lowest_low, avg_range):
        """
        Simulate a trade from entry to exit.
        
        Args:
            df (pd.DataFrame): Historical data
            current_idx (int): Current index (entry point)
            position_type (str): 'long' or 'short'
            entry_price (float): Entry price
            highest_high (float): Highest high from first 6 bars
            lowest_low (float): Lowest low from first 6 bars
            avg_range (float): Average range from first 6 bars
            
        Returns:
            dict: Trade details including exit information
        """
        entry_timestamp = df.index[current_idx]
        current_date = entry_timestamp.date()
        
        # Calculate stop loss and target based on position type
        if position_type == 'long':
            stoploss = entry_price - 1.5 * avg_range
            target = entry_price + 2.0 * avg_range
        else:  # short
            stoploss = entry_price + 1.5 * avg_range
            target = entry_price - 2.0 * avg_range
        
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
            'exit_reason': exit_reason
        }
        
        logger.info(f"Trade executed: {position_type} at {entry_timestamp}, "
                   f"exit at {exit_timestamp}, ppnl: {ppnl:.4f}%, reason: {exit_reason}")
        
        return trade
    
    def backtest_strategy(self, df):
        """
        Main backtesting function that iterates through data and executes trades.
        
        Args:
            df (pd.DataFrame): Historical OHLCV data
            
        Returns:
            list: List of all executed trades
        """
        if df.empty:
            logger.warning("No data to backtest")
            return []
        
        logger.info("Starting backtest...")
        
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
            highest_high, lowest_low, avg_range = self.calculate_baseline_range(df, i)
            
            # Skip if insufficient data for baseline calculation
            if highest_high is None or lowest_low is None or avg_range is None:
                continue
            
            # Check entry conditions
            position_type, entry_price = self.check_entry_conditions(
                df, i, highest_high, lowest_low, avg_range
            )
            
            # Execute trade if conditions met
            if position_type and entry_price:
                trade = self.simulate_trade(
                    df, i + 1, position_type, entry_price, highest_high, lowest_low, avg_range
                )
                trades.append(trade)
                active_trade = True  # Mark trade as active for this day
        
        logger.info(f"Backtest completed. Total trades: {len(trades)}")
        return trades
    
    def save_trades_to_database(self, trades):
        """Save executed trades to database."""
        if not trades:
            logger.warning("No trades to save to database")
            return
        
        insert_query = """
        INSERT INTO trades (
            symbol, entry_timestamp, entry_price, exit_timestamp, exit_price,
            stoploss, target, position_type, pnl, ppnl, trade_date
        ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);
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
                    trade['trade_date']
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
                'total_pnl': 0.0
            }
        
        ppnls = [trade['ppnl'] for trade in trades]
        winning_trades = sum(1 for ppnl in ppnls if ppnl > 0)
        total_trades = len(trades)
        
        metrics = {
            'total_trades': total_trades,
            'winning_trades': winning_trades,
            'win_rate': (winning_trades / total_trades) * 100 if total_trades > 0 else 0,
            'avg_ppnl': sum(ppnls) / total_trades,
            'max_win': max(ppnls),
            'max_loss': min(ppnls),
            'total_pnl': sum(trade['pnl'] for trade in trades)
        }
        
        return metrics
    
    def print_performance_report(self, metrics):
        """Print formatted performance report."""
        print("\n" + "="*60)
        print("INSTRADAY TRADING STRATEGY PERFORMANCE REPORT")
        print("="*60)
        print(f"Total Trades: {metrics['total_trades']}")
        print(f"Winning Trades: {metrics['winning_trades']}")
        print(f"Win Rate: {metrics['win_rate']:.2f}%")
        print(f"Average PPNL: {metrics['avg_ppnl']:.4f}%")
        print(f"Maximum Win: {metrics['max_win']:.4f}%")
        print(f"Maximum Loss: {metrics['max_loss']:.4f}%")
        print(f"Total PNL: {metrics['total_pnl']:.4f}")
        print("="*60)
        
        # Check if targets are met
        win_rate_target = 50 <= metrics['win_rate'] <= 60
        avg_ppnl_target = 0.5 <= metrics['avg_ppnl'] <= 0.6
        
        print("\nTARGET ASSESSMENT:")
        print(f"Win Rate Target (50-60%): {'✓ MET' if win_rate_target else '✗ NOT MET'} ({metrics['win_rate']:.2f}%)")
        print(f"Avg PPNL Target (0.5-0.6%): {'✓ MET' if avg_ppnl_target else '✗ NOT MET'} ({metrics['avg_ppnl']:.4f}%)")
        
        if not (win_rate_target and avg_ppnl_target):
            print("\nSTRATEGY OPTIMIZATION SUGGESTED:")
            if not win_rate_target:
                print("- Consider adjusting breakout thresholds (0.5 multiplier)")
                print("- Review stop loss distance (1.5 * avg_range)")
            if not avg_ppnl_target:
                print("- Consider adjusting risk-reward ratio (currently 1.5:2.0)")
                print("- Review position sizing or entry timing constraints")
        print("="*60)
    
    def run_backtest(self):
        """Main function to run the complete backtest."""
        try:
            # Connect to database
            if not self.connect_to_database():
                return
            
            # Create trades table
            self.create_trades_table()
            
            # Fetch historical data
            df = self.fetch_historical_data()
            
            if df.empty:
                logger.warning("No data available for backtesting")
                return
            
            # Run backtest
            trades = self.backtest_strategy(df)
            
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
    # Initialize backtester for AXISBANK
    backtester = IntradayBacktester(symbol='AXISBANK', duration='5minute')
    
    # Run the backtest
    backtester.run_backtest()

if __name__ == "__main__":
    main()
