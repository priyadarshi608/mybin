import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split, GridSearchCV
from xgboost import XGBRegressor
from sqlalchemy import create_engine
import datetime
import warnings
import inspect

# Suppress any residual warnings (optional, as SQLAlchemy should eliminate the specific warning)
warnings.filterwarnings("ignore", category=UserWarning)

# Function to calculate RSI
def calculate_rsi(series, period=5):
    if len(series) < period:
        return np.nan
    delta = series.diff()
    gain = delta.where(delta > 0, 0).rolling(window=period).mean()
    loss = -delta.where(delta < 0, 0).rolling(window=period).mean()
    rs = gain / loss
    rs = rs.replace([np.inf, -np.inf], np.nan)
    rsi = 100 - 100 / (1 + rs)
    return rsi.iloc[-1] if not pd.isna(rsi.iloc[-1]) else np.nan

# Function to calculate MACD histogram
def calculate_macd(series, short=6, long=13, signal=4):
    if len(series) < long:
        return np.nan
    ema_short = series.ewm(span=short, adjust=False).mean()
    ema_long = series.ewm(span=long, adjust=False).mean()
    macd = ema_short - ema_long
    signal_line = macd.ewm(span=signal, adjust=False).mean()
    hist = macd.iloc[-1] - signal_line.iloc[-1]
    return hist if not pd.isna(hist) else np.nan

# Function to log errors with timestamp and line number
def log_error(msg):
    line_no = inspect.currentframe().f_back.f_lineno
    print(f"[{datetime.datetime.now()}] ERROR (Line {line_no}): {msg}")

# Function to log debug information with timestamp and line number
def log_debug(msg):
    line_no = inspect.currentframe().f_back.f_lineno
    print(f"[{datetime.datetime.now()}] DEBUG (Line {line_no}): {msg}")

# Function to get pre-position features
def get_pre_features(row, conn, daily_data, all_symbols):
    trade_date = row['trade_date']
    pos_ts = row['position_timestamp']
    symbol = row['symbol']
    feat = {}
    try:
        feat['dow'] = trade_date.weekday()
        feat['hour'] = pos_ts.hour
        feat['minute'] = pos_ts.minute
        for sym in all_symbols:
            df = daily_data.get(sym, pd.DataFrame())
            if df.empty:
                log_debug(f"No daily data for {sym} on {trade_date}")
                feat[f'{sym}_1d_ret'] = 0
                feat[f'{sym}_5d_ret'] = 0
                feat[f'{sym}_vol'] = 0
                feat[f'{sym}_rsi'] = 50  # Neutral RSI
                feat[f'{sym}_macd'] = 0
                continue
            df_before = df[df['end_timestamp'] < pos_ts]
            if len(df_before) < 5:
                log_debug(f"Insufficient daily data for {sym} before {pos_ts}")
                feat[f'{sym}_1d_ret'] = 0
                feat[f'{sym}_5d_ret'] = 0
                feat[f'{sym}_vol'] = 0
                feat[f'{sym}_rsi'] = 50
                feat[f'{sym}_macd'] = 0
                continue
            closes = df_before['close'].tail(15)
            returns = closes.pct_change().dropna()
            feat[f'{sym}_1d_ret'] = returns.iloc[-1] if len(returns) > 0 else 0
            feat[f'{sym}_5d_ret'] = returns.tail(5).mean() if len(returns) >= 5 else 0
            feat[f'{sym}_vol'] = returns.tail(5).std() if len(returns) >= 5 else 0
            feat[f'{sym}_rsi'] = calculate_rsi(closes) if not pd.isna(calculate_rsi(closes)) else 50
            feat[f'{sym}_macd'] = calculate_macd(closes) if not pd.isna(calculate_macd(closes)) else 0
        date_str = trade_date.strftime('%Y-%m-%d')
        for sym in all_symbols:
            query = f"SELECT * FROM market_data WHERE symbol='{sym}' AND duration='minute' AND date(start_timestamp)='{date_str}' AND end_timestamp < '{pos_ts}' ORDER BY end_timestamp"
            print(query)
            try:
                min_df = pd.read_sql(query, conn)
            except Exception as e:
                log_error(f"Failed to fetch minute data for {sym} on {date_str}: {e}")
                min_df = pd.DataFrame()
            if len(min_df) < 5:
                log_debug(f"Insufficient minute data for {sym} on {date_str} before {pos_ts}")
                feat[f'{sym}_intra_rsi'] = 50
                feat[f'{sym}_intra_macd'] = 0
                feat[f'{sym}_intra_ret'] = 0
                feat[f'{sym}_intra_vol'] = 0
                continue
            closes = min_df['close']
            feat[f'{sym}_intra_rsi'] = calculate_rsi(closes) if not pd.isna(calculate_rsi(closes)) else 50
            feat[f'{sym}_intra_macd'] = calculate_macd(closes) if not pd.isna(calculate_macd(closes)) else 0
            open_price = min_df['open'].iloc[0]
            current = min_df['close'].iloc[-1]
            feat[f'{sym}_intra_ret'] = (current - open_price) / open_price if open_price != 0 else 0
            feat[f'{sym}_intra_vol'] = min_df['volume'].sum() if not min_df.empty else 0
    except Exception as e:
        log_error(f"Error computing pre features for {trade_date}: {e}")
    return feat

