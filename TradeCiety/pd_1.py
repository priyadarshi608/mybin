import mysql.connector
import pandas as pd
import numpy as np
from datetime import time, datetime, timedelta
from itertools import product
import math
import json

cutoff_date = datetime(2025, 1, 1).date()

# ---------- LOGGING CONFIG ----------
LOG_EVERY_N_BARS = 500
LOG_EVERY_N_TRADES = 500

# ---------- CONFIG ----------
MYSQL_CONF = {
    "host": "localhost",
    "user": "root",
    "password": "root",
    "database": "market",
    "raise_on_warnings": True
}

SYMBOL = "ADANIENT"
NIFTY_SYMBOL = "NIFTY50"
DURATION = "5minute"

ATR_PERIOD = 14

ENTRY_TIME_START = time(9, 20)
ENTRY_TIME_END = time(15, 10)

MAX_EXIT_TIME = time(15, 15)

exit_strategies = ["fixed", "fixed_trailing", "atr_based", "atr_based_trailing"]

fixed_targets_pct = [0.0050, 0.010, 0.02]
fixed_stop_pct = [0.0025, 0.005, 0.01]

atr_target_mults = [1.0, 2.0]
atr_stop_mults = [0.5, 1.0]

TRADES_TABLE = "intraday_trades_5min"

# ---------- utility functions ----------
def r2(x):
    try:
        return round(float(x), 2)
    except:
        return x

def round_meta_values(obj):
    """Recursively round all numeric values inside a dict/list to 2 decimals."""
    if isinstance(obj, dict):
        return {k: round_meta_values(v) for k, v in obj.items()}
    elif isinstance(obj, list):
        return [round_meta_values(v) for v in obj]
    else:
        try:
            return round(float(obj), 2)
        except:
            return obj

def connect_mysql():
    print("[INFO] Connecting to MySQL...")
    return mysql.connector.connect(**MYSQL_CONF)

def ensure_trades_table_exists(conn):
    cur = conn.cursor()
    cur.execute("""
    CREATE TABLE IF NOT EXISTS intraday_meta_5min (
      id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
      symbol VARCHAR(32),
      entry_timestamp TIMESTAMP,
      meta JSON,
      UNIQUE KEY unique_symbol_ts (symbol, entry_timestamp)
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
      notes TEXT
    );
    """)
    conn.commit()
    cur.close()
    print(f"[INFO] Table '{TRADES_TABLE}' is ready.")

def fetch_5min_data_for_symbol(conn, symbol):
    """
    Fetch 5-minute rows for a symbol (including is_correct),
    return dataframe with start_timestamp etc.
    """
    q = ("""
        SELECT start_timestamp, end_timestamp, open, high, low, close, volume, is_correct
        FROM market_data
        WHERE symbol = %s AND duration = %s
        ORDER BY start_timestamp ASC
    """)
    df = pd.read_sql(q, conn, params=(symbol, DURATION))
    df['start_timestamp'] = pd.to_datetime(df['start_timestamp'])
    df['end_timestamp'] = pd.to_datetime(df['end_timestamp'])
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
    # For safety, drop 'date' if not needed later; but we'll keep date column (helpful)
    # df_sym.drop(columns=['date'], inplace=True)
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

