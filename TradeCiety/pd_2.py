#!/usr/bin/env python3
import mysql.connector
import pandas as pd
import numpy as np
from datetime import time, datetime, timedelta
from itertools import product
import math
import json

cutoff_date = datetime(2015, 4, 1).date()

# ---------- LOGGING CONFIG ----------
LOG_EVERY_N_BARS = 500
LOG_EVERY_N_TRADES = 500

# ---------- CONFIG ----------
MYSQL_CONF = {
    "host": "localhost",
    "user": "root",
    "password": "root",
    "database": "market",
    "raise_on_warnings": False
}

SYMBOL = "AXISBANK"
NIFTY_SYMBOL = "NIFTY50"
DURATION = "5minute"

ATR_PERIOD = 14

ENTRY_TIME_START = time(9, 20)
ENTRY_TIME_END = time(15, 10)

MAX_EXIT_TIME = time(15, 15)

configs = [
    {"target": 1.0, "stoploss": 0.5, "exit_strategy": "fixed"},
    {"target": 1.0, "stoploss": 0.5, "exit_strategy": "fixed_trailing"},
    {"target": 1.5, "stoploss": 0.5, "exit_strategy": "fixed"},
    {"target": 1.5, "stoploss": 0.5, "exit_strategy": "fixed_trailing"},
    {"target": 1.5, "stoploss": 0.75, "exit_strategy": "fixed"},
    {"target": 1.5, "stoploss": 0.75, "exit_strategy": "fixed_trailing"},
    {"target": 2.25, "stoploss": 0.75, "exit_strategy": "fixed"},
    {"target": 2.25, "stoploss": 0.75, "exit_strategy": "fixed_trailing"},
    {"target": 2, "stoploss": 1, "exit_strategy": "fixed"},
    {"target": 2, "stoploss": 1, "exit_strategy": "fixed_trailing"},
    {"target": 3, "stoploss": 1, "exit_strategy": "fixed"},
    {"target": 3, "stoploss": 1, "exit_strategy": "fixed_trailing"},

    {"target": 1.0, "stoploss": 0.5, "exit_strategy": "atr"},
    {"target": 1.0, "stoploss": 0.5, "exit_strategy": "atr_trailing"},
    {"target": 1.5, "stoploss": 0.5, "exit_strategy": "atr"},
    {"target": 1.5, "stoploss": 0.5, "exit_strategy": "atr_trailing"},
    {"target": 1.5, "stoploss": 0.75, "exit_strategy": "atr"},
    {"target": 1.5, "stoploss": 0.75, "exit_strategy": "atr_trailing"},
    {"target": 2.25, "stoploss": 0.75, "exit_strategy": "atr"},
    {"target": 2.25, "stoploss": 0.75, "exit_strategy": "atr_trailing"},
    {"target": 2, "stoploss": 1, "exit_strategy": "atr"},
    {"target": 2, "stoploss": 1, "exit_strategy": "atr_trailing"},
    {"target": 3, "stoploss": 1, "exit_strategy": "atr"},
    {"target": 3, "stoploss": 1, "exit_strategy": "atr_trailing"},
]

TRADES_TABLE = "intraday_trades"

# ---------- utility functions ----------
def r2(x):
    if x is None:
        return None
    try:
        fx = float(x)
        if math.isnan(fx) or math.isinf(fx):
            return None
        return round(fx, 2)
    except:
        return None

def safe_round(v):
    """Return None for None/nan/inf, else rounded float."""
    if v is None:
        return None
    try:
        fv = float(v)
        if math.isnan(fv) or math.isinf(fv):
            return None
        return round(fv, 2)
    except:
        return None

def round_meta_values(obj):
    """Recursively round all numeric values inside a dict/list to 2 decimals."""
    if isinstance(obj, dict):
        return {k: round_meta_values(v) for k, v in obj.items()}
    elif isinstance(obj, list):
        return [round_meta_values(v) for v in obj]
    else:
        try:
            fv = float(obj)
            if math.isnan(fv) or math.isinf(fv):
                return obj
            return round(fv, 2)
        except:
            return obj

def connect_mysql():
    print("[INFO] Connecting to MySQL...")
    return mysql.connector.connect(**MYSQL_CONF)