# Create SQLAlchemy engine
try:
    engine = create_engine('mysql+mysqlconnector://root:root@localhost/market')
    conn = engine.connect()
except Exception as e:
    log_error(f"Database connection failed: {e}")
    exit(1)

# Fetch trades
try:
    trades_df = pd.read_sql("SELECT * FROM daily_trades ORDER BY trade_date", conn)
    log_debug(f"Fetched {len(trades_df)} trades from daily_trades")
except Exception as e:
    log_error(f"Failed to fetch daily_trades: {e}")
    conn.close()
    exit(1)

# Symbols
stocks = ['AXISBANK', 'ICICIBANK', 'HDFCBANK']
indices = ['NIFTY50', 'SENSEX']
all_symbols = stocks + indices

# Fetch daily data
daily_data = {}
for sym in all_symbols:
    query = f"SELECT * FROM market_data WHERE symbol='{sym}' AND duration='day' ORDER BY end_timestamp"
    print(query)
    try:
        daily_data[sym] = pd.read_sql(query, conn)
        log_debug(f"Fetched {len(daily_data[sym])} daily records for {sym}")
    except Exception as e:
        log_error(f"Failed to fetch daily data for {sym}: {e}")
        daily_data[sym] = pd.DataFrame()

# Prepare pre-position dataset
pre_features = []
labels = []
trade_dates = []
pos_tss = []
for idx, row in trades_df.iterrows():
    feat = get_pre_features(row, conn, daily_data, all_symbols)
    pre_features.append(feat)
    labels.append(row['pp'])
    trade_dates.append(row['trade_date'])
    pos_tss.append(row['position_timestamp'])

pre_df = pd.DataFrame(pre_features)
pre_df['pp'] = labels
pre_df['trade_date'] = trade_dates
pre_df['pos_ts'] = pos_tss
log_debug(f"Pre-position dataset size before dropna: {len(pre_df)}")
pre_df.fillna({'rsi': 50, 'macd': 0, 'ret': 0, 'vol': 0}, inplace=True)
pre_df.dropna(subset=['pp', 'trade_date', 'pos_ts'], inplace=True)
log_debug(f"Pre-position dataset size after dropna: {len(pre_df)}")

if len(pre_df) < 5:
    log_error("Insufficient data for training pre-position model (less than 5 rows)")
    conn.close()
    exit(1)

pre_df.sort_values('trade_date', inplace=True)
train_size = int(0.8 * len(pre_df))
train_pre = pre_df.iloc[:train_size]
test_pre = pre_df.iloc[train_size:]

X_train_pre = train_pre.drop(['pp', 'trade_date', 'pos_ts'], axis=1)
y_train_pre = train_pre['pp']
X_test_pre = test_pre.drop(['pp', 'trade_date', 'pos_ts'], axis=1)
y_test_pre = test_pre['pp']

# Train pre-position model with grid search
param_grid = {'n_estimators': [50, 100], 'max_depth': [3, 5]}
grid = GridSearchCV(XGBRegressor(objective='reg:squarederror'), param_grid, cv=3)
try:
    grid.fit(X_train_pre, y_train_pre)
    pre_model = grid.best_estimator_
    log_debug(f"Pre-position model trained with best params: {grid.best_params_}")
except Exception as e:
    log_error(f"Pre-position model training failed: {e}")
    conn.close()
    exit(1)

