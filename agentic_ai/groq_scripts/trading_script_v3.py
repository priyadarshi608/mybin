import mysql.connector
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from xgboost import XGBRegressor, XGBClassifier
from datetime import datetime, timedelta
import logging
import sys
from sklearn.model_selection import GridSearchCV

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(sys.stdout),
        logging.FileHandler(f'trading_script_v3.log')
    ]
)
logger = logging.getLogger(__name__)

# Version number (will be replaced dynamically)
3 = 3

try:
    logger.info("Connecting to MySQL database 'market'")
    conn = mysql.connector.connect(user='root', password='root', host='localhost', database='market')
    cursor = conn.cursor()
    logger.info("Database connection established")
except mysql.connector.Error as err:
    logger.error(f"Database connection error: {err}")
    sys.exit(1)

try:
    logger.info("Fetching data from daily_trades for AXISBANK")
    df_trades = pd.read_sql("SELECT * FROM daily_trades WHERE symbol = 'AXISBANK'", conn)
    logger.info(f"Retrieved {len(df_trades)} trades")
    df_trades['trade_date'] = pd.to_datetime(df_trades['trade_date'])
    df_trades['position_timestamp'] = pd.to_datetime(df_trades['position_timestamp'])
    df_trades['exit_timestamp'] = pd.to_datetime(df_trades['exit_timestamp'])
    df_trades.sort_values('trade_date', inplace=True)
    logger.info("Processed daily_trades data")

    df_trades['loss'] = (df_trades['pp'] < 0).astype(int)
    logger.info("Added loss labels")

    def get_pre_features(row, conn):
        logger.debug(f"Computing features for trade on {row['trade_date']}")
        trade_date_str = row['trade_date'].strftime('%Y-%m-%d')
        position_ts = row['position_timestamp']

        query = f"SELECT * FROM market_data WHERE symbol='AXISBANK' AND duration='1d' AND date(start_timestamp) < '{trade_date_str}' ORDER BY start_timestamp DESC LIMIT 60"
        df_past = pd.read_sql(query, conn)
        if len(df_past) < 60:
            logger.warning(f"Insufficient past data for AXISBANK on {trade_date_str}: {len(df_past)} days")
            return None

        query_index = f"SELECT * FROM market_data WHERE symbol='NIFTY50' AND duration='1d' AND date(start_timestamp) < '{trade_date_str}' ORDER BY start_timestamp DESC LIMIT 60"
        df_index = pd.read_sql(query_index, conn)
        series_index = df_index['close']

        macd = macd_crossover(df_past)
        more_past_returns = additional_past_returns(df_past, 90)
        icici_data = related_stock_data('ICICIBANK', df_past, conn, trade_date_str)
        hdfc_data = related_stock_data('HDFCBANK', df_past, conn, trade_date_str)
        sensex_data = related_stock_data('SENSEX', df_past, conn, trade_date_str)

        series = df_past['close']
        delta = series.diff()
        gain = delta.clip(lower=0).rolling(window=21).mean()
        loss = -delta.clip(upper=0).rolling(window=21).mean()
        rs = gain / loss
        rsi = 100 - 100 / (1 + rs)
        rsi_val = rsi.iloc[-1] if not np.isnan(rsi.iloc[-1]) else 50

        ret1 = (series.iloc[-1] - series.iloc[-2]) / series.iloc[-2] if len(series) >= 2 else 0
        ret5 = (series.iloc[-1] - series.iloc[-5]) / series.iloc[-5] if len(series) >= 5 else 0
        ret10 = (series.iloc[-1] - series.iloc[-10]) / series.iloc[-10] if len(series) >= 10 else 0
        pct_changes = series.pct_change().dropna()
        vol = pct_changes.std() if not pct_changes.empty else 0

        ret1_index = (series_index.iloc[-1] - series_index.iloc[-2]) / series_index.iloc[-2] if len(series_index) >= 2 else 0

        time_frac = (position_ts.hour * 60 + position_ts.minute) / (15 * 60 + 30)
        features = {
            'rsi': rsi_val,
            'ret1': ret1,
            'ret5': ret5,
            'ret10': ret10,
            'vol': vol,
            'ret1_index': ret1_index,
            'time_frac': time_frac,
            'macd': macd,
            'more_past_returns': more_past_returns,
            'icici_data': icici_data,
            'hdfc_data': hdfc_data,
            'sensex_data': sensex_data
        }
        logger.debug(f"Features computed: {features}")
        return features

    def macd_crossover(df):
        short_window = 12
        long_window = 26
        signal_window = 9

        short_ema = df['close'].ewm(span=short_window, adjust=False).mean()
        long_ema = df['close'].ewm(span=long_window, adjust=False).mean()

        macd = short_ema - long_ema
        signal = macd.ewm(span=signal_window, adjust=False).mean()

        macd_crossover = np.where(macd > signal, 1, 0)
        return macd_crossover.iloc[-1]

    def additional_past_returns(df, n):
        returns = df['close'].pct_change().dropna()
        return returns.rolling(window=n).mean().iloc[-1]

    def related_stock_data(symbol, df_past, conn, trade_date_str):
        query = f"SELECT * FROM market_data WHERE symbol='{symbol}' AND duration='1d' AND date(start_timestamp) < '{trade_date_str}' ORDER BY start_timestamp DESC LIMIT 60"
        df_related = pd.read_sql(query, conn)
        series_related = df_related['close']
        ret_related = (series_related.iloc[-1] - series_related.iloc[-2]) / series_related.iloc[-2] if len(series_related) >= 2 else 0
        return ret_related

    logger.info("Starting feature engineering for pre-position classifier")
    features_list = []
    for idx, row in df_trades.iterrows():
        logger.debug(f"Processing trade {idx + 1}/{len(df_trades)}")
        feats = get_pre_features(row, conn)
        if feats is None:
            features_list.append({k: np.nan for k in ['rsi', 'ret1', 'ret5', 'ret10', 'vol', 'ret1_index', 'time_frac', 'macd', 'more_past_returns', 'icici_data', 'hdfc_data', 'sensex_data']})
        else:
            features_list.append(feats)
    df_features = pd.DataFrame(features_list)
    valid_mask = df_features.notna().all(axis=1)
    df_trades = df_trades[valid_mask].reset_index(drop=True)
    df_features = df_features[valid_mask].reset_index(drop=True)
    logger.info(f"Filtered {len(df_trades)} valid trades after feature engineering")

    param_grid_rf = {
        'n_estimators': [50, 100, 200],
        'max_depth': [None, 5, 10]
    }

    grid_search_rf = GridSearchCV(RandomForestClassifier(random_state=42), param_grid_rf, cv=5, scoring='f1_macro')
    grid_search_rf.fit(df_features, df_trades['loss'])

    best_rf = grid_search_rf.best_estimator_
    logger.info(f"Best RF parameters: {grid_search_rf.best_params_}")

    train_size = int(len(df_trades) * 0.7)
    X_train = df_features.iloc[:train_size]
    y_train = df_trades.iloc[:train_size]['loss']
    X_test = df_features.iloc[train_size:]
    y_test = df_trades.iloc[train_size:]['loss']
    logger.info(f"Data split: {train_size} train, {len(df_trades) - train_size} test")

    param_grid_xgb = {
        'max_depth': [5, 10],
        'learning_rate': [0.05, 0.1],
        'n_estimators': [50, 100]
    }

    grid_search_xgb = GridSearchCV(XGBClassifier(objective='binary:logistic', random_state=42), param_grid_xgb, cv=5, scoring='f1_macro')
    grid_search_xgb.fit(X_train, y_train)

    best_xgb = grid_search_xgb.best_estimator_
    logger.info(f"Best XGB parameters: {grid_search_xgb.best_params_}")

    best_xgb.fit(X_train, y_train)
    logger.info("Classifier trained")

    def get_minute_data(trade_date, conn):
        trade_date_str = trade_date.strftime('%Y-%m-%d')
        query = f"SELECT * FROM market_data WHERE symbol='AXISBANK' AND duration='1m' AND date(start_timestamp) = '{trade_date_str}' ORDER BY start_timestamp"
        df_min = pd.read_sql(query, conn)
        df_min['start_timestamp'] = pd.to_datetime(df_min['start_timestamp'])
        logger.debug(f"Fetched {len(df_min)} minute data points for {trade_date_str}")
        return df_min

    logger.info("Preparing data for early exit regressor")
    early_data = []
    for idx, row in df_trades.iloc[:train_size].iterrows():
        df_min = get_minute_data(row['trade_date'], conn)
        if df_min.empty:
            logger.warning(f"No minute data for {row['trade_date']}")
            continue
        pos_ts = row['position_timestamp']
        exit_ts = row['exit_timestamp'] if row['exit_timestamp'] is not None else row['trade_date'].replace(hour=15, minute=15, second=0)
        df_after = df_min[df_min['start_timestamp'] >= pos_ts]
        for __, min_row in df_after.iterrows():
            current_ts = min_row['start_timestamp']
            if current_ts >= exit_ts:
                break
            current_price = min_row['close']
            current_pp = ((current_price - row['position_price']) / row['position_price']) * 100
            time_elapsed = (current_ts - pos_ts).total_seconds() / 60
            prev_mins = df_min[(df_min['start_timestamp'] < current_ts) & (df_min['start_timestamp'] >= current_ts - timedelta(minutes=10))]
            short_ret = ((min_row['close'] - prev_mins['close'].iloc[0]) / prev_mins['close'].iloc[0]) if len(prev_mins) > 1 else 0
            vol = min_row['volume']
            target = row['pp']
            early_data.append({'current_pp': current_pp, 'time_elapsed': time_elapsed, 'short_ret': short_ret, 'volume': vol, 'target': target})
    logger.info(f"Collected {len(early_data)} data points for early exit regressor")

    if early_data:
        logger.info("Training XGBRegressor")
        df_early = pd.DataFrame(early_data)
        X_early = df_early[['current_pp', 'time_elapsed', 'short_ret', 'volume']]
        y_early = df_early['target']
        reg = XGBRegressor(objective='reg:squarederror', n_estimators=100, max_depth=5, random_state=42)
        reg.fit(X_early, y_early)
        logger.info("Regressor trained")
    else:
        logger.warning("No early exit data available")
        reg = None

    logger.info("Starting backtesting")
    adapted_trades = []
    affected_count = 0
    profitable_trades = 0
    for idx, row in df_trades.iterrows():
        logger.debug(f"Backtesting trade {idx + 1}/{len(df_trades)}")
        feats = get_pre_features(row, conn)
        if feats is None:
            predicted_loss = 0
        else:
            df_feat = pd.DataFrame([feats])
            predicted_loss = best_xgb.predict(df_feat)[0]

        if predicted_loss == 1 and row['pp'] < 0:
            logger.info(f"Avoiding trade on {row['trade_date']} (predicted loss)")
            adapted = row.copy()
            adapted['pp'] = 0.00
            adapted['position'] = '0'
            adapted['exit_price'] = None
            adapted['exit_timestamp'] = None
            affected_count += 1
            adapted_trades.append(adapted)
            continue

        df_min = get_minute_data(row['trade_date'], conn)
        pos_ts = row['position_timestamp']
        original_exit_ts = row['exit_timestamp'] if row['exit_timestamp'] is not None else row['trade_date'].replace(hour=15, minute=15, second=0)
        exit_price = row['close_price']
        exit_ts = original_exit_ts
        is_affected = False
        if not df_min.empty and reg is not None:
            df_after = df_min[df_min['start_timestamp'] > pos_ts]
            for __, min_row in df_after.iterrows():
                current_ts = min_row['start_timestamp']
                if current_ts >= original_exit_ts:
                    break
                current_price = min_row['close']
                current_pp = ((current_price - row['position_price']) / row['position_price']) * 100
                time_elapsed = (current_ts - pos_ts).total_seconds() / 60
                prev_mins = df_min[(df_min['start_timestamp'] < current_ts) & (df_min['start_timestamp'] >= current_ts - timedelta(minutes=10))]
                short_ret = ((min_row['close'] - prev_mins['close'].iloc[0]) / prev_mins['close'].iloc[0]) if len(prev_mins) > 1 else 0
                vol = min_row['volume']
                feat = pd.DataFrame({'current_pp': [current_pp], 'time_elapsed': [time_elapsed], 'short_ret': [short_ret], 'volume': [vol]})
                predicted_pp = reg.predict(feat)[0]
                if predicted_pp < -0.5 and row['pp'] < 0:
                    logger.info(f"Early exit for trade on {row['trade_date']} at {current_ts}")
                    exit_price = current_price
                    exit_ts = current_ts
                    is_affected = True
                    break
        new_pp = ((exit_price - row['position_price']) / row['position_price']) * 100 if exit_price is not None else 0
        adapted = row.copy()
        adapted['pp'] = new_pp
        adapted['exit_price'] = exit_price
        adapted['exit_timestamp'] = exit_ts
        if is_affected:
            affected_count += 1
        if adapted['pp'] >= 0:
            profitable_trades += 1
        adapted_trades.append(adapted)

    df_adapted = pd.DataFrame(adapted_trades)
    avg_pp = df_adapted['pp'].mean()
    logger.info(f"Backtesting complete. Average pp: {avg_pp:.6f}, Affected trades: {affected_count}, Profitable trades: {profitable_trades}")
    print(f"Average pp: {avg_pp:.6f}")
    print(f"Number of affected trades: {affected_count}")
    print(f"Profitable trades: {profitable_trades}")

    logger.info("Inserting results into daily_trades_adapted")
    for idx, row in df_adapted.iterrows():
        sql = """INSERT INTO daily_trades_adapted
            (symbol, trade_date, version_number, open_price, position_price, position_timestamp, close_price, exit_price, exit_timestamp, position, pp)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"""
        val = (
            row['symbol'], row['trade_date'], 3, row['open_price'], row['position_price'], row['position_timestamp'],
            row['close_price'], row['exit_price'], row['exit_timestamp'], row.get('position', 'long'), row['pp']
        )
        try:
            cursor.execute(sql, val)
        except mysql.connector.Error as err:
            logger.error(f"Failed to insert trade {idx + 1}: {err}")
    conn.commit()
    logger.info("Results inserted into database")

except Exception as e:
    logger.error(f"Error during execution: {e}")
finally:
    if conn.is_connected():
        logger.info("Closing database connection")
        cursor.close()
        conn.close()