def ensure_trades_table_exists(conn):
    cur = conn.cursor()
    # DROP existing meta & trades to avoid schema mismatch (as your earlier code did)
    # cur.execute("""DROP TABLE IF EXISTS intraday_meta""")
    # conn.commit()
    # cur.execute("""DROP TABLE IF EXISTS intraday_trades""")
    # conn.commit()

    # Create intraday_meta with hardcoded schema (Option B)
    # This schema contains the columns your meta dict produces (approx ~65)
    cur.execute("""
    CREATE TABLE IF NOT EXISTS intraday_meta (
      id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
      symbol VARCHAR(32),
      entry_timestamp TIMESTAMP,

      prevday_open DOUBLE, prevday_high DOUBLE, prevday_low DOUBLE, prevday_close DOUBLE, prevday_volume DOUBLE,

      ema5_5min DOUBLE, ema5_5min_tm1 DOUBLE, ema5_5min_tm3 DOUBLE, ema5_5min_tm5 DOUBLE,
      ema20_5min DOUBLE, ema20_5min_tm1 DOUBLE, ema20_5min_tm3 DOUBLE, ema20_5min_tm5 DOUBLE,
      ema50_5min DOUBLE, ema50_5min_tm1 DOUBLE, ema50_5min_tm3 DOUBLE, ema50_5min_tm5 DOUBLE,

      ema5_prevday DOUBLE, ema5_prevday_tm1 DOUBLE, ema5_prevday_tm3 DOUBLE, ema5_prevday_tm5 DOUBLE,
      ema20_prevday DOUBLE, ema20_prevday_tm1 DOUBLE, ema20_prevday_tm3 DOUBLE, ema20_prevday_tm5 DOUBLE,
      ema50_prevday DOUBLE, ema50_prevday_tm1 DOUBLE, ema50_prevday_tm3 DOUBLE, ema50_prevday_tm5 DOUBLE,

      today_open DOUBLE, today_high DOUBLE, today_low DOUBLE, today_close DOUBLE, today_volume DOUBLE,

      nifty_prev_open DOUBLE, nifty_prev_high DOUBLE, nifty_prev_low DOUBLE, nifty_prev_close DOUBLE, nifty_prev_volume DOUBLE,
      nifty_today_open DOUBLE, nifty_today_high DOUBLE, nifty_today_low DOUBLE, nifty_today_close DOUBLE,

      macd DOUBLE, macd_signal DOUBLE, macd_hist DOUBLE,
      rsi DOUBLE, stoch_k DOUBLE, stoch_d DOUBLE,
      vwap DOUBLE,
      bb_mid DOUBLE, bb_std DOUBLE, bb_upper DOUBLE, bb_lower DOUBLE,

      day_1st_5min_open DOUBLE, day_1st_5min_high DOUBLE, day_1st_5min_low DOUBLE,
      day_1st_5min_close DOUBLE, day_1st_5min_volume DOUBLE,

      latest_5min_open DOUBLE, latest_5min_high DOUBLE, latest_5min_low DOUBLE,
      latest_5min_close DOUBLE, latest_5min_volume DOUBLE,

      atr DOUBLE,

      UNIQUE KEY unique_symbol_ts (symbol, entry_timestamp),
      KEY `idx_entry_timestamp` (`entry_timestamp`)
    )
    """)
    cur.execute(f"""
    CREATE TABLE IF NOT EXISTS {TRADES_TABLE} (
      id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
      symbol VARCHAR(32),
      entry_timestamp TIMESTAMP,
      entry_price DOUBLE,
      direction VARCHAR(8),
      param_target DOUBLE,
      target_price DOUBLE,
      param_stoploss DOUBLE,
      stoploss_price DOUBLE,
      updated_stoploss_price DOUBLE,
      exit_timestamp TIMESTAMP,
      exit_price DOUBLE,
      exit_strategy VARCHAR(64),
      pnl DOUBLE,
      ppnl DOUBLE,
      meta_id INT,
      notes TEXT,
      KEY `idx_entry_timestamp` (`entry_timestamp`),
      KEY `idx_meta_id` (`meta_id`)
    );
    """)
    conn.commit()
    cur.close()
    print(f"[INFO] Table '{TRADES_TABLE}' and 'intraday_meta' are ready.")

def fetch_5min_data_for_symbol(conn, symbol):
    """
    Fetch 5-minute rows for a symbol (including is_correct),
    return dataframe with start_timestamp etc.
    """
    q = ("""
        SELECT start_timestamp, end_timestamp, open, high, low, close, volume, is_correct
        FROM market_data
        WHERE symbol = %s AND duration = %s
        AND start_timestamp >= '2015-04-01 00:00:00'
        ORDER BY start_timestamp ASC
    """)
    df = pd.read_sql(q, conn, params=(symbol, DURATION))
    df['start_timestamp'] = pd.to_datetime(df['start_timestamp'])
    df['end_timestamp'] = pd.to_datetime(df['end_timestamp'])
    return df

# ---------- NEW: daily loaders & daily EMA prep (minimal add) ----------
def fetch_day_data(conn, symbol):
    q = """
        SELECT start_timestamp, open, high, low, close, volume
        FROM market_data
        WHERE symbol=%s AND duration='day' AND is_correct=1
        AND start_timestamp >= '2015-04-01 00:00:00'
        ORDER BY start_timestamp ASC
    """
    df = pd.read_sql(q, conn, params=(symbol,))
    df['start_timestamp'] = pd.to_datetime(df['start_timestamp'])
    df['date'] = df['start_timestamp'].dt.date
    return df

def prepare_daily_features(df):
    """Compute daily EMAs from daily candles."""
    if df is None or df.empty:
        return df
    df = df.copy().reset_index(drop=True)
    df['ema5_day'] = df['close'].ewm(span=5, adjust=False).mean()
    df['ema20_day'] = df['close'].ewm(span=20, adjust=False).mean()
    df['ema50_day'] = df['close'].ewm(span=50, adjust=False).mean()
    return df