# Prepare early exit dataset
early_features = []
early_labels = []
for idx, row in trades_df.iterrows():
    trade_date = row['trade_date']
    pos_ts = row['position_timestamp']
    date_str = trade_date.strftime('%Y-%m-%d')
    min_dfs = {}
    for sym in all_symbols:
        query = f"SELECT * FROM market_data WHERE symbol='{sym}' AND duration='minute' AND date(start_timestamp)='{date_str}' ORDER BY start_timestamp"
        print(query)
        try:
            min_dfs[sym] = pd.read_sql(query, conn)
            log_debug(f"Fetched {len(min_dfs[sym])} minute records for {sym} on {date_str}")
        except Exception as e:
            log_error(f"Failed to fetch minute data for {sym} on {date_str}: {e}")
            min_dfs[sym] = pd.DataFrame()
    stock_min = min_dfs.get('AXISBANK', pd.DataFrame())
    if stock_min.empty:
        log_debug(f"No minute data for AXISBANK on {date_str}")
        continue
    stock_min_after = stock_min[stock_min['start_timestamp'] >= pos_ts]
    for m_idx, m_row in stock_min_after.iterrows():
        curr_ts = m_row['end_timestamp']
        curr_price = m_row['close']
        feat = {}
        try:
            feat['minutes_since_pos'] = (curr_ts - pos_ts).total_seconds() / 60
            feat['current_pp'] = (curr_price - row['position_price']) / row['position_price'] * 100 if row['position_price'] != 0 else 0
            for sym in all_symbols:
                sym_df = min_dfs.get(sym, pd.DataFrame())
                if sym_df.empty:
                    feat[f'{sym}_short_rsi'] = 50
                    feat[f'{sym}_short_macd'] = 0
                    feat[f'{sym}_short_ret'] = 0
                    feat[f'{sym}_volume'] = 0
                    continue
                sym_before = sym_df[sym_df['end_timestamp'] <= curr_ts]
                if len(sym_before) < 5:
                    feat[f'{sym}_short_rsi'] = 50
                    feat[f'{sym}_short_macd'] = 0
                    feat[f'{sym}_short_ret'] = 0
                    feat[f'{sym}_volume'] = 0
                    continue
                closes = sym_before['close'].tail(15)
                feat[f'{sym}_short_rsi'] = calculate_rsi(closes, 5) if not pd.isna(calculate_rsi(closes, 5)) else 50
                feat[f'{sym}_short_macd'] = calculate_macd(closes) if not pd.isna(calculate_macd(closes)) else 0
                feat[f'{sym}_short_ret'] = closes.pct_change().tail(5).mean() if len(closes) >= 6 else 0
                feat[f'{sym}_volume'] = sym_before['volume'].tail(5).sum() if len(sym_before) >= 5 else 0
        except Exception as e:
            log_error(f"Error computing early features for {trade_date} at {curr_ts}: {e}")
            continue
        early_features.append(feat)
        early_labels.append(row['pp'])

early_df = pd.DataFrame(early_features)
early_df['pp'] = early_labels
log_debug(f"Early exit dataset size before dropna: {len(early_df)}")
early_df.fillna({'rsi': 50, 'macd': 0, 'ret': 0, 'vol': 0, 'volume': 0}, inplace=True)
early_df.dropna(subset=['pp'], inplace=True)
log_debug(f"Early exit dataset size after dropna: {len(early_df)}")

if len(early_df) < 5:
    log_error("Insufficient data for training early exit model (less than 5 rows)")
    conn.close()
    exit(1)

X_early = early_df.drop('pp', axis=1)
y_early = early_df['pp']
X_train_early, X_test_early, y_train_early, y_test_early = train_test_split(X_early, y_early, test_size=0.2, random_state=42)

# Train early exit model with grid search
grid = GridSearchCV(XGBRegressor(objective='reg:squarederror'), param_grid, cv=3)
try:
    grid.fit(X_train_early, y_train_early)
    early_model = grid.best_estimator_
    log_debug(f"Early exit model trained with best params: {grid.best_params_}")
except Exception as e:
    log_error(f"Early exit model training failed: {e}")
    conn.close()
    exit(1)

# Backtesting
try:
    with conn.begin():
        conn.execute("DELETE FROM daily_trades_adapted")
except Exception as e:
    log_error(f"Failed to clear daily_trades_adapted: {e}")

affected = 0
new_pps = []
version_number = 1
insert_query = """
INSERT INTO daily_trades_adapted (symbol, trade_date, version_number, open_price, position_price, position_timestamp, close_price, exit_price, exit_timestamp, position, pp)
VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
"""

