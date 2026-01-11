#!/usr/bin/env python3
"""
Backtester with minimal addition: NIFTY50 trend confirmation (EMA9 vs EMA21).
Only minimal changes from your original script:
 - Load NIFTY50 once in main, compute indicators.
 - Pass nifty_df into backtest_strategy.
 - Inside backtest_strategy, align NIFTY EMAs to df_day and add one condition:
     - longs require nifty_ema9 > nifty_ema21
     - shorts require nifty_ema9 < nifty_ema21
Everything else is unchanged.
"""

import pandas as pd
import mysql.connector
from mysql.connector import Error
from datetime import time
import sys
import ta  # pip install ta

def connect_to_database():
    """Establish connection to the MySQL database."""
    try:
        connection = mysql.connector.connect(
            host='localhost',
            user='root',
            password='root',
            database='market'
        )
        if connection.is_connected():
            return connection
    except Error as e:
        print(f"Error connecting to MySQL: {e}")
        sys.exit(1)

def create_trades_table(cursor):
    """Create the trades table if it does not exist."""
    create_query = """
    CREATE TABLE IF NOT EXISTS trades (
        id INT AUTO_INCREMENT PRIMARY KEY,
        symbol VARCHAR(20) NOT NULL,
        entry_timestamp TIMESTAMP NOT NULL,
        entry_price DOUBLE(16,4) NOT NULL,
        side VARCHAR(10) NOT NULL,
        exit_timestamp TIMESTAMP NOT NULL,
        exit_price DOUBLE(16,4) NOT NULL,
        stoploss DOUBLE(16,4) NOT NULL,
        target DOUBLE(16,4) NOT NULL,
        pnl DOUBLE NOT NULL,
        ppnl DOUBLE NOT NULL
    )
    """
    try:
        cursor.execute(create_query)
    except Error as e:
        print(f"Error creating trades table: {e}")
        raise

def load_data(cursor, symbol='AXISBANK'):
    """Load historical 5-minute OHLCV data from the database."""
    query = """
    SELECT start_timestamp, open, high, low, close, volume, is_correct
    FROM market_data
    WHERE symbol = %s 
    AND duration = '5minute' 
    ORDER BY start_timestamp
    """
    try:
        # This code expects a global 'conn' variable for the connection (keeps parity with your existing code)
        df = pd.read_sql(query, conn, params=(symbol,), index_col='start_timestamp', parse_dates=True)
        df = df[['open', 'high', 'low', 'close', 'volume', 'is_correct']]
        if df.empty:
            raise ValueError(f"No data found for symbol {symbol}")
        return df
    except Error as e:
        print(f"Error loading data: {e}")
        raise

def compute_indicators(df):
    """Compute EMAs, RSI, ADX and ATR (kept global; VWAP & per-day vol ma are computed per day)."""
    df['ema9'] = df['close'].ewm(span=9, adjust=False).mean()
    df['ema21'] = df['close'].ewm(span=21, adjust=False).mean()
    df['ema50'] = df['close'].ewm(span=50, adjust=False).mean()
    df['rsi'] = ta.momentum.RSIIndicator(df['close'], window=14).rsi()
    adx_indicator = ta.trend.ADXIndicator(df['high'], df['low'], df['close'], window=14)
    df['adx'] = adx_indicator.adx()
    df['atr'] = ta.volatility.AverageTrueRange(df['high'], df['low'], df['close'], window=14).average_true_range()
    df['date'] = df.index.date
    return df