def fetch_5min_data(conn, symbol, nifty_symbol=NIFTY_SYMBOL):
    """
    Fetch data for symbol and for NIFTY. Skip entire days where ANY
    candle for either symbol has is_correct == 0.
    Return combined df for symbol only (cleaned), and the nifty df (cleaned).
    """
    print(f"[INFO] Fetching 5-minute data for symbol={symbol} and NIFTY={nifty_symbol}...")
    df_sym = fetch_5min_data_for_symbol(conn, symbol)
    df_nifty = fetch_5min_data_for_symbol(conn, nifty_symbol)
    if df_sym.empty:
        print(f"[ERROR] No data for {symbol}")
    if df_nifty.empty:
        print(f"[ERROR] No data for {nifty_symbol}")
    # Add date column
    df_sym['date'] = df_sym['start_timestamp'].dt.date
    df_nifty['date'] = df_nifty['start_timestamp'].dt.date
    # Days to drop: any date where is_correct == 0 in either df
    bad_days_sym = set(df_sym[df_sym['is_correct'] == 0]['date'].unique())
    bad_days_nifty = set(df_nifty[df_nifty['is_correct'] == 0]['date'].unique())
    bad_days = bad_days_sym.union(bad_days_nifty)
    if len(bad_days) > 0:
        print(f"[WARN] Skipping days due to incorrect candles (stock or NIFTY): {sorted(bad_days)}")
    # keep only good days
    df_sym = df_sym[~df_sym['date'].isin(bad_days)].copy()
    df_nifty = df_nifty[~df_nifty['date'].isin(bad_days)].copy()
    # drop helper column is_correct left for debugging then drop
    df_sym.drop(columns=['is_correct'], inplace=True)
    df_nifty.drop(columns=['is_correct'], inplace=True)
    # For safety, keep date column (helpful)
    print(f"[INFO] Loaded {len(df_sym)} rows for {symbol} after filtering bad days.")
    print(f"[INFO] Loaded {len(df_nifty)} rows for {nifty_symbol} after filtering bad days.")
    return df_sym, df_nifty

def time_of(ts):
    return ts.time()

# ---------- indicators ----------
def compute_atr(df, period=ATR_PERIOD):
    df = df.copy()

    # True Range components
    df['prev_close'] = df['close'].shift(1)
    df['tr1'] = (df['high'] - df['low']).abs()
    df['tr2'] = (df['high'] - df['prev_close']).abs()
    df['tr3'] = (df['low'] - df['prev_close']).abs()

    # True Range
    df['TR'] = df[['tr1','tr2','tr3']].max(axis=1)

    # --- Wilder ATR (RMA) ---
    atr = df['TR'].copy()
    atr.iloc[:period] = atr.iloc[:period].rolling(period).mean()   # seed = SMA

    for i in range(period, len(atr)):
        atr.iloc[i] = (atr.iloc[i-1] * (period - 1) + atr.iloc[i]) / period

    df['ATR'] = atr

    # cleanup
    df.drop(columns=['prev_close','tr1','tr2','tr3','TR'], inplace=True)

    return df

def add_sma_ema(df, windows=[5,20,50]):
    df = df.copy()
    for w in windows:
        df[f'ema{w}'] = df['close'].ewm(span=w, adjust=False).mean()
    return df

def add_macd(df, fast=12, slow=26, signal=9):
    df = df.copy()
    ema_fast = df['close'].ewm(span=fast, adjust=False).mean()
    ema_slow = df['close'].ewm(span=slow, adjust=False).mean()
    df['macd'] = ema_fast - ema_slow
    df['macd_signal'] = df['macd'].ewm(span=signal, adjust=False).mean()
    df['macd_hist'] = df['macd'] - df['macd_signal']
    return df

def add_rsi(df, period=14):
    df = df.copy()

    delta = df['close'].diff()

    gain = delta.clip(lower=0)
    loss = -delta.clip(upper=0)

    avg_gain = gain.rolling(period).mean()
    avg_loss = loss.rolling(period).mean()

    rsi_values = [np.nan] * len(df)

    for i in range(len(df)):
        if i < period:
            rsi_values[i] = np.nan
            continue

        if i == period:
            current_gain = avg_gain.iloc[i]
            current_loss = avg_loss.iloc[i]
        else:
            current_gain = (current_gain * (period - 1) + gain.iloc[i]) / period
            current_loss = (current_loss * (period - 1) + loss.iloc[i]) / period

        if current_loss == 0:
            rsi_values[i] = 100
        else:
            rs = current_gain / current_loss
            rsi_values[i] = 100 - (100 / (1 + rs))

    df['rsi'] = rsi_values

    return df

def add_bollinger_bands(df, period=20, std_mult=2):
    df = df.copy()
    df['bb_mid'] = df['close'].rolling(period).mean()
    df['bb_std'] = df["close"].rolling(period).std(ddof=0)

    df['bb_upper'] = df['bb_mid'] + std_mult * df['bb_std']
    df['bb_lower'] = df['bb_mid'] - std_mult * df['bb_std']

    return df

def add_stochastic(df, k_period=14, d_period=3):
    df = df.copy()
    low_k = df['low'].rolling(window=k_period, min_periods=1).min()
    high_k = df['high'].rolling(window=k_period, min_periods=1).max()
    df['stoch_k'] = 100 * ((df['close'] - low_k) / (high_k - low_k).replace(0, np.nan))
    df['stoch_k'] = df['stoch_k'].fillna(50)
    df['stoch_d'] = df['stoch_k'].rolling(window=d_period, min_periods=1).mean()
    return df

def add_vwap(df):
    df = df.copy()
    df['tpv'] = ((df['high'] + df['low'] + df['close']) / 3.0) * df['volume']
    df['date'] = df['start_timestamp'].dt.date
    df['cum_tpv'] = df.groupby('date')['tpv'].cumsum()
    df['cum_vol'] = df.groupby('date')['volume'].cumsum()
    df['vwap'] = df['cum_tpv'] / df['cum_vol'].replace(0, np.nan)
    df.drop(columns=['tpv','cum_tpv','cum_vol'], inplace=True)
    df.drop(columns=['date'], inplace=True)
    return df

def add_pivots_daily(df, conn):
    df = df.copy()
    df['date'] = df['start_timestamp'].dt.date
    return df