def add_sma_ema(df, windows=[5,10,20,30,40,50]):
    df = df.copy()
    for w in windows:
        df[f'sma{w}'] = df['close'].rolling(window=w, min_periods=1).mean()
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

    # Step 1: Simple average for the first 'period'
    avg_gain = gain.rolling(period).mean()
    avg_loss = loss.rolling(period).mean()

    rsi_values = [np.nan] * len(df)

    # Step 2: Wilder smoothing
    for i in range(len(df)):
        if i < period:
            # Not enough data to compute proper RSI
            rsi_values[i] = np.nan
            continue

        if i == period:
            # First Wilder average = just the SMA values
            current_gain = avg_gain.iloc[i]
            current_loss = avg_loss.iloc[i]
        else:
            # Wilder's smoothing formula
            current_gain = (current_gain * (period - 1) + gain.iloc[i]) / period
            current_loss = (current_loss * (period - 1) + loss.iloc[i]) / period

        # Compute RS & RSI
        if current_loss == 0:
            rs = np.inf
            rsi = 100
        else:
            rs = current_gain / current_loss
            rsi = 100 - (100 / (1 + rs))

        rsi_values[i] = rsi

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
    # compute cumulative typical price * volume and cumulative volume per day
    df['tpv'] = ((df['high'] + df['low'] + df['close']) / 3.0) * df['volume']
    # group per date
    df['date'] = df['start_timestamp'].dt.date
    df['cum_tpv'] = df.groupby('date')['tpv'].cumsum()
    df['cum_vol'] = df.groupby('date')['volume'].cumsum()
    df['vwap'] = df['cum_tpv'] / df['cum_vol'].replace(0, np.nan)
    df.drop(columns=['tpv','cum_tpv','cum_vol'], inplace=True)
    df.drop(columns=['date'], inplace=True)
    return df

def add_pivots_daily(df, conn):
    """
    Compute pivot points using:
    1. Daily OHLC from DB when that day's row has is_correct = 1 and duration='day'
    2. Otherwise, fallback to intraday-aggregated OHLC

    Then shift by 1 day so today's rows use previous day's pivots.
    """
    df = df.copy()
    df['date'] = df['start_timestamp'].dt.date

    # --------------------------
    # 1. Load daily OHLC from DB
    # --------------------------
    daily_q = """
        SELECT date(start_timestamp) as trade_date, open, high, low, close, volume
        FROM market_data
        WHERE symbol = %s
          AND duration = 'day'
          AND is_correct = 1
        ORDER BY date(start_timestamp)
    """

    daily_df = pd.read_sql(daily_q, conn, params=(SYMBOL,))
    daily_df['trade_date'] = pd.to_datetime(daily_df['trade_date']).dt.date

    daily_df = daily_df.rename(columns={
        'trade_date': 'date',
        'open': 'd_open',
        'high': 'd_high',
        'low': 'd_low',
        'close': 'd_close',
        'volume': 'd_volume'
    })
    daily_df.set_index('date', inplace=True)

    # -----------------------------------------
    # 2. Build fallback intraday aggregated OHLC
    # -----------------------------------------
    intraday_daily = df.groupby('date').agg({
        'open': 'first',
        'high': 'max',
        'low': 'min',
        'close': 'last',
        'volume': 'sum'
    }).rename(columns={
        'open': 'd_open',
        'high': 'd_high',
        'low': 'd_low',
        'close': 'd_close',
        'volume': 'd_volume'
    })

    # ---------------------------------------------------
    # 3. Merge: DB daily OHLC overrides intraday OHLC
    # ---------------------------------------------------
    # combine_first keeps DB values where available
    final_daily = daily_df.combine_first(intraday_daily)

    # ---------------------------------------------------
    # 4. Compute pivots
    # ---------------------------------------------------
    daily = final_daily.copy()
    daily['PP'] = (daily['d_high'] + daily['d_low'] + daily['d_close']) / 3.0
    daily['R1'] = 2 * daily['PP'] - daily['d_low']
    daily['R2'] = daily['PP'] + (daily['d_high'] - daily['d_low'])
    daily['R3'] = daily['d_high'] + 2 * (daily['PP'] - daily['d_low'])
    daily['S1'] = 2 * daily['PP'] - daily['d_high']
    daily['S2'] = daily['PP'] - (daily['d_high'] - daily['d_low'])
    daily['S3'] = daily['d_low'] - 2 * (daily['d_high'] - daily['PP'])

    # Shift by 1 day: Today's row uses Yesterday's pivots
    daily = daily.shift(1)

    daily = daily.reset_index().rename(columns={'date': 'date_key'})

    # ---------------------------------------------------
    # 5. Merge back into main df
    # ---------------------------------------------------
    df = df.merge(daily, left_on='date', right_on='date_key', how='left')
    df.drop(columns=['date', 'date_key'], inplace=True)

    return df

