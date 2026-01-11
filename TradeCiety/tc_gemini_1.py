#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
Production-Ready Intraday Backtesting Script (v2 - Improved)

This script performs a rigorous, "point-in-time" backtest of an intraday
trading strategy, ensuring no look-ahead bias.

Strategy Implemented (v2):
- **Primary Trend Filter:** VWAP (Volume Weighted Average Price).
    - LONG if price > VWAP, SHORT if price < VWAP.
- **Trend Strength Filter:** ADX (Average Directional Index)
    - Only take trades if ADX > 20 (i.e., a trend is active).
- **Trend Direction Filter:** Directional Movement Index (+DI / -DI)
    - LONG: +DI must be greater than -DI.
    - SHORT: -DI must be greater than +DI.
- **Entry Signal:** RSI (Relative Strength Index) Pullback.
    - LONG: When all above conditions are met, wait for RSI to dip below 30
      and then cross back above 30 (buying a deep dip in a strong uptrend).
    - SHORT: When all above conditions are met, wait for RSI to peak above 70
      and then cross back below 70 (selling a sharp rally in a strong downtrend).
- **Risk Management:**
    - Take Profit (TP): 0.75%
    - Stop Loss (SL): 0.5%
    - Risk/Reward Ratio: 1.5:1
- **Trading Window:**
    - No entries before 09:20:00.
    - All positions are forcibly closed at the open of the 15:15:00 bar.
    - Only one trade is open at a time.

Required Libraries:
- mysql-connector-python
- pandas
- pandas_ta (for technical analysis indicators)

Install them using:
pip install mysql-connector-python pandas pandas_ta
"""

import mysql.connector
import pandas as pd
import pandas_ta as ta
import datetime
from decimal import Decimal, ROUND_DOWN

# --- Configuration ---

# Database Connection
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': 'root',
    'database': 'market'
}

# Backtest Parameters
TARGET_SYMBOL = 'AXISBANK'
TARGET_DURATION = '5minute'
TRADES_TABLE_NAME = 'trades_log_v2' # Using a new table for v2 results

# Strategy Parameters
RSI_PERIOD = 14
ADX_PERIOD = 14
RSI_OVERSOLD_LEVEL = 30   # Changed from 40 for deeper pullbacks
RSI_OVERBOUGHT_LEVEL = 70  # Changed from 60 for sharper rallies
ADX_THRESHOLD = 20        # New filter: Only trade if trend strength > 20

TAKE_PROFIT_PCT = 0.0075  # Changed from 1.0% (more realistic target)
STOP_LOSS_PCT = 0.005     # Kept at 0.5%
# New R:R is 1.5 : 1

# Trading Session Parameters
TRADE_START_TIME = datetime.time(9, 20)
LAST_ENTRY_TIME = datetime.time(15, 10) # Last bar to check for new entries
FORCE_EXIT_TIME = datetime.time(15, 15) # Bar on which we exit EOD

# --- Database Functions ---

def connect_db():
    """Establishes a connection to the MySQL database."""
    try:
        cnx = mysql.connector.connect(**DB_CONFIG)
        cursor = cnx.cursor()
        print("Successfully connected to MySQL database 'market'.")
        return cnx, cursor
    except mysql.connector.Error as err:
        print(f"Error connecting to database: {err}")
        exit(1)

def create_trades_table(cursor):
    """Creates the table to log trades if it doesn't already exist."""
    create_table_query = f"""
    CREATE TABLE IF NOT EXISTS `{TRADES_TABLE_NAME}` (
      `id` INT NOT NULL AUTO_INCREMENT,
      `symbol` VARCHAR(20) NOT NULL,
      `position_type` VARCHAR(10) NOT NULL,
      `entry_timestamp` TIMESTAMP NOT NULL,
      `entry_price` DOUBLE(16, 4) NOT NULL,
      `exit_timestamp` TIMESTAMP NOT NULL,
      `exit_price` DOUBLE(16, 4) NOT NULL,
      `stop_loss_price` DOUBLE(16, 4) NOT NULL,
      `target_price` DOUBLE(16, 4) NOT NULL,
      `pnl` DOUBLE(16, 4) NOT NULL,
      `ppnl` DOUBLE(10, 4) NOT NULL COMMENT 'Percentage PnL',
      `exit_reason` VARCHAR(20) NOT NULL,
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB;
    """
    try:
        cursor.execute(create_table_query)
        print(f"Table '{TRADES_TABLE_NAME}' is ready.")
    except mysql.connector.Error as err:
        print(f"Failed to create table: {err}")