# ---------- prepare full feature DataFrames ----------
def prepare_features(df, conn):
    df = df.copy().reset_index(drop=True)

    # ----- Compute ATR first -----
    df = compute_atr(df)         # row-based → shift

    # Add date column for grouping
    df['date'] = df['start_timestamp'].dt.date

    # daily EMA(5), (20), (50) — these are intraday daily-group EMAs (not the daily-candle EMAs)
    df['ema5_day'] = df.groupby('date')['close'] \
                       .transform(lambda s: s.ewm(span=5, adjust=False).mean())
    df['ema20_day'] = df.groupby('date')['close'] \
                        .transform(lambda s: s.ewm(span=20, adjust=False).mean())
    df['ema50_day'] = df.groupby('date')['close'] \
                        .transform(lambda s: s.ewm(span=50, adjust=False).mean())

    # ----- Compute remaining row-based technical indicators -----
    df = add_sma_ema(df)         # row-based → shift
    df = add_macd(df)            # row-based → shift
    df = add_rsi(df)             # row-based → shift
    df = add_stochastic(df)      # row-based → shift
    df = add_bollinger_bands(df)
    df = add_vwap(df)

    # --------------------------------------------
    # 1) Add today's first 5-min candle features
    # --------------------------------------------
    df['date'] = df['start_timestamp'].dt.date
    first_candle = df.groupby('date').agg(
        day_1st_5min_open=('open', 'first'),
        day_1st_5min_high=('high', 'first'),
        day_1st_5min_low=('low', 'first'),
        day_1st_5min_close=('close', 'first'),
        day_1st_5min_volume=('volume', 'first')
    ).reset_index()
    df = df.merge(first_candle, on='date', how='left')

    # --------------------------------------------
    # 2) Add current candle OHLCV (latest_)
    # --------------------------------------------
    df['latest_5min_open'] = df['open']
    df['latest_5min_high'] = df['high']
    df['latest_5min_low'] = df['low']
    df['latest_5min_close'] = df['close']
    df['latest_5min_volume'] = df['volume']

    # ----- Identify columns to shift -----
    no_shift_cols = {
        'start_timestamp', 'end_timestamp', 'date',
        'open', 'high', 'low', 'close', 'volume',

        # today's first-candle snapshots (do not shift)
        'day_1st_5min_open', 'day_1st_5min_high', 'day_1st_5min_low',
        'day_1st_5min_close', 'day_1st_5min_volume',

        # daily EMA columns MUST NOT be shifted — they are used for "prevday" lookups.
        'ema5_day', 'ema20_day', 'ema50_day',

        # prevday _meta_ keys (already population-based) — keep them unshifted if present
        'ema5_prevday', 'ema5_prevday_tm1', 'ema5_prevday_tm3', 'ema5_prevday_tm5',
        'ema20_prevday', 'ema20_prevday_tm1', 'ema20_prevday_tm3', 'ema20_prevday_tm5',
        'ema50_prevday', 'ema50_prevday_tm1', 'ema50_prevday_tm3', 'ema50_prevday_tm5',
    }

    all_cols = set(df.columns)
    shift_cols = list(all_cols - no_shift_cols)

    # ----- Apply shift only to row-based indicators -----
    for c in shift_cols:
        df[c] = df[c].shift(1)

    return df