# ---------- prepare full feature DataFrames ----------
def prepare_features(df, conn):
    """
    Prepare features for 5-minute bars:
    - Compute ATR, SMA/EMA, MACD, RSI, Stochastic → row-based → MUST shift
    - Compute VWAP → intraday cumulative → DO NOT shift
    - Compute pivots (previous-day) → already based on prior day → DO NOT shift

    After computing:
      shift only the row-based indicators by 1 bar
      so entry at row i uses indicators from row i-1.
    """
    df = df.copy().reset_index(drop=True)

    # ----- Compute all features -----
    df = compute_atr(df)         # row-based → shift
    df = add_sma_ema(df)         # row-based → shift
    df = add_macd(df)            # row-based → shift
    df = add_rsi(df)             # row-based → shift
    df = add_stochastic(df)      # row-based → shift
    df = add_bollinger_bands(df)
    df = add_vwap(df)

    df = add_pivots_daily(df, conn)    # DO NOT SHIFT (previous-day values)

    # --------------------------------------------
    # 1) Add today's first 5-min candle features
    # --------------------------------------------
    df['date'] = df['start_timestamp'].dt.date
    # Get first candle per day
    first_candle = df.groupby('date').agg(
        day_1st_5min_open=('open', 'first'),
        day_1st_5min_high=('high', 'first'),
        day_1st_5min_low=('low', 'first'),
        day_1st_5min_close=('close', 'first'),
        day_1st_5min_volume=('volume', 'first')
    ).reset_index()
    # Merge these into main df
    df = df.merge(first_candle, on='date', how='left')

    # --------------------------------------------
    # 2) Add current candle OHLCV with prefix
    # --------------------------------------------
    df['latest_5min_open'] = df['open']
    df['latest_5min_high'] = df['high']
    df['latest_5min_low'] = df['low']
    df['latest_5min_close'] = df['close']
    df['latest_5min_volume'] = df['volume']

    # ----- Identify columns to shift -----
    # Base columns that should NEVER shift
    no_shift_cols = {
        'start_timestamp', 'end_timestamp', 'date',
        'open', 'high', 'low', 'close', 'volume',
        'day_1st_5min_open', 'day_1st_5min_high', 'day_1st_5min_low', 'day_1st_5min_close', 'day_1st_5min_volume',
        'PP', 'R1', 'R2', 'R3', # pivot levels
        'S1', 'S2', 'S3'
    }

    # All columns after feature engineering
    all_cols = set(df.columns)

    # Row-based indicator columns = all minus no-shift ones
    shift_cols = list(all_cols - no_shift_cols)

    # ----- Apply shift only to row-based indicators -----
    for c in shift_cols:
        df[c] = df[c].shift(1)
    # print(df)
    # exit(0)

    return df