def fetch_data(cursor, symbol, duration):
    """Fetches 5-minute OHLCV data for a given symbol."""
    print(f"Fetching data for {symbol} ({duration})...")
    query = """
    SELECT start_timestamp, open, high, low, close, volume
    FROM market_data
    WHERE symbol = %s AND duration = %s AND is_correct = 1
    ORDER BY start_timestamp ASC;
    """
    try:
        cursor.execute(query, (symbol, duration))
        data = cursor.fetchall()
        
        if not data:
            print(f"No data found for {symbol} with duration {duration}.")
            return pd.DataFrame()

        # Create DataFrame
        df = pd.DataFrame(
            data,
            columns=['start_timestamp', 'open', 'high', 'low', 'close', 'volume']
        )
        print(f"Fetched {len(df)} records.")
        return df
    except mysql.connector.Error as err:
        print(f"Error fetching data: {err}")
        return pd.DataFrame()

def save_trades_to_db(cnx, cursor, trades_df):
    """Saves the backtest trade results to the database."""
    if trades_df.empty:
        print("No trades to save.")
        return

    print(f"Saving {len(trades_df)} trades to database...")
    
    # Prepare data for executemany (list of tuples)
    trade_tuples = [tuple(row) for row in trades_df.itertuples(index=False)]
    
    insert_query = f"""
    INSERT INTO `{TRADES_TABLE_NAME}` (
      symbol, position_type, entry_timestamp, entry_price, 
      exit_timestamp, exit_price, stop_loss_price, target_price, 
      pnl, ppnl, exit_reason
    ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    """
    
    try:
        # Truncate table for a fresh backtest result
        cursor.execute(f"TRUNCATE TABLE `{TRADES_TABLE_NAME}`")
        print("Cleared previous backtest results.")
        
        # Insert new results
        cursor.executemany(insert_query, trade_tuples)
        cnx.commit()
        print(f"Successfully saved {cursor.rowcount} trades.")
    except mysql.connector.Error as err:
        print(f"Error saving trades to database: {err}")
        cnx.rollback()

# --- Strategy & Backtesting Functions ---

def calculate_indicators(df):
    """
    Calculates technical indicators.
    Crucially, VWAP is calculated on a daily resetting basis to be correct.
    """
    print("Calculating indicators...")
    if df.empty:
        return df

    # 1. RSI (Causal, uses a rolling window)
    df['rsi'] = ta.rsi(df['close'], length=RSI_PERIOD)

    # 2. VWAP (Causal, must reset daily)
    # We group by date and calculate cumulative sums for PV and V
    df['date'] = df['start_timestamp'].dt.date
    
    # Calculate Typical Price * Volume
    df['tpv'] = ((df['high'] + df['low'] + df['close']) / 3) * df['volume']
    
    # Group by date and calculate cumulative sums
    grouped = df.groupby('date')
    df['cum_volume'] = grouped['volume'].cumsum()
    df['cum_tpv'] = grouped['tpv'].cumsum()
    
    # Calculate VWAP
    df['vwap'] = df['cum_tpv'] / df['cum_volume']
    
    # 3. ADX and +/-DI (Causal)
    # This function returns a DataFrame with ADX, DMP (+DI), DMN (-DI)
    adx_df = ta.adx(df['high'], df['low'], df['close'], length=ADX_PERIOD)
    
    # Merge ADX results into the main DataFrame
    # Note: Column names are like 'ADX_14', 'DMP_14', 'DMN_14'
    df = pd.concat([df, adx_df], axis=1)
    
    # Rename for easier access
    df = df.rename(columns={
        f'ADX_{ADX_PERIOD}': 'adx',
        f'DMP_{ADX_PERIOD}': 'dmp',
        f'DMN_{ADX_PERIOD}': 'dmn'
    })

    # Clean up intermediate columns
    df = df.drop(columns=['date', 'tpv', 'cum_volume', 'cum_tpv'])
    
    # Drop NaN rows created by indicator warm-up period
    df = df.dropna().reset_index(drop=True)
    print("Indicators calculated.")
    return df

