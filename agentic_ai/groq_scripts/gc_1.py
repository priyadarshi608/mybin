import subprocess
import re
from groq import Groq
import os
from dotenv import load_dotenv
import sys
from datetime import datetime
import logging

# Load environment variables
load_dotenv()
api_key = os.getenv("GROQ_API_KEY")
if not api_key:
    raise ValueError("GROQ_API_KEY not found in .env file")

# Configure top-level logging for the agentic loop
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(sys.stdout),
        logging.FileHandler('agentic_loop.log')
    ]
)
logger = logging.getLogger(__name__)

# Initialize Groq client
logger.info("Initializing Groq client")
client = Groq(api_key=api_key)

def generate_text(prompt, max_new_tokens=4000, temperature=0.7):
    logger.debug("Generating text with Groq LLM")
    try:
        response = client.chat.completions.create(
            model="meta-llama/llama-4-scout-17b-16e-instruct",
            messages=[{"role": "user", "content": prompt}],
            max_tokens=max_new_tokens,
            temperature=temperature
        )
        generated = response.choices[0].message.content
        logger.debug("Text generation completed")
        return generated
    except Exception as e:
        logger.error(f"Text generation error: {e}")
        return None

def extract_code(generated_text):
    logger.debug("Extracting Python code from generated text")
    match = re.search(r"```python\n(.*?)\n```", generated_text, re.DOTALL)
    if match:
        logger.debug("Code extracted successfully")
        return match.group(1)
    logger.warning("No Python code found in generated text")
    return None

# Initial version of the trading script
initial_code = """
import mysql.connector
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from xgboost import XGBRegressor
from datetime import datetime, timedelta
import logging
import sys

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(sys.stdout),
        logging.FileHandler(f'trading_script_v{VERSION_NUMBER}.log')
    ]
)
logger = logging.getLogger(__name__)

# Version number (will be replaced dynamically)

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

        query = f"SELECT * FROM market_data WHERE symbol='AXISBANK' AND duration='1d' AND date(start_timestamp) < '{trade_date_str}' ORDER BY start_timestamp DESC LIMIT 30"
        df_past = pd.read_sql(query, conn)
        if len(df_past) < 30:
            logger.warning(f"Insufficient past data for AXISBANK on {trade_date_str}: {len(df_past)} days")
            return None

        series = df_past['close']
        delta = series.diff()
        gain = delta.clip(lower=0).rolling(window=14).mean()
        loss = -delta.clip(upper=0).rolling(window=14).mean()
        rs = gain / loss
        rsi = 100 - 100 / (1 + rs)
        rsi_val = rsi.iloc[-1] if not np.isnan(rsi.iloc[-1]) else 50

        ret1 = (series.iloc[-1] - series.iloc[-2]) / series.iloc[-2] if len(series) >= 2 else 0
        ret5 = (series.iloc[-1] - series.iloc[-5]) / series.iloc[-5] if len(series) >= 5 else 0
        pct_changes = series.pct_change().dropna()
        vol = pct_changes.std() if not pct_changes.empty else 0

        query_index = f"SELECT * FROM market_data WHERE symbol='NIFTY50' AND duration='1d' AND date(start_timestamp) < '{trade_date_str}' ORDER BY start_timestamp DESC LIMIT 30"
        df_index = pd.read_sql(query_index, conn)
        series_index = df_index['close']
        ret1_index = (series_index.iloc[-1] - series_index.iloc[-2]) / series_index.iloc[-2] if len(series_index) >= 2 else 0

        time_frac = (position_ts.hour * 60 + position_ts.minute) / (15 * 60 + 30)
        features = {
            'rsi': rsi_val,
            'ret1': ret1,
            'ret5': ret5,
            'vol': vol,
            'ret1_index': ret1_index,
            'time_frac': time_frac
        }
        logger.debug(f"Features computed: {features}")
        return features

    logger.info("Starting feature engineering for pre-position classifier")
    features_list = []
    for idx, row in df_trades.iterrows():
        logger.debug(f"Processing trade {idx + 1}/{len(df_trades)}")
        feats = get_pre_features(row, conn)
        if feats is None:
            features_list.append({k: np.nan for k in ['rsi', 'ret1', 'ret5', 'vol', 'ret1_index', 'time_frac']})
        else:
            features_list.append(feats)
    df_features = pd.DataFrame(features_list)
    valid_mask = df_features.notna().all(axis=1)
    df_trades = df_trades[valid_mask].reset_index(drop=True)
    df_features = df_features[valid_mask].reset_index(drop=True)
    logger.info(f"Filtered {len(df_trades)} valid trades after feature engineering")

    train_size = int(len(df_trades) * 0.8)
    X_train = df_features.iloc[:train_size]
    y_train = df_trades.iloc[:train_size]['loss']
    X_test = df_features.iloc[train_size:]
    y_test = df_trades.iloc[train_size:]['loss']
    logger.info(f"Data split: {train_size} train, {len(df_trades) - train_size} test")

    logger.info("Training RandomForestClassifier")
    clf = RandomForestClassifier(n_estimators=50, max_depth=3, random_state=42)
    clf.fit(X_train, y_train)
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
            prev_mins = df_min[(df_min['start_timestamp'] < current_ts) & (df_min['start_timestamp'] >= current_ts - timedelta(minutes=5))]
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
        reg = XGBRegressor(objective='reg:squarederror', n_estimators=50, max_depth=3, random_state=42)
        reg.fit(X_early, y_early)
        logger.info("Regressor trained")
    else:
        logger.warning("No early exit data available")
        reg = None

    logger.info("Starting backtesting")
    adapted_trades = []
    affected_count = 0
    for idx, row in df_trades.iterrows():
        logger.debug(f"Backtesting trade {idx + 1}/{len(df_trades)}")
        feats = get_pre_features(row, conn)
        if feats is None:
            predicted_loss = 0
        else:
            df_feat = pd.DataFrame([feats])
            predicted_loss = clf.predict(df_feat)[0]

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
                prev_mins = df_min[(df_min['start_timestamp'] < current_ts) & (df_min['start_timestamp'] >= current_ts - timedelta(minutes=5))]
                short_ret = ((min_row['close'] - prev_mins['close'].iloc[0]) / prev_mins['close'].iloc[0]) if len(prev_mins) > 1 else 0
                vol = min_row['volume']
                feat = pd.DataFrame({'current_pp': [current_pp], 'time_elapsed': [time_elapsed], 'short_ret': [short_ret], 'volume': [vol]})
                predicted_pp = reg.predict(feat)[0]
                if predicted_pp < -0.2 and row['pp'] < 0:
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
        adapted_trades.append(adapted)

    df_adapted = pd.DataFrame(adapted_trades)
    avg_pp = df_adapted['pp'].mean()
    logger.info(f"Backtesting complete. Average pp: {avg_pp:.6f}, Affected trades: {affected_count}")
    print(f"Average pp: {avg_pp:.6f}")
    print(f"Number of affected trades: {affected_count}")

    logger.info("Inserting results into daily_trades_adapted")
    for idx, row in df_adapted.iterrows():
        sql = THREE_DOUBLE_QUOTE
            INSERT INTO daily_trades_adapted
            (symbol, trade_date, version_number, open_price, position_price, position_timestamp, close_price, exit_price, exit_timestamp, position, pp)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        THREE_DOUBLE_QUOTE
        val = (
            row['symbol'], row['trade_date'], VERSION_NUMBER, row['open_price'], row['position_price'], row['position_timestamp'],
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
"""