def compute_and_store_metas(conn, df_sym, df_nifty):
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

    # Now, for each valid i, compute meta, insert if not exists
    meta_id_map = {}  # st -> id
    insert_meta_q = """
    INSERT IGNORE INTO intraday_meta_5min (symbol, entry_timestamp, meta)
    VALUES (%s, %s, %s)
    """

    for i in valid_is:
        row = df_sym.iloc[i]
        st = row['start_timestamp']
        current_date = st.date()

        meta = {}

        # Previous day OHLCV and pivots
        for c in ('d_open','d_high','d_low','d_close','d_volume','PP','R1','R2','R3','S1','S2','S3'):
            meta[c] = None
            if c in row and not pd.isna(row[c]):
                meta[c] = float(row[c]) if not isinstance(row[c], pd.Timestamp) else None

        # Today's OHLC so far
        today_prev_mask = (df_sym['start_timestamp'].dt.date == current_date) & \
                          (df_sym['start_timestamp'] < st)
        sub = df_sym[today_prev_mask]
        if not sub.empty:
            meta['today_open']  = float(sub.iloc[0]['open'])
            meta['today_high']  = float(sub['high'].max())
            meta['today_low']   = float(sub['low'].min())
            meta['today_close'] = float(sub.iloc[-1]['close'])
        else:
            meta['today_open']  = None
            meta['today_high']  = None
            meta['today_low']   = None
            meta['today_close'] = None

        # Previous day NIFTY + today's NIFTY
        try:
            prev_date = (current_date - timedelta(days=1))
            nifty_prev = df_nifty[df_nifty['start_timestamp'].dt.date == prev_date]
            if not nifty_prev.empty:
                meta['nifty_prev_open']   = float(nifty_prev.iloc[0]['open'])
                meta['nifty_prev_high']   = float(nifty_prev['high'].max())
                meta['nifty_prev_low']    = float(nifty_prev['low'].min())
                meta['nifty_prev_close']  = float(nifty_prev.iloc[-1]['close'])
                meta['nifty_prev_volume'] = int(nifty_prev['volume'].sum())
            else:
                meta['nifty_prev_open'] = meta['nifty_prev_high'] = None
                meta['nifty_prev_low'] = meta['nifty_prev_close'] = None
                meta['nifty_prev_volume'] = None

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
        #  NEW: Include all indicator & feature columns
        #       including day_1st_5min_* and latest_5min_*
        # -------------------------------------------------------
        indicator_keys = [
            c for c in row.index
            if c.startswith((
                'sma','ema','macd','macd_signal','macd_hist',
                'rsi','stoch','vwap','PP','R1','R2','R3','S1','S2','S3',
                'bb_',
                'day_1st_5min_',      # <-- NEW
                'latest_5min_'        # <-- NEW
            ))
        ]

        for k in indicator_keys:
            val = row[k]
            meta[k] = None if pd.isna(val) else float(val)

        # Ensure ATR is included (shifted)
        meta['atr'] = float(row['ATR']) if ('ATR' in row and not pd.isna(row['ATR'])) else None

        # Add timestamp
        meta['meta_as_of'] = st.isoformat()

        # Store meta in DB
        try:
            cur.execute(insert_meta_q, (SYMBOL, st.to_pydatetime(), json.dumps(round_meta_values(meta), default=str)))
            conn.commit()
        except mysql.connector.IntegrityError:
            pass
        except Exception as e:
            print(f"[ERROR] Issue inserting meta for {st}: {e}")
            continue

        # Fetch meta_id
        select_id_q = "SELECT id FROM intraday_meta_5min WHERE symbol = %s AND entry_timestamp = %s"
        cur.execute(select_id_q, (SYMBOL, st.to_pydatetime()))
        result = cur.fetchone()
        if result:
            meta_id_map[st] = result[0]
        else:
            print(f"[WARN] No id found for meta {st}")

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
        # print(row)
        st = row['start_timestamp']
        date_str = st.strftime("%Y-%m-%d")
        current_date = st.date()
        if current_date < cutoff_date:
            continue
        t = time_of(st)
        if not (ENTRY_TIME_START <= t <= ENTRY_TIME_END):
            continue

        entry_price = float(row['open'])
        # atr_at_entry = float(row['ATR']) if 'ATR' in df.columns and not math.isnan(row['ATR']) else None
        # atr_at_entry = float(df.iloc[i-1]['ATR']) if i > 0 else None
        atr_at_entry = float(df.iloc[i]['ATR'])

        for direction in ('long', 'short'):

            if strategy_name.startswith("fixed"):
                target_price = entry_price * (1 + target_param) if direction == 'long' else entry_price * (1 - target_param)
                stop_price = entry_price * (1 - stop_param) if direction == 'long' else entry_price * (1 + stop_param)
            else:
                # if atr_at_entry is None or atr_at_entry == 0:
                if atr_at_entry is None:
                    continue
                target_price = entry_price + atr_at_entry * target_param if direction == 'long' else entry_price - atr_at_entry * target_param
                stop_price = entry_price - atr_at_entry * stop_param if direction == 'long' else entry_price + atr_at_entry * stop_param
                # print("t: " + str(t) + ", atr_at_entry: " + str(atr_at_entry) + ", target_price: " + str(target_price) + ", stop_price: " + str(stop_price))

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

                # print("t: " + str(t))
                if trailing_enabled:
                    # print("trailing_enabled: " + str(trailing_enabled))
                    # print("strategy_name: " + str(strategy_name))
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
                        # local_atr = float(bar['ATR']) if ('ATR' in df.columns and not math.isnan(bar['ATR'])) else atr_at_entry
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
                # print("eff_stop: " + str(eff_stop))
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
                    if direction == 'long':
                        dist_target = abs(eff_target - entry_price)
                        dist_stop = abs(entry_price - eff_stop)
                    else:
                        dist_target = abs(entry_price - eff_target)
                        dist_stop = abs(eff_stop - entry_price)
                    # winner = 'target' if dist_target <= dist_stop else 'stop'
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

                # if bar_time > MAX_EXIT_TIME:
                #     exit_price = float(df.iloc[j-1]['open']) if j-1 >= 0 else float(bar['close'])
                #     exit_ts = df.iloc[j-1]['start_timestamp'] if j-1 >= 0 else bar_ts
                #     exit_note = "forced_time_exit_past"
                #     exited = True
                #     break

                # if bar_time == MAX_EXIT_TIME and j == n-1:
                #     exit_price = bar_open
                #     exit_ts = bar_ts
                #     exit_note = "forced_time_exit_lastbar"
                #     exited = True
                #     break

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