for idx, row in trades_df.iterrows():
    feat = get_pre_features(row, conn, daily_data, all_symbols)
    feat_df = pd.DataFrame([feat]).fillna({'rsi': 50, 'macd': 0, 'ret': 0, 'vol': 0})
    try:
        pred_pp = pre_model.predict(feat_df)[0]
    except Exception as e:
        log_error(f"Pre-position prediction failed for {row['trade_date']}: {e}")
        pred_pp = row['pp']  # Fallback to take
    original_pp = row['pp']
    position_taken = True
    if pred_pp < 0 and original_pp < 0:  # Only skip loss-making trades
        values = (row['symbol'], row['trade_date'], version_number, row['open_price'], row['position_price'], row['position_timestamp'], row['close_price'], None, None, '0', 0.00)
        try:
            conn.execute(insert_query, values)
            new_pps.append(0.00)
            position_taken = False
            affected += 1
        except Exception as e:
            log_error(f"Insert failed for skipped trade {row['trade_date']}: {e}")
    else:
        date_str = row['trade_date'].strftime('%Y-%m-%d')
        min_dfs = {}
        for sym in all_symbols:
            query = f"SELECT * FROM market_data WHERE symbol='{sym}' AND duration='minute' AND date(start_timestamp)='{date_str}' ORDER BY start_timestamp"
            print(query)
            try:
                min_dfs[sym] = pd.read_sql(query, conn)
            except Exception as e:
                log_error(f"Failed to fetch minute data for {sym} on {date_str}: {e}")
                min_dfs[sym] = pd.DataFrame()
        stock_min = min_dfs.get('AXISBANK', pd.DataFrame())
        if stock_min.empty:
            values = (row['symbol'], row['trade_date'], version_number, row['open_price'], row['position_price'], row['position_timestamp'], row['close_price'], row['exit_price'], row['exit_timestamp'], row['position'], original_pp)
            try:
                conn.execute(insert_query, values)
                new_pps.append(original_pp)
            except Exception as e:
                log_error(f"Insert failed for {row['trade_date']}: {e}")
            continue
        stock_min_after = stock_min[stock_min['start_timestamp'] >= row['position_timestamp']]
        exit_price = row['exit_price']
        exit_ts = row['exit_timestamp']
        pp = original_pp
        pos_price = row['position_price']
        early_exited = False
        for m_idx, m_row in stock_min_after.iterrows():
            curr_ts = m_row['end_timestamp']
            if curr_ts >= row['exit_timestamp']:
                break
            curr_price = m_row['close']
            early_feat = {}
            try:
                early_feat['minutes_since_pos'] = (curr_ts - row['position_timestamp']).total_seconds() / 60
                early_feat['current_pp'] = (curr_price - pos_price) / pos_price * 100 if pos_price != 0 else 0
                for sym in all_symbols:
                    sym_df = min_dfs.get(sym, pd.DataFrame())
                    if sym_df.empty:
                        early_feat[f'{sym}_short_rsi'] = 50
                        early_feat[f'{sym}_short_macd'] = 0
                        early_feat[f'{sym}_short_ret'] = 0
                        early_feat[f'{sym}_volume'] = 0
                        continue
                    sym_before = sym_df[sym_df['end_timestamp'] <= curr_ts]
                    if len(sym_before) < 5:
                        early_feat[f'{sym}_short_rsi'] = 50
                        early_feat[f'{sym}_short_macd'] = 0
                        early_feat[f'{sym}_short_ret'] = 0
                        early_feat[f'{sym}_volume'] = 0
                        continue
                    closes = sym_before['close'].tail(15)
                    early_feat[f'{sym}_short_rsi'] = calculate_rsi(closes, 5) if not pd.isna(calculate_rsi(closes, 5)) else 50
                    early_feat[f'{sym}_short_macd'] = calculate_macd(closes) if not pd.isna(calculate_macd(closes)) else 0
                    early_feat[f'{sym}_short_ret'] = closes.pct_change().tail(5).mean() if len(closes) >= 6 else 0
                    early_feat[f'{sym}_volume'] = sym_before['volume'].tail(5).sum() if len(sym_before) >= 5 else 0
                early_feat_df = pd.DataFrame([early_feat]).fillna({'rsi': 50, 'macd': 0, 'ret': 0, 'volume': 0})
                pred_pp = early_model.predict(early_feat_df)[0]
                if pred_pp < -0.5 and original_pp < 0:  # Only exit early for loss-making trades
                    exit_price = curr_price
                    exit_ts = curr_ts
                    pp = (exit_price - pos_price) / pos_price * 100 if pos_price != 0 else 0
                    early_exited = True
                    break
            except Exception as e:
                log_error(f"Early exit prediction failed for {row['trade_date']} at {curr_ts}: {e}")
                continue
        values = (row['symbol'], row['trade_date'], version_number, row['open_price'], pos_price, row['position_timestamp'], row['close_price'], exit_price, exit_ts, row['position'], pp)
        try:
            conn.execute(insert_query, values)
            new_pps.append(pp)
            if early_exited:
                affected += 1
        except Exception as e:
            log_error(f"Insert failed for {row['trade_date']}: {e}")

# Commit all changes
try:
    conn.commit()
except Exception as e:
    log_error(f"Failed to commit changes: {e}")

# Compute and print results
avg_pp = np.mean(new_pps) if new_pps else 0.0
print(f"Final average pp: {avg_pp:.6f}%")
print(f"Number of affected trades: {affected}")

# Close connection
conn.close()

# Return average pp as float
print(avg_pp / 100)  # Convert percentage to float (e.g., 0.85 for 0.85%)