def compute_and_store_metas(conn, df_sym, df_nifty, df_day_sym, df_day_nifty):
    """
    Writes explicit columns into intraday_meta (hardcoded schema).
    Uses daily dataframes (df_day_sym / df_day_nifty) to source prev-day OHLC and daily EMAs.
    """
    cur = conn.cursor()

    # Find valid entry points
    valid_is = []
    for i, row in df_sym.iterrows():
        st = row['start_timestamp']
        current_date = st.date()
        if current_date < cutoff_date:
            continue
        t = time_of(st)
        if not (ENTRY_TIME_START <= t <= ENTRY_TIME_END):
            continue
        valid_is.append(i)

    meta_id_map = {}  # st -> id

    for i in valid_is:
        row = df_sym.iloc[i]
        st = row['start_timestamp']
        current_date = st.date()

        meta = {}

        # --------- PREV DAY OHLC FROM DAILY DF (avoid SQL edge cases) ---------
        try:
            if df_day_sym is not None and not df_day_sym.empty:
                prev_daily = df_day_sym[df_day_sym['date'] < current_date]
                if not prev_daily.empty:
                    last_daily = prev_daily.iloc[-1]
                    meta['prevday_open'] = float(last_daily['open'])
                    meta['prevday_high'] = float(last_daily['high'])
                    meta['prevday_low'] = float(last_daily['low'])
                    meta['prevday_close'] = float(last_daily['close'])
                    meta['prevday_volume'] = float(last_daily.get('volume', 0) or 0.0)
                else:
                    meta['prevday_open'] = meta['prevday_high'] = meta['prevday_low'] = meta['prevday_close'] = meta['prevday_volume'] = None
            else:
                meta['prevday_open'] = meta['prevday_high'] = meta['prevday_low'] = meta['prevday_close'] = meta['prevday_volume'] = None
        except Exception:
            meta['prevday_open'] = meta['prevday_high'] = meta['prevday_low'] = meta['prevday_close'] = meta['prevday_volume'] = None

        # --------- TODAY OHLC SO FAR (intraday df) ---------
        today_prev_mask = (df_sym['start_timestamp'].dt.date == current_date) & \
                          (df_sym['start_timestamp'] < st)
        sub = df_sym[today_prev_mask]
        if not sub.empty:
            meta['today_open']  = float(sub.iloc[0]['open'])
            meta['today_high']  = float(sub['high'].max())
            meta['today_low']   = float(sub['low'].min())
            meta['today_close'] = float(sub.iloc[-1]['close'])
            meta['today_volume'] = float(sub['volume'].sum())
        else:
            meta['today_open']  = None
            meta['today_high']  = None
            meta['today_low']   = None
            meta['today_close'] = None
            meta['today_volume'] = None

        # --------- NIFTY prev-day (from daily) and nifty today (from intraday) ----------
        try:
            if df_day_nifty is not None and not df_day_nifty.empty:
                prev_daily_n = df_day_nifty[df_day_nifty['date'] < current_date]
                if not prev_daily_n.empty:
                    last_n = prev_daily_n.iloc[-1]
                    meta['nifty_prev_open'] = float(last_n['open'])
                    meta['nifty_prev_high'] = float(last_n['high'])
                    meta['nifty_prev_low'] = float(last_n['low'])
                    meta['nifty_prev_close'] = float(last_n['close'])
                    meta['nifty_prev_volume'] = float(last_n.get('volume', 0) or 0.0)
                else:
                    meta['nifty_prev_open'] = meta['nifty_prev_high'] = meta['nifty_prev_low'] = meta['nifty_prev_close'] = meta['nifty_prev_volume'] = None
            else:
                meta['nifty_prev_open'] = meta['nifty_prev_high'] = meta['nifty_prev_low'] = meta['nifty_prev_close'] = meta['nifty_prev_volume'] = None

            nifty_today_prev_mask = (df_nifty['start_timestamp'].dt.date == current_date) & \
                                    (df_nifty['start_timestamp'] < st)
            subn = df_nifty[nifty_today_prev_mask]
            if not subn.empty:
                meta['nifty_today_open']  = float(subn.iloc[0]['open'])
                meta['nifty_today_high']  = float(subn['high'].max())
                meta['nifty_today_low']   = float(subn['low'].min())
                meta['nifty_today_close'] = float(subn.iloc[-1]['close'])
            else:
                meta['nifty_today_open'] = meta['nifty_today_high'] = None
                meta['nifty_today_low'] = meta['nifty_today_close'] = None

        except Exception:
            meta['nifty_prev_open'] = meta['nifty_prev_high'] = None
            meta['nifty_prev_low'] = meta['nifty_prev_close'] = None
            meta['nifty_prev_volume'] = None
            meta['nifty_today_open'] = meta['nifty_today_high'] = None
            meta['nifty_today_low'] = meta['nifty_today_close'] = None

        # -------------------------------------------------------
        #  Include all indicator & feature columns (from intraday df row)
        # -------------------------------------------------------
        ema_logicals = [
            ('ema5_5min',  ['ema5_5min', 'ema5']),
            ('ema20_5min', ['ema20_5min', 'ema20']),
            ('ema50_5min', ['ema50_5min', 'ema50'])
        ]

        for meta_key, candidate_cols in ema_logicals:
            found_col = None
            for c in candidate_cols:
                if c in df_sym.columns:
                    found_col = c
                    break

            if found_col is not None:
                try:
                    val = row.get(found_col)
                    meta[meta_key] = None if pd.isna(val) else float(val)
                except Exception:
                    meta[meta_key] = None
            else:
                meta[meta_key] = None

            # tm lookbacks (by integer index in the intraday series)
            for off in (1, 3, 5):
                tm_key = f"{meta_key}_tm{off}"
                idx = i - off
                if found_col is not None and idx >= 0:
                    try:
                        v = df_sym.iloc[idx].get(found_col)
                        meta[tm_key] = None if pd.isna(v) else float(v)
                    except Exception:
                        meta[tm_key] = None
                else:
                    meta[tm_key] = None

        # 2) expose intraday's emaX_day (these are intraday-grouped daily EMAs)
        for col in ('ema5_day', 'ema20_day', 'ema50_day'):
            if col in row.index:
                try:
                    meta[col] = None if pd.isna(row.get(col)) else float(row.get(col))
                except Exception:
                    meta[col] = None
            else:
                meta[col] = None

        # ---------- NEW: Prev-day daily-candle EMAs & tm1/tm3/tm5 from df_day_sym ----------
        try:
            if df_day_sym is not None and not df_day_sym.empty:
                prev_daily = df_day_sym[df_day_sym['date'] < current_date].reset_index(drop=True)
                if not prev_daily.empty:
                    L = len(prev_daily)
                    last_daily = prev_daily.iloc[-1]
                    # base prevday EMAs (from daily-candle EMAs)
                    meta['ema5_prevday']  = None if pd.isna(last_daily.get('ema5_day', np.nan)) else float(last_daily.get('ema5_day'))
                    meta['ema20_prevday'] = None if pd.isna(last_daily.get('ema20_day', np.nan)) else float(last_daily.get('ema20_day'))
                    meta['ema50_prevday'] = None if pd.isna(last_daily.get('ema50_day', np.nan)) else float(last_daily.get('ema50_day'))
                    # tm lookbacks: tm1 => day before prev_day (index L-2), tm3 => L-4, tm5 => L-6
                    tm_map = {1: L-2, 3: L-4, 5: L-6}
                    for off, idx in tm_map.items():
                        key_suffix = f"tm{off}"
                        if idx >= 0:
                            row_tm = prev_daily.iloc[idx]
                            meta[f"ema5_prevday_{key_suffix}"]  = None if pd.isna(row_tm.get('ema5_day', np.nan)) else float(row_tm.get('ema5_day'))
                            meta[f"ema20_prevday_{key_suffix}"] = None if pd.isna(row_tm.get('ema20_day', np.nan)) else float(row_tm.get('ema20_day'))
                            meta[f"ema50_prevday_{key_suffix}"] = None if pd.isna(row_tm.get('ema50_day', np.nan)) else float(row_tm.get('ema50_day'))
                        else:
                            meta[f"ema5_prevday_{key_suffix}"] = None
                            meta[f"ema20_prevday_{key_suffix}"] = None
                            meta[f"ema50_prevday_{key_suffix}"] = None
                else:
                    # no prev daily rows
                    for name in [
                        'ema5_prevday','ema5_prevday_tm1','ema5_prevday_tm3','ema5_prevday_tm5',
                        'ema20_prevday','ema20_prevday_tm1','ema20_prevday_tm3','ema20_prevday_tm5',
                        'ema50_prevday','ema50_prevday_tm1','ema50_prevday_tm3','ema50_prevday_tm5'
                    ]:
                        meta[name] = None
            else:
                for name in [
                    'ema5_prevday','ema5_prevday_tm1','ema5_prevday_tm3','ema5_prevday_tm5',
                    'ema20_prevday','ema20_prevday_tm1','ema20_prevday_tm3','ema20_prevday_tm5',
                    'ema50_prevday','ema50_prevday_tm1','ema50_prevday_tm3','ema50_prevday_tm5'
                ]:
                    meta[name] = None
        except Exception:
            for name in [
                'ema5_prevday','ema5_prevday_tm1','ema5_prevday_tm3','ema5_prevday_tm5',
                'ema20_prevday','ema20_prevday_tm1','ema20_prevday_tm3','ema20_prevday_tm5',
                'ema50_prevday','ema50_prevday_tm1','ema50_prevday_tm3','ema50_prevday_tm5'
            ]:
                meta[name] = None

        # 4) Other indicators & snapshots
        indicator_keys = [
            'macd','macd_signal','macd_hist',
            'rsi','stoch_k','stoch_d',
            'vwap',
            'bb_mid','bb_std','bb_upper','bb_lower',
            'day_1st_5min_open','day_1st_5min_high','day_1st_5min_low','day_1st_5min_close','day_1st_5min_volume',
            'latest_5min_open','latest_5min_high','latest_5min_low','latest_5min_close','latest_5min_volume',
            'ATR'  # we'll map to 'atr' column
        ]
        for k in indicator_keys:
            if k in row.index:
                try:
                    if k == 'ATR':
                        meta['atr'] = None if pd.isna(row.get('ATR')) else float(row.get('ATR'))
                    else:
                        meta[k if k != 'ATR' else 'atr'] = None if pd.isna(row.get(k)) else float(row.get(k))
                except Exception:
                    if k == 'ATR':
                        meta['atr'] = None
                    else:
                        meta[k] = None
            else:
                if k == 'ATR':
                    meta['atr'] = meta.get('atr', None)
                else:
                    meta[k] = None

        # Ensure meta_as_of (store entry timestamp)
        meta['meta_as_of'] = st.to_pydatetime()

        # ----------------------------
        # Now map meta to hardcoded columns and INSERT
        # ----------------------------
        columns = [
            'symbol', 'entry_timestamp',
            'prevday_open','prevday_high','prevday_low','prevday_close','prevday_volume',

            'ema5_5min','ema5_5min_tm1','ema5_5min_tm3','ema5_5min_tm5',
            'ema20_5min','ema20_5min_tm1','ema20_5min_tm3','ema20_5min_tm5',
            'ema50_5min','ema50_5min_tm1','ema50_5min_tm3','ema50_5min_tm5',

            'ema5_prevday','ema5_prevday_tm1','ema5_prevday_tm3','ema5_prevday_tm5',
            'ema20_prevday','ema20_prevday_tm1','ema20_prevday_tm3','ema20_prevday_tm5',
            'ema50_prevday','ema50_prevday_tm1','ema50_prevday_tm3','ema50_prevday_tm5',

            'today_open','today_high','today_low','today_close','today_volume',

            'nifty_prev_open','nifty_prev_high','nifty_prev_low','nifty_prev_close','nifty_prev_volume',

            'nifty_today_open','nifty_today_high','nifty_today_low','nifty_today_close',

            'macd','macd_signal','macd_hist',
            'rsi','stoch_k','stoch_d',
            'vwap',
            'bb_mid','bb_std','bb_upper','bb_lower',

            'day_1st_5min_open','day_1st_5min_high','day_1st_5min_low','day_1st_5min_close','day_1st_5min_volume',

            'latest_5min_open','latest_5min_high','latest_5min_low','latest_5min_close','latest_5min_volume',

            'atr'
        ]

        vals = []
        for col in columns:
            if col == 'symbol':
                vals.append(SYMBOL)
            elif col == 'entry_timestamp':
                vals.append(st.to_pydatetime())
            else:
                v = meta.get(col)
                if v is None:
                    vals.append(None)
                else:
                    # convert NaN/inf to None, else round floats
                    if isinstance(v, (int, float, np.integer, np.floating)):
                        vals.append(safe_round(v))
                    else:
                        # keep timestamps unchanged
                        if col == 'meta_as_of' and isinstance(v, (str,)):
                            try:
                                vals.append(pd.to_datetime(v).to_pydatetime())
                            except Exception:
                                vals.append(v)
                        else:
                            vals.append(v)

        placeholders = ",".join(["%s"] * len(vals))
        col_names_sql = ",".join([f"`{c}`" for c in columns])
        insert_sql = f"INSERT IGNORE INTO intraday_meta ({col_names_sql}) VALUES ({placeholders})"

        try:
            cur.execute(insert_sql, tuple(vals))
            conn.commit()
        except Exception as e:
            print(f"[ERROR] Issue inserting meta for {st}: {e}")
            continue

        select_id_q = "SELECT id FROM intraday_meta WHERE symbol = %s AND entry_timestamp = %s"
        try:
            cur.execute(select_id_q, (SYMBOL, st.to_pydatetime()))
            result = cur.fetchone()
            if result:
                meta_id_map[st] = result[0]
            else:
                print(f"[WARN] No id found for meta {st}")
        except Exception as e:
            print(f"[ERROR] Failed to fetch meta id for {st}: {e}")

    cur.close()
    print(f"[INFO] Computed and stored {len(meta_id_map)} unique metas.")
    return meta_id_map