# ---------- top-level run ----------
def run_backtest():
    conn = connect_mysql()
    ensure_trades_table_exists(conn)

    raw_df, raw_nifty = fetch_5min_data(conn, SYMBOL, NIFTY_SYMBOL)
    if raw_df.empty:
        print("[ERROR] No data found for symbol. Exiting.")
        return

    # raw_df_copy = raw_df.copy()
    # df = compute_atr(raw_df_copy, period=ATR_PERIOD)
    # df = df.reset_index(drop=True)

    # prepare features (compute indicators & shift row-based ones)
    df_sym = prepare_features(raw_df, conn)
    df_nifty = prepare_features(raw_nifty, conn)
    # reset index to ensure integer positions match
    df_sym = df_sym.reset_index(drop=True)
    df_nifty = df_nifty.reset_index(drop=True)
    df = df_sym.copy()
    # print(df['ATR'])
    # print(df_sym['ATR'])
    # exit(0)

    # Compute and store unique metas
    meta_id_map = compute_and_store_metas(conn, df_sym, df_nifty)

    all_param_sets = []
    for strat in exit_strategies:
        if strat.startswith("fixed"):
            for t_pct, s_pct in product(fixed_targets_pct, fixed_stop_pct):
                all_param_sets.append((strat, t_pct, s_pct))
        else:
            for t_mul, s_mul in product(atr_target_mults, atr_stop_mults):
                all_param_sets.append((strat, t_mul, s_mul))

    print(f"[INFO] Running backtest for {len(all_param_sets)} parameter combinations...")

    all_trades = []
    for strat, tparam, sparam in all_param_sets:

        trades = simulate_for_params(df, strat, tparam, sparam)

        if trades:
            cur = conn.cursor()
            # insert_q = (f"INSERT INTO {TRADES_TABLE} "
            #             "(symbol, entry_timestamp, entry_price, direction, exit_timestamp, exit_price, exit_strategy, param_target, target_price, param_stoploss, stoploss_price, updated_stoploss_price, pnl, ppnl, notes) "
            #             "VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)")
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
                    r2(tparam),
                    r2(tr['target_price']),
                    r2(sparam),
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