def take_trades(df_day, signal_time, side, exit_end_time, trades, cursor, symbol):
    """Simulate trade execution and record results. Uses ATR-based SL/TP (1x ATR SL, 2x ATR TP)."""
    cutoff_time = time(15, 5, 0)
    if signal_time.time() > cutoff_time:
        return

    entry_time = signal_time
    entry_price = round(df_day.loc[entry_time, 'close'], 2)

    atr = df_day.loc[entry_time, 'atr']
    if pd.isna(atr) or atr == 0:
        return

    # ATR-based SL/TP (1x ATR SL, 2x ATR target)
    if side == 'long':
        stoploss = round(entry_price - atr, 2)
        target_price = round(entry_price + 2 * atr, 2)
    else:
        stoploss = round(entry_price + atr, 2)
        target_price = round(entry_price - 2 * atr, 2)

    # Simulate post-entry
    post_entry_mask = (df_day.index > entry_time) & (df_day.index.time <= exit_end_time)
    post_entry = df_day[post_entry_mask]
    if post_entry.empty:
        exit_time = entry_time
        exit_price = entry_price
    else:
        exit_time, exit_price = None, None
        position_open = True
        for idx, row in post_entry.iterrows():
            high, low = row['high'], row['low']
            if side == 'long':
                if low <= stoploss:
                    exit_price, exit_time = stoploss, idx
                    position_open = False
                    break
                elif high >= target_price:
                    exit_price, exit_time = target_price, idx
                    position_open = False
                    break
            else:
                if high >= stoploss:
                    exit_price, exit_time = stoploss, idx
                    position_open = False
                    break
                elif low <= target_price:
                    exit_price, exit_time = target_price, idx
                    position_open = False
                    break

        if position_open:
            exit_time = post_entry.index[-1]
            exit_price = post_entry.loc[exit_time, 'open']

    exit_price = round(exit_price, 2)

    # Calculate PnL
    pnl = round(exit_price - entry_price, 2) if side == 'long' else round(entry_price - exit_price, 2)
    ppnl = round((pnl / entry_price) * 100, 2)

    trade = {
        'symbol': symbol,
        'entry_timestamp': entry_time,
        'entry_price': entry_price,
        'side': side,
        'exit_timestamp': exit_time,
        'exit_price': exit_price,
        'stoploss': stoploss,
        'target': target_price,
        'pnl': pnl,
        'ppnl': ppnl
    }
    trades.append(trade)

    # Insert trade into DB
    insert_query = """
    INSERT INTO trades (symbol, entry_timestamp, entry_price, side, exit_timestamp, exit_price, stoploss, target, pnl, ppnl)
    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    """
    try:
        cursor.execute(insert_query, (
            trade['symbol'], trade['entry_timestamp'], trade['entry_price'], trade['side'],
            trade['exit_timestamp'], trade['exit_price'], trade['stoploss'], trade['target'],
            trade['pnl'], trade['ppnl']
        ))
    except Error as e:
        print(f"Error inserting trade: {e}")

def backtest_strategy(df, cursor, symbol='AXISBANK',
                      vol_window=20, vol_mult=1.2,
                      entry_start_time=time(9,20),
                      exit_end_time=time(15,15),
                      min_bars_per_day=10,
                      nifty_df=None):
    """
    Perform the backtest with EMA+RSI+ADX+ATR and added Volume + VWAP confirmation.
    Added parameter nifty_df (minimal change) for NIFTY50 trend confirmation.
    vol_window: rolling window for per-day volume average (bars)
    vol_mult: multiplier threshold (e.g., 1.2 means 20% above avg)
    """
    trades = []

    for date, df_day in df.groupby('date'):
        df_day = df_day.copy()
        if len(df_day) < min_bars_per_day:
            continue

        # compute per-day rolling volume mean (vol_ma20) - min_periods=1 avoids NaN early
        df_day['vol_ma20'] = df_day['volume'].rolling(window=vol_window, min_periods=1).mean()

        # compute cumulative VWAP per day (typical price * vol) cumulative / cum vol
        tp = (df_day['high'] + df_day['low'] + df_day['close']) / 3.0
        df_day['cum_pv'] = (tp * df_day['volume']).cumsum()
        df_day['cum_v'] = df_day['volume'].cumsum()
        # avoid division by zero
        df_day['vwap'] = df_day['cum_pv'] / df_day['cum_v']
        df_day.drop(columns=['cum_pv', 'cum_v'], inplace=True)

        # causal EMA crossover detection (using shifted EMAs as before)
        df_day['prev_ema9'] = df_day['ema9'].shift(1)
        df_day['prev_ema21'] = df_day['ema21'].shift(1)
        df_day['cross_up'] = (
            (df_day['prev_ema9'] > df_day['prev_ema21']) &
            (df_day['prev_ema9'].shift(1) <= df_day['prev_ema21'].shift(1))
        )
        df_day['cross_down'] = (
            (df_day['prev_ema9'] < df_day['prev_ema21']) &
            (df_day['prev_ema9'].shift(1) >= df_day['prev_ema21'].shift(1))
        )

        # Avoid low-volatility lunch hours
        df_day = df_day[~((df_day.index.time >= time(12, 0)) & (df_day.index.time <= time(13, 30)))]

        # ---- Minimal addition: align NIFTY50 EMAs to this day's timestamps ----
        if nifty_df is not None:
            # reindex nifty to same timestamps (this will introduce NaN where no matching timestamp)
            nifty_slice = nifty_df.reindex(df_day.index)
            # rename columns to avoid collision
            nifty_slice = nifty_slice[['ema9', 'ema21']].rename(columns={'ema9': 'nifty_ema9', 'ema21': 'nifty_ema21'})
            # join (left) so df_day keeps its rows
            df_day = df_day.join(nifty_slice)

        # Only consider entries after start time
        possible_entries = df_day[df_day.index.time >= entry_start_time]

        # Volume + VWAP filters included (added NIFTY trend condition with minimal change)
        long_signals = possible_entries[
            (possible_entries['cross_up']) &
            (possible_entries['ema9'] > possible_entries['ema21']) &
            (possible_entries['ema21'] > possible_entries['ema50']) &
            (possible_entries['rsi'] > 55) &
            (possible_entries['adx'] > 20) &
            (possible_entries['close'] > possible_entries['vwap']) &  # price above VWAP
            (possible_entries['volume'] > (possible_entries['vol_ma20'] * vol_mult)) &  # volume spike
            (possible_entries['close'] > possible_entries[['ema9', 'ema21']].max(axis=1)) &  # immediate confirmation
            (possible_entries['nifty_ema9'] > possible_entries['nifty_ema21'])  # <-- NIFTY trend must be up
        ].index

        short_signals = possible_entries[
            (possible_entries['cross_down']) &
            (possible_entries['ema9'] < possible_entries['ema21']) &
            (possible_entries['ema21'] < possible_entries['ema50']) &
            (possible_entries['rsi'] < 45) &
            (possible_entries['adx'] > 20) &
            (possible_entries['close'] < possible_entries['vwap']) &  # price below VWAP
            (possible_entries['volume'] > (possible_entries['vol_ma20'] * vol_mult)) &  # volume spike
            (possible_entries['close'] < possible_entries[['ema9', 'ema21']].min(axis=1)) &  # immediate confirmation
            (possible_entries['nifty_ema9'] < possible_entries['nifty_ema21'])  # <-- NIFTY trend must be down
        ].index

        # Execute trades found
        for long_signal in long_signals:
            take_trades(df_day, long_signal, 'long', exit_end_time, trades, cursor, symbol)
        for short_signal in short_signals:
            take_trades(df_day, short_signal, 'short', exit_end_time, trades, cursor, symbol)

    conn.commit()
    return trades