def run_backtest(df):
    """
    Runs the bar-by-bar backtest loop to prevent look-ahead bias.
    """
    if df.empty:
        print("Dataframe is empty, cannot run backtest.")
        return pd.DataFrame()

    print("Running backtest (v2)...")
    trades = []
    
    # Position state
    in_position = False
    position_type = None
    entry_price = 0.0
    entry_timestamp = None
    stop_loss_price = 0.0
    target_price = 0.0

    # Iterate from the first valid row (after indicator warmup)
    for i in range(1, len(df)):
        prev_row = df.iloc[i-1]
        row = df.iloc[i]

        current_time = row['start_timestamp'].time()

        # --- 1. IN-POSITION LOGIC (Check for Exits) ---
        if in_position:
            exit_price = None
            exit_reason = None
            exit_timestamp = row['start_timestamp']

            # 1a. Check for End-of-Day Force Exit
            if current_time == FORCE_EXIT_TIME:
                exit_price = row['open']  # Exit at the open of the 15:15 bar
                exit_reason = "EOD"

            # 1b. Check for Stop-Loss or Take-Profit
            if position_type == 'LONG' and not exit_reason:
                # Check SL first (important)
                if row['low'] <= stop_loss_price:
                    exit_price = stop_loss_price
                    exit_reason = "SL"
                # Check TP
                elif row['high'] >= target_price:
                    exit_price = target_price
                    exit_reason = "TP"
            
            elif position_type == 'SHORT' and not exit_reason:
                # Check SL first (important)
                if row['high'] >= stop_loss_price:
                    exit_price = stop_loss_price
                    exit_reason = "SL"
                # Check TP
                elif row['low'] <= target_price:
                    exit_price = target_price
                    exit_reason = "TP"

            # 1c. If an exit was triggered, log the trade
            if exit_reason:
                # Calculate PnL
                if position_type == 'LONG':
                    pnl = exit_price - entry_price
                else:
                    pnl = entry_price - exit_price
                
                ppnl = (pnl / entry_price) * 100
                
                # Append trade to log
                trades.append({
                    'symbol': TARGET_SYMBOL,
                    'position_type': position_type,
                    'entry_timestamp': entry_timestamp,
                    'entry_price': entry_price,
                    'exit_timestamp': exit_timestamp,
                    'exit_price': exit_price,
                    'stop_loss_price': stop_loss_price,
                    'target_price': target_price,
                    'pnl': pnl,
                    'ppnl': ppnl,
                    'exit_reason': exit_reason
                })
                
                # Reset position state
                in_position = False
                position_type = None
                entry_price = 0.0

        # --- 2. NOT-IN-POSITION LOGIC (Check for Entries) ---
        if (not in_position and
            current_time >= TRADE_START_TIME and
            current_time <= LAST_ENTRY_TIME):
            
            # --- v2 Strategy Conditions ---
            
            # 1. Trend Strength Condition
            is_trending = row['adx'] > ADX_THRESHOLD
            
            # 2. Main Trend Condition (VWAP)
            is_uptrend = row['close'] > row['vwap']
            is_downtrend = row['close'] < row['vwap']
            
            # 3. Momentum Direction Condition
            is_bullish_momentum = row['dmp'] > row['dmn']
            is_bearish_momentum = row['dmn'] > row['dmp']

            # 4. RSI Pullback Condition
            rsi_buy_signal = row['rsi'] > RSI_OVERSOLD_LEVEL and prev_row['rsi'] <= RSI_OVERSOLD_LEVEL
            rsi_sell_signal = row['rsi'] < RSI_OVERBOUGHT_LEVEL and prev_row['rsi'] >= RSI_OVERBOUGHT_LEVEL

            # LONG Entry Signal:
            if (is_trending and
                is_uptrend and
                is_bullish_momentum and
                rsi_buy_signal):
                
                in_position = True
                position_type = 'LONG'
                entry_price = row['close']
                entry_timestamp = row['start_timestamp']
                
                # Set SL and TP
                stop_loss_price = entry_price * (1 - STOP_LOSS_PCT)
                target_price = entry_price * (1 + TAKE_PROFIT_PCT)

            # SHORT Entry Signal:
            elif (is_trending and
                  is_downtrend and
                  is_bearish_momentum and
                  rsi_sell_signal):

                in_position = True
                position_type = 'SHORT'
                entry_price = row['close']
                entry_timestamp = row['start_timestamp']
                
                # Set SL and TP
                stop_loss_price = entry_price * (1 + STOP_LOSS_PCT)
                target_price = entry_price * (1 - TAKE_PROFIT_PCT)

    print(f"Backtest complete. {len(trades)} trades generated.")
    return pd.DataFrame(trades)