# ---------- trading logic ----------
def simulate_for_params(df, strategy_name, target_param, stop_param):
    trades = []
    n = len(df)
    processed_bars = 0

    print(f"[INFO] Running strategy={strategy_name}, target={target_param}, stop={stop_param}")

    for i, row in df.iterrows():
        st = row['start_timestamp']
        current_date = st.date()
        if current_date < cutoff_date:
            continue
        t = time_of(st)
        if not (ENTRY_TIME_START <= t <= ENTRY_TIME_END):
            continue

        entry_price = float(row['open'])
        atr_at_entry = float(df.iloc[i]['ATR'])

        for direction in ('long', 'short'):

            if strategy_name.startswith("fixed"):
                target_price = entry_price * (1 + target_param) if direction == 'long' else entry_price * (1 - target_param)
                stop_price = entry_price * (1 - stop_param) if direction == 'long' else entry_price * (1 + stop_param)
            else:
                if atr_at_entry is None:
                    continue
                target_price = entry_price + atr_at_entry * target_param if direction == 'long' else entry_price - atr_at_entry * target_param
                stop_price = entry_price - atr_at_entry * stop_param if direction == 'long' else entry_price + atr_at_entry * stop_param

            trailing_enabled = strategy_name.endswith("trailing")

            highest_since_entry = entry_price
            lowest_since_entry = entry_price
            current_trail_stop = stop_price

            exited = False
            exit_ts = None
            exit_price = None
            exit_note = ""

            for j in range(i, n):
                bar = df.iloc[j]
                bar_open = float(bar['open'])
                bar_high = float(bar['high'])
                bar_low = float(bar['low'])
                bar_ts = bar['start_timestamp']
                bar_time = time_of(bar_ts)

                processed_bars += 1
                if processed_bars % LOG_EVERY_N_BARS == 0:
                    print(f"   [RUN] Processed {processed_bars} bars... entry={st}, dir={direction}")

                if bar_high > highest_since_entry:
                    highest_since_entry = bar_high
                if bar_low < lowest_since_entry:
                    lowest_since_entry = bar_low

                if trailing_enabled:
                    if strategy_name.startswith("fixed"):
                        if direction == 'long':
                            new_stop = highest_since_entry * (1 - stop_param)
                            if new_stop > current_trail_stop:
                                current_trail_stop = new_stop
                        else:
                            new_stop = lowest_since_entry * (1 + stop_param)
                            if new_stop < current_trail_stop:
                                current_trail_stop = new_stop
                    else:
                        local_atr = df.iloc[j]['ATR']
                        if local_atr is not None:
                            if direction == 'long':
                                new_stop = highest_since_entry - stop_param * local_atr
                                if new_stop > current_trail_stop:
                                    current_trail_stop = new_stop
                            else:
                                new_stop = lowest_since_entry + stop_param * local_atr
                                if new_stop < current_trail_stop:
                                    current_trail_stop = new_stop

                eff_stop = current_trail_stop if trailing_enabled else stop_price
                eff_target = target_price

                target_hit = False
                stop_hit = False

                if direction == 'long':
                    if bar_high >= eff_target:
                        target_hit = True
                    if bar_low <= eff_stop:
                        stop_hit = True
                else:
                    if bar_low <= eff_target:
                        target_hit = True
                    if bar_high >= eff_stop:
                        stop_hit = True

                if target_hit and stop_hit:
                    winner = 'stop'
                elif target_hit:
                    winner = 'target'
                elif stop_hit:
                    winner = 'stop'
                else:
                    winner = None

                if winner == 'target':
                    exit_price = eff_target
                    exit_ts = bar_ts
                    exit_note = "target_hit"
                    exited = True
                elif winner == 'stop':
                    exit_price = eff_stop
                    exit_ts = bar_ts
                    exit_note = "stop_hit"
                    exited = True

                if exited:
                    break

                if bar_time == MAX_EXIT_TIME:
                    exit_price = bar_open
                    exit_ts = bar_ts
                    exit_note = "forced_time_exit_max_time_bar"
                    exited = True
                    break

            if not exited:
                last_row = df.iloc[-1]
                exit_price = float(last_row['close'])
                exit_ts = last_row['start_timestamp']
                exit_note = "final_fallback_exit"

            pnl = exit_price - entry_price if direction == 'long' else entry_price - exit_price
            ppnl = pnl * 100 / entry_price

            trades.append({
                "symbol": SYMBOL,
                "entry_timestamp": st,
                "entry_price": entry_price,
                "direction": direction,
                "param_target": target_param,
                "target_price": target_price,
                "param_stoploss": stop_param,
                "stoploss_price": stop_price,
                "updated_stoploss_price": stop_price if not trailing_enabled else current_trail_stop,
                "exit_timestamp": exit_ts,
                "exit_price": exit_price,
                "exit_strategy": strategy_name,
                "pnl": pnl,
                "ppnl": ppnl,
                "notes": exit_note
            })

    print(f"[INFO] Completed strategy={strategy_name}, generated {len(trades)} trades.")
    return trades