def compute_and_print_stats(trades, symbol):
    """Compute and print backtest statistics."""
    if not trades:
        print(f"No trades were generated for {symbol}.")
        return

    trades_df = pd.DataFrame(trades)
    avg_ppnl = trades_df['ppnl'].mean()
    win_rate = (trades_df['ppnl'] > 0).mean() * 100
    num_trades = len(trades)
    total_pnl = trades_df['ppnl'].sum()

    print(f"\nBacktest Results for {symbol} (EMA + RSI + ADX + ATR + Volume + VWAP):")
    print(f"Number of trades: {num_trades}")
    print(f"Average %PnL per trade: {avg_ppnl:.4f}%")
    print(f"Win rate: {win_rate:.2f}%")
    print(f"Total %PnL: {total_pnl:.2f}%")

def get_equity_symbols_by_category(conn, cursor):
    try:
        query = """SELECT symbol FROM equityStocks WHERE category IN (3, 4, 5)"""
        cursor.execute(query)
        result = cursor.fetchall()
        symbols = [row[0] for row in result]  # Extract only the symbol column
        return symbols
    
    except Exception as e:
        print(f"Error fetching symbols: {e}")
        conn.rollback()
        return []

if __name__ == "__main__":
    conn = None
    cursor = None
    try:
        conn = connect_to_database()
        cursor = conn.cursor()

        create_trades_table(cursor)

        # Load NIFTY50 once (minimal addition). If NIFTY50 symbol differs in your DB, change it here.
        nifty_df = None
        try:
            nifty_df = load_data(cursor, 'NIFTY50')
            nifty_df = compute_indicators(nifty_df)
        except Exception as e:
            print(f"Warning: could not load/compute NIFTY50 data: {e}")
            nifty_df = None

        # symbols = get_equity_symbols_by_category(conn, cursor)
        symbols = ['AXISBANK', 'ADANIENT']
        for symbol in symbols:
            try:
                df = load_data(cursor, symbol)
                df = compute_indicators(df)
                trades = backtest_strategy(df, cursor, symbol,
                                          vol_window=20, vol_mult=1.2,
                                          entry_start_time=time(9,20),
                                          exit_end_time=time(15,15),
                                          min_bars_per_day=10,
                                          nifty_df=nifty_df)  # pass nifty_df (minimal change)
                compute_and_print_stats(trades, symbol)
            except Exception as e:
                print(f"{symbol} : Error during backtest: {e}")
                continue

    except Exception as e:
        print(f"Fatal error: {e}")
    finally:
        if cursor:
            cursor.close()
        if conn and conn.is_connected():
            conn.close()
            print("\nDatabase connection closed.")