def print_statistics(trades_df):
    """Calculates and prints key performance metrics."""
    if trades_df.empty:
        print("No trades to analyze.")
        return
        
    print("\n--- Backtest Performance Statistics (v2) ---")
    
    total_trades = len(trades_df)
    winning_trades = trades_df[trades_df['ppnl'] > 0]
    losing_trades = trades_df[trades_df['ppnl'] < 0]

    # Handle potential divide-by-zero errors
    try:
        win_rate = (len(winning_trades) / total_trades) * 100
    except ZeroDivisionError:
        win_rate = 0.0

    total_pnl_pct = trades_df['ppnl'].sum()
    average_ppnl = trades_df['ppnl'].mean()
    
    try:
        avg_win_ppnl = winning_trades['ppnl'].mean()
    except ZeroDivisionError:
        avg_win_ppnl = 0.0

    try:
        avg_loss_ppnl = losing_trades['ppnl'].mean()
    except ZeroDivisionError:
        avg_loss_ppnl = 0.0

    try:
        # Profit Factor: Gross Profit / Gross Loss
        gross_profit = winning_trades['pnl'].sum()
        gross_loss = abs(losing_trades['pnl'].sum())
        profit_factor = gross_profit / gross_loss
    except ZeroDivisionError:
        profit_factor = float('inf') if gross_profit > 0 else 0.0
        
    print(f"Total Trades:         {total_trades}")
    print(f"Win Rate:             {win_rate:.2f}%")
    print(f"Profit Factor:        {profit_factor:.2f}")
    print(f"Total PnL (%):        {total_pnl_pct:.2f}%")
    print(f"Average PnL (%):      {average_ppnl:.4f}%")
    print(f"Average Win (%):      {avg_win_ppnl:.4f}%")
    print(f"Average Loss (%):     {avg_loss_ppnl:.4f}%")
    
    print("\nExit Reasons:")
    print(trades_df['exit_reason'].value_counts(normalize=True).mul(100).round(2).astype(str) + '%')

# --- Main Execution ---

def main():
    """Main execution function."""
    cnx = None
    cursor = None
    try:
        cnx, cursor = connect_db()
        
        # 1. Setup the trades log table
        create_trades_table(cursor)
        
        # 2. Fetch historical data
        df = fetch_data(cursor, TARGET_SYMBOL, TARGET_DURATION)
        
        if df.empty:
            return

        # 3. Calculate indicators
        df_with_indicators = calculate_indicators(df)
        
        if df_with_indicators.empty:
            print("Not enough data to calculate indicators.")
            return

        # 4. Run the backtest
        trades_df = run_backtest(df_with_indicators)
        
        if not trades_df.empty:
            # 5. Save results to database
            save_trades_to_db(cnx, cursor, trades_df)
            
            # 6. Print performance statistics
            print_statistics(trades_df)
        else:
            print("Backtest generated no trades.")
            
    except Exception as e:
        print(f"An unexpected error occurred: {e}")
    finally:
        if cursor:
            cursor.close()
        if cnx:
            cnx.close()
            print("MySQL connection closed.")

if __name__ == "__main__":
    main()