def load_existing_meta_id_map(conn, symbol):
    """
    Load entry_timestamp → meta_id mapping from existing intraday_meta table.
    Returns: dict { datetime: int }
    """
    print("[INFO] Loading existing meta_id_map from intraday_meta...")

    cur = conn.cursor()
    cur.execute(
        "SELECT entry_timestamp, id FROM intraday_meta WHERE symbol = %s",
        (symbol,)
    )

    meta_id_map = {row[0]: row[1] for row in cur.fetchall()}
    cur.close()

    print(f"[INFO] Loaded {len(meta_id_map)} existing meta records.")
    return meta_id_map

# ---------- top-level run ----------
def run_backtest():
    conn = connect_mysql()
    ensure_trades_table_exists(conn)

    raw_df, raw_nifty = fetch_5min_data(conn, SYMBOL, NIFTY_SYMBOL)
    if raw_df.empty:
        print("[ERROR] No data found for symbol. Exiting.")
        return

    # prepare features (compute indicators & shift row-based ones)
    df_sym = prepare_features(raw_df, conn)
    df_nifty = prepare_features(raw_nifty, conn)
    # reset index to ensure integer positions match
    df_sym = df_sym.reset_index(drop=True)
    df_nifty = df_nifty.reset_index(drop=True)
    df = df_sym.copy()

    # ---------- NEW: fetch & prepare DAILY dataframes ----------
    df_day_sym = prepare_daily_features(fetch_day_data(conn, SYMBOL))
    df_day_nifty = prepare_daily_features(fetch_day_data(conn, NIFTY_SYMBOL))

    # Compute and store unique metas (now receives daily dataframes)
    # meta_id_map = compute_and_store_metas(conn, df_sym, df_nifty, df_day_sym, df_day_nifty)
    meta_id_map = load_existing_meta_id_map(conn, SYMBOL)

    all_param_sets = configs
    # print(f"[INFO] Running backtest for {len(all_param_sets)} parameter combinations...")

    all_trades = []
    for cfg in all_param_sets:
        strat  = cfg["exit_strategy"]
        target = cfg["target"]
        stop   = cfg["stoploss"]

        # Convert percentages ONLY for fixed types
        if strat.startswith("fixed"):
            tparam = target / 100.0      # convert 1.0% → 0.01
            sparam   = stop / 100.0        # convert 0.5% → 0.005
        else:
            tparam = target              # ATR multiplier (unchanged)
            sparam   = stop

        trades = simulate_for_params(df, strat, tparam, sparam)

        if trades:
            cur = conn.cursor()
            insert_q = (f"INSERT INTO {TRADES_TABLE} "
                        "(symbol, entry_timestamp, entry_price, direction, exit_timestamp, exit_price, exit_strategy, param_target, target_price, param_stoploss, stoploss_price, updated_stoploss_price, pnl, ppnl, notes, meta_id) "
                        "VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)")
            rows = []

            for tr in trades:
                rows.append((
                    tr['symbol'],
                    tr['entry_timestamp'].to_pydatetime(),
                    r2(tr['entry_price']),
                    tr['direction'],
                    tr['exit_timestamp'].to_pydatetime(),
                    r2(tr['exit_price']),
                    strat,
                    tparam * 100 if strat.startswith("fixed") else tparam,
                    r2(tr['target_price']),
                    sparam * 100 if strat.startswith("fixed") else sparam,
                    r2(tr['stoploss_price']),
                    r2(tr['updated_stoploss_price']),
                    r2(tr['pnl']),
                    r2(tr['ppnl']),
                    tr['notes'],
                    meta_id_map.get(tr['entry_timestamp'], None)
                ))

            cur.executemany(insert_q, rows)
            conn.commit()
            cur.close()

            print(f"[DB] Wrote {len(rows)} trades to DB for strategy={strat}")

        all_trades.extend(trades)

        if len(all_trades) % LOG_EVERY_N_TRADES == 0:
            print(f"[INFO] Accumulated trades so far: {len(all_trades)}")

    trades_df = pd.DataFrame(all_trades)
    if trades_df.empty:
        print("[INFO] No trades generated.")
        conn.close()
        return

    avg_ppnl = trades_df['ppnl'].mean()
    win_rate = (trades_df['ppnl'] > 0).sum() / len(trades_df)
    total_trades = len(trades_df)
    net_pnl = trades_df['pnl'].sum()

    print("\n=== BACKTEST SUMMARY ===")
    print(f"Symbol: {SYMBOL}")
    print(f"Total trades: {total_trades}")
    print(f"Avg ppnl per trade: {avg_ppnl*100:.3f}%")
    print(f"Win rate: {win_rate*100:.2f}%")
    print(f"Net PnL: {net_pnl:.4f}")

    print("\nPer-strategy summary:")
    for strat, group in trades_df.groupby('exit_strategy'):
        gsize = len(group)
        avg_pp = group['ppnl'].mean()
        wr = (group['ppnl'] > 0).sum() / gsize
        print(f"  {strat}: trades={gsize}, avg_ppnl={avg_pp:.4f}, win_rate={wr*100:.2f}%")

    conn.close()


if __name__ == "__main__":
    run_backtest()