# Agentic loop to iterate and improve
version = 1
max_iterations = 20
current_code = initial_code
while True:
    logger.info(f"Starting iteration {version}")
    file_name = f"trading_script_v{version}.py"
    logger.info(f"Saving script as {file_name}")
    # Dynamically replace VERSION_NUMBER in the code
    code_with_version = current_code.replace("VERSION_NUMBER", str(version))
    code_with_double_quote_replaced = code_with_version.replace("THREE_DOUBLE_QUOTE", str('"""'))
    with open(file_name, "w") as f:
        f.write(code_with_double_quote_replaced)

    logger.info(f"Executing script {file_name}")
    process = subprocess.Popen(["python3", file_name], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    stdout, stderr = process.communicate()
    output = stdout.decode("utf-8").strip()
    error = stderr.decode("utf-8").strip()

    if error:
        result = f"Execution error:\n{error}"
        avg_pp = 0.0
        logger.error(f"Script execution failed: {error}")
    else:
        result = output
        match = re.search(r"Average pp: ([\d\.-]+)", output)
        avg_pp = float(match.group(1)) if match else 0.0
        logger.info(f"Script executed. Average pp: {avg_pp}")

    print(f"Version {version} result: avg_pp = {avg_pp}, output:\n{result}")
    logger.info(f"Version {version} result: avg_pp = {avg_pp}")

    if avg_pp >= 0.8:
        logger.info(f"Goal achieved with version {version} (avg_pp = {avg_pp})")
        print(f"Goal achieved with version {version}!")
        break

    if version >= max_iterations:
        logger.warning("Reached maximum iterations without achieving goal")
        print("Reached maximum iterations without achieving the goal.")
        break

    logger.info("Generating improved script with LLM")
    prompt = f"""
        You are an expert in Python and machine learning for trading strategies.
        The current script is:
        ```python
        {current_code}
        ```
        It produced this output:
        {result}
        The average pp is {avg_pp}, which is less than 0.8.
        Improve the script to achieve an average pp of at least 0.8% per trade.
        Preserve as many profitable trades as possible (do not modify trades with original pp >= 0).
        Minimize the number of affected trades.
        Possible improvements:
        Add more features (e.g., MACD, more past returns, data from related stocks like ICICIBANK, HDFCBANK, SENSEX).
        Optimize hyperparameters (use grid search).
        Change model types (e.g., XGBClassifier for classifier).
        Adjust the early exit threshold or logic.
        Improve feature engineering for both models.
        Ensure chronological split and handle small data.
        Add error handling.
        Increment the version_number to {version + 1}.
        Provide the complete, standalone updated Python script inside python code block.
        Do not add extra text outside the code block.
    """

    generated = generate_text(prompt)
    if generated is None:
        logger.error("Failed to generate improved script")
        print("Failed to generate improved script. Stopping.")
        break

    new_code = extract_code(generated)
    if new_code is None:
        logger.error("Failed to extract improved code")
        print("Failed to extract improved code. Stopping.")
        break

    current_code = new_code
    version += 1
    logger.info(f"Generated new script version {version}")
