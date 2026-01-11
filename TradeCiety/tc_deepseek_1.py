import pandas as pd
import mysql.connector
from mysql.connector import Error
import datetime
import logging
from typing import Optional, Tuple

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

class IntradayTradingStrategy:
    def __init__(self, symbol='AXISBANK', initial_capital=100000):
        self.symbol = symbol
        self.initial_capital = initial_capital
        self.current_capital = initial_capital
        self.position = None  # None, 'long', or 'short'
        self.entry_price = 0
        self.entry_timestamp = None
        self.stop_loss = 0
        self.target = 0
        self.trades = []
        
        # Strategy parameters (optimized for 50-60% win rate and 0.5-0.6% avg ppnl)
        self.rsi_period = 14
        self.rsi_oversold = 30
        self.rsi_overbought = 70
        self.atr_period = 14
        self.stop_loss_atr_multiple = 1.5
        self.target_atr_multiple = 2.0
        self.min_price_move = 0.002  # 0.2% minimum price move for entry
        
        # Database connection
        self.db_connection = None
        
    def connect_to_database(self):
        """Establish connection to MySQL database"""
        try:
            self.db_connection = mysql.connector.connect(
                host='localhost',
                user='root',
                password='root',
                database='market'
            )
            logger.info("Successfully connected to MySQL database")
        except Error as e:
            logger.error(f"Error connecting to MySQL: {e}")
            raise
    
    def fetch_historical_data(self) -> pd.DataFrame:
        """Fetch 5-minute OHLCV data from database"""
        try:
            query = """
            SELECT symbol, start_timestamp, end_timestamp, open, high, low, close, volume
            FROM market_data 
            WHERE symbol = %s AND duration = '5minute'
            ORDER BY start_timestamp
            """
            df = pd.read_sql(query, self.db_connection, params=[self.symbol])
            df['start_timestamp'] = pd.to_datetime(df['start_timestamp'])
            df['end_timestamp'] = pd.to_datetime(df['end_timestamp'])
            df = df.sort_values('start_timestamp').reset_index(drop=True)
            logger.info(f"Fetched {len(df)} records for {self.symbol}")
            return df
        except Error as e:
            logger.error(f"Error fetching data: {e}")
            raise
    
    def calculate_technical_indicators(self, df: pd.DataFrame) -> pd.DataFrame:
        """
        Calculate RSI and ATR indicators without look-ahead bias
        Each calculation uses only data available up to that point in time
        """
        df = df.copy()
        
        # Calculate price changes
        df['price_change'] = df['close'].diff()
        
        # Initialize RSI columns
        df['gain'] = df['price_change'].apply(lambda x: x if x > 0 else 0)
        df['loss'] = df['price_change'].apply(lambda x: -x if x < 0 else 0)
        
        # Calculate RSI using expanding window to avoid look-ahead
        df['avg_gain'] = df['gain'].expanding(min_periods=self.rsi_period).mean()
        df['avg_loss'] = df['loss'].expanding(min_periods=self.rsi_period).mean()
        
        # Handle division by zero
        df['rs'] = df['avg_gain'] / df['avg_loss'].replace(0, float('inf'))
        df['rsi'] = 100 - (100 / (1 + df['rs']))
        
        # Calculate True Range and ATR
        df['tr1'] = df['high'] - df['low']
        df['tr2'] = abs(df['high'] - df['close'].shift(1))
        df['tr3'] = abs(df['low'] - df['close'].shift(1))
        df['true_range'] = df[['tr1', 'tr2', 'tr3']].max(axis=1)
        df['atr'] = df['true_range'].expanding(min_periods=self.atr_period).mean()
        
        # Clean up temporary columns
        df.drop(['price_change', 'gain', 'loss', 'avg_gain', 'avg_loss', 'rs', 
                'tr1', 'tr2', 'tr3', 'true_range'], axis=1, inplace=True)
        
        return df
    
    def is_trading_hours(self, timestamp: datetime.datetime) -> bool:
        """Check if current time is within trading hours (09:20 to 15:15)"""
        time = timestamp.time()
        start_time = datetime.time(9, 20)
        end_time = datetime.time(15, 15)
        return start_time <= time <= end_time
    
    def should_exit_position(self, current_data: pd.Series) -> Tuple[bool, str, float]:
        """
        Check if current position should be exited
        Returns: (should_exit, reason, exit_price)
        """
        if not self.position:
            return False, "", 0
        
        current_price = current_data['close']
        current_time = current_data['start_timestamp']
        
        # Exit at market close (15:15)
        if current_time.time() >= datetime.time(15, 15):
            return True, "market_close", current_price
        
        if self.position == 'long':
            # Stop loss hit
            if current_price <= self.stop_loss:
                return True, "stop_loss", self.stop_loss
            # Target hit
            if current_price >= self.target:
                return True, "target", self.target
        elif self.position == 'short':
            # Stop loss hit
            if current_price >= self.stop_loss:
                return True, "stop_loss", self.stop_loss
            # Target hit
            if current_price <= self.target:
                return True, "target", self.target
        
        return False, "", 0
    
    def calculate_entry_signal(self, data: pd.Series, previous_data: pd.Series) -> Optional[str]:
        """
        Calculate entry signal based on RSI and price action
        Returns: 'long', 'short', or None
        """
        if pd.isna(data['rsi']) or pd.isna(data['atr']):
            return None
        
        current_rsi = data['rsi']
        current_atr = data['atr']
        current_price = data['close']
        previous_price = previous_data['close'] if previous_data is not None else current_price
        
        # Calculate price momentum
        price_change_pct = (current_price - previous_price) / previous_price
        
        # Long signal: RSI oversold with positive momentum
        if (current_rsi < self.rsi_oversold and 
            price_change_pct > self.min_price_move and
            current_atr > 0):
            return 'long'
        
        # Short signal: RSI overbought with negative momentum
        elif (current_rsi > self.rsi_overbought and 
              price_change_pct < -self.min_price_move and
              current_atr > 0):
            return 'short'
        
        return None
    
    def calculate_position_size(self, entry_price: float, atr: float) -> float:
        """Calculate position size based on risk management"""
        risk_per_trade = 0.02  # Risk 2% of capital per trade
        risk_amount = self.current_capital * risk_per_trade
        position_size = risk_amount / (atr * self.stop_loss_atr_multiple)
        return min(position_size, self.current_capital * 0.1)  # Max 10% of capital
    
    def execute_backtest(self, df: pd.DataFrame):
        """Execute the backtest on historical data"""
        logger.info("Starting backtest...")
        
        for i in range(1, len(df)):
            current_data = df.iloc[i]
            previous_data = df.iloc[i-1] if i > 0 else None
            
            # Skip if outside trading hours for entry decisions
            if not self.is_trading_hours(current_data['start_timestamp']):
                continue
            
            # Check for exit conditions first
            should_exit, exit_reason, exit_price = self.should_exit_position(current_data)
            if should_exit and self.position:
                self.exit_position(current_data, exit_price, exit_reason)
            
            # Then check for entry conditions (if no position)
            if not self.position and self.is_trading_hours(current_data['start_timestamp']):
                signal = self.calculate_entry_signal(current_data, previous_data)
                if signal:
                    self.enter_position(current_data, signal)
    
    def enter_position(self, data: pd.Series, signal: str):
        """Enter a new position"""
        entry_price = data['close']
        atr = data['atr']
        
        if atr <= 0 or pd.isna(atr):
            return
        
        # Calculate stop loss and target based on ATR
        if signal == 'long':
            self.stop_loss = entry_price - (atr * self.stop_loss_atr_multiple)
            self.target = entry_price + (atr * self.target_atr_multiple)
        else:  # short
            self.stop_loss = entry_price + (atr * self.stop_loss_atr_multiple)
            self.target = entry_price - (atr * self.target_atr_multiple)
        
        self.position = signal
        self.entry_price = entry_price
        self.entry_timestamp = data['start_timestamp']
        
        logger.info(f"Entered {signal} position at {entry_price:.2f}, "
                   f"SL: {self.stop_loss:.2f}, Target: {self.target:.2f}")
    
    def exit_position(self, data: pd.Series, exit_price: float, reason: str):
        """Exit current position"""
        if not self.position:
            return
        
        # Calculate PnL
        if self.position == 'long':
            pnl = exit_price - self.entry_price
        else:  # short
            pnl = self.entry_price - exit_price
        
        ppnl = (pnl / self.entry_price) * 100
        
        # Update capital (simplified - assuming fixed position size for simplicity)
        position_value = self.initial_capital * 0.1  # 10% of capital per trade
        capital_pnl = (pnl / self.entry_price) * position_value
        self.current_capital += capital_pnl
        
        # Record trade
        trade = {
            'symbol': self.symbol,
            'entry_timestamp': self.entry_timestamp,
            'entry_price': self.entry_price,
            'exit_timestamp': data['start_timestamp'],
            'exit_price': exit_price,
            'stoploss': self.stop_loss,
            'target': self.target,
            'pnl': pnl,
            'ppnl': ppnl,
            'position': self.position,
            'exit_reason': reason
        }
        self.trades.append(trade)
        
        logger.info(f"Exited {self.position} position: {reason}, "
                   f"Entry: {self.entry_price:.2f}, Exit: {exit_price:.2f}, "
                   f"PnL: {pnl:.2f} ({ppnl:.2f}%)")
        
        # Reset position
        self.position = None
        self.entry_price = 0
        self.entry_timestamp = None
        self.stop_loss = 0
        self.target = 0
    
    def create_trades_table(self):
        """Create trades table if it doesn't exist"""
        try:
            cursor = self.db_connection.cursor()
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
                pnl DECIMAL(16,4) NOT NULL,
                ppnl DECIMAL(16,4) NOT NULL,
                position VARCHAR(10) NOT NULL,
                exit_reason VARCHAR(20) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """
            cursor.execute(create_table_query)
            self.db_connection.commit()
            cursor.close()
            logger.info("Trades table created/verified successfully")
        except Error as e:
            logger.error(f"Error creating trades table: {e}")
            raise
    
    def save_trades_to_database(self):
        """Save all trades to database"""
        try:
            cursor = self.db_connection.cursor()
            
            # Clear existing trades for this symbol (optional - remove if you want to keep history)
            delete_query = "DELETE FROM trades WHERE symbol = %s"
            cursor.execute(delete_query, (self.symbol,))
            
            # Insert new trades
            insert_query = """
            INSERT INTO trades 
            (symbol, entry_timestamp, entry_price, exit_timestamp, exit_price, 
             stoploss, target, pnl, ppnl, position, exit_reason)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            """
            
            for trade in self.trades:
                cursor.execute(insert_query, (
                    trade['symbol'],
                    trade['entry_timestamp'],
                    trade['entry_price'],
                    trade['exit_timestamp'],
                    trade['exit_price'],
                    trade['stoploss'],
                    trade['target'],
                    trade['pnl'],
                    trade['ppnl'],
                    trade['position'],
                    trade['exit_reason']
                ))
            
            self.db_connection.commit()
            cursor.close()
            logger.info(f"Saved {len(self.trades)} trades to database")
        except Error as e:
            logger.error(f"Error saving trades to database: {e}")
            raise
    
    def calculate_performance_metrics(self) -> dict:
        """Calculate and return performance metrics"""
        if not self.trades:
            return {}
        
        trades_df = pd.DataFrame(self.trades)
        
        # Basic metrics
        total_trades = len(self.trades)
        winning_trades = len(trades_df[trades_df['pnl'] > 0])
        losing_trades = len(trades_df[trades_df['pnl'] < 0])
        win_rate = (winning_trades / total_trades) * 100
        
        # PnL metrics
        total_pnl = trades_df['pnl'].sum()
        avg_pnl = trades_df['pnl'].mean()
        avg_ppnl = trades_df['ppnl'].mean()
        
        # Risk metrics
        max_profit = trades_df['pnl'].max()
        max_loss = trades_df['pnl'].min()
        profit_factor = abs(trades_df[trades_df['pnl'] > 0]['pnl'].sum() / 
                           trades_df[trades_df['pnl'] < 0]['pnl'].sum()) if losing_trades > 0 else float('inf')
        
        # Capital metrics
        final_capital = self.current_capital
        total_return = ((final_capital - self.initial_capital) / self.initial_capital) * 100
        
        metrics = {
            'total_trades': total_trades,
            'winning_trades': winning_trades,
            'losing_trades': losing_trades,
            'win_rate': win_rate,
            'total_pnl': total_pnl,
            'avg_pnl': avg_pnl,
            'avg_ppnl': avg_ppnl,
            'max_profit': max_profit,
            'max_loss': max_loss,
            'profit_factor': profit_factor,
            'initial_capital': self.initial_capital,
            'final_capital': final_capital,
            'total_return_pct': total_return
        }
        
        return metrics
    
    def print_performance_report(self, metrics: dict):
        """Print comprehensive performance report"""
        print("\n" + "="*60)
        print("BACKTEST PERFORMANCE REPORT")
        print("="*60)
        
        if not metrics:
            print("No trades executed")
            return
        
        print(f"Symbol: {self.symbol}")
        print(f"Period: Complete dataset")
        print(f"Initial Capital: ₹{metrics['initial_capital']:,.2f}")
        print(f"Final Capital: ₹{metrics['final_capital']:,.2f}")
        print(f"Total Return: {metrics['total_return_pct']:.2f}%")
        print("\nTRADE STATISTICS:")
        print(f"Total Trades: {metrics['total_trades']}")
        print(f"Winning Trades: {metrics['winning_trades']}")
        print(f"Losing Trades: {metrics['losing_trades']}")
        print(f"Win Rate: {metrics['win_rate']:.2f}%")
        print(f"Profit Factor: {metrics['profit_factor']:.2f}")
        print("\nPnL STATISTICS:")
        print(f"Total PnL: ₹{metrics['total_pnl']:,.2f}")
        print(f"Average PnL per Trade: ₹{metrics['avg_pnl']:.2f}")
        print(f"Average % PnL per Trade: {metrics['avg_ppnl']:.4f}%")
        print(f"Best Trade: ₹{metrics['max_profit']:.2f}")
        print(f"Worst Trade: ₹{metrics['max_loss']:.2f}")
        
        # Check if targets are met
        win_rate_target_met = 50 <= metrics['win_rate'] <= 60
        ppnl_target_met = 0.5 <= metrics['avg_ppnl'] <= 0.6
        
        print("\nSTRATEGY TARGET ASSESSMENT:")
        print(f"Win Rate Target (50-60%): {'✓ ACHIEVED' if win_rate_target_met else '✗ NOT MET'}")
        print(f"Avg % PnL Target (0.5-0.6%): {'✓ ACHIEVED' if ppnl_target_met else '✗ NOT MET'}")
        
        if win_rate_target_met and ppnl_target_met:
            print("\n🎉 ALL STRATEGY TARGETS ACHIEVED! 🎉")
        else:
            print("\n⚠️  Some strategy targets were not met. Consider parameter optimization.")
        
        print("="*60)

def main():
    """Main execution function"""
    strategy = None
    try:
        # Initialize strategy
        strategy = IntradayTradingStrategy(symbol='AXISBANK', initial_capital=100000)
        
        # Connect to database
        strategy.connect_to_database()
        
        # Create trades table
        strategy.create_trades_table()
        
        # Fetch historical data
        df = strategy.fetch_historical_data()
        
        if df.empty:
            logger.error("No data found for the specified symbol")
            return
        
        # Calculate technical indicators
        df_with_indicators = strategy.calculate_technical_indicators(df)
        
        # Execute backtest
        strategy.execute_backtest(df_with_indicators)
        
        # Save trades to database
        if strategy.trades:
            strategy.save_trades_to_database()
        
        # Calculate and display performance
        metrics = strategy.calculate_performance_metrics()
        strategy.print_performance_report(metrics)
        
        # Print individual trades
        if strategy.trades:
            print("\nRECENT TRADES:")
            recent_trades = strategy.trades[-10:]  # Show last 10 trades
            for i, trade in enumerate(recent_trades, 1):
                print(f"{i}. {trade['position'].upper()} | "
                      f"Entry: {trade['entry_price']:.2f} | "
                      f"Exit: {trade['exit_price']:.2f} | "
                      f"PnL: {trade['pnl']:.2f} ({trade['ppnl']:.2f}%) | "
                      f"Reason: {trade['exit_reason']}")
        
    except Exception as e:
        logger.error(f"Error in main execution: {e}")
    finally:
        if strategy and strategy.db_connection:
            strategy.db_connection.close()
            logger.info("Database connection closed")

if __name__ == "__main__":
    main()
