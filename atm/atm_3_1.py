import mysql.connector
import pandas as pd
import numpy as np
from datetime import datetime
from sklearn.ensemble import HistGradientBoostingClassifier

# Connect to the database (update credentials)
mydb = mysql.connector.connect(
    host="localhost",
    user="root",  # Your username
    password="root",  # Your password
    database="market"
)
mycursor = mydb.cursor()

# Fetch data
print("Fetching data...")
mycursor.execute("SELECT * FROM market_data WHERE symbol = 'AXISBANK' AND duration = 'day' ORDER BY start_timestamp")
daily_df = pd.DataFrame(mycursor.fetchall(), columns=['id', 'symbol', 'category', 'duration', 'start_timestamp', 'end_timestamp', 'open', 'high', 'low', 'close', 'volume', 'is_correct'])
daily_df['date'] = pd.to_datetime(daily_df['start_timestamp']).dt.date
# Convert to float
price_cols = ['open', 'high', 'low', 'close']
daily_df[price_cols] = daily_df[price_cols].astype(float)

mycursor.execute("SELECT * FROM market_data WHERE symbol = 'AXISBANK' AND duration = 'minute' ORDER BY start_timestamp")
minute_all_df = pd.DataFrame(mycursor.fetchall(), columns=['id', 'symbol', 'category', 'duration', 'start_timestamp', 'end_timestamp', 'open', 'high', 'low', 'close', 'volume', 'is_correct'])
minute_all_df['date'] = pd.to_datetime(minute_all_df['start_timestamp']).dt.date
minute_all_df[price_cols] = minute_all_df[price_cols].astype(float)

mycursor.execute("SELECT * FROM daily_trades ORDER BY trade_date")
daily_trades_df = pd.DataFrame(mycursor.fetchall(), columns=['symbol', 'trade_date', 'open_price', 'position_price', 'position_timestamp', 'close_price', 'exit_price', 'exit_timestamp', 'position', 'pp'])
# Convert to float
trade_price_cols = ['open_price', 'position_price', 'close_price', 'exit_price', 'pp']
daily_trades_df[trade_price_cols] = daily_trades_df[trade_price_cols].astype(float)
daily_trades_df = daily_trades_df.sort_values('trade_date').reset_index(drop=True)  # Ensure sorted for incremental

# Drop and recreate table
mycursor.execute("DROP TABLE IF EXISTS daily_trades_adapted")
mycursor.execute("""
    CREATE TABLE daily_trades_adapted (
        symbol VARCHAR(20) NOT NULL,
        trade_date DATE NOT NULL,
        open_price DECIMAL(10,2),
        position_price DECIMAL(10,2),
        position_timestamp DATETIME,
        close_price DECIMAL(10,2),
        exit_price DECIMAL(10,2),
        exit_timestamp DATETIME,
        position VARCHAR(10),
        pp DECIMAL(10,2)
    )
""")

def compute_features(minute_df, i, daily_df, trade_date, market_open='09:15:00'):
    """Safely compute features without NaNs."""
    current_time = minute_df.iloc[i]['start_timestamp']
    current_price = minute_df.iloc[i]['close']
    day_open = minute_df.iloc[0]['open']
    
    # Time of day (minutes since open)
    market_open_dt = datetime(current_time.year, current_time.month, current_time.day, 9, 15, 0)
    time_of_day = (current_time - market_open_dt).total_seconds() / 60
    
    # Intraday return
    intraday_return = (current_price - day_open) / day_open
    
    # Intraday volatility (std of returns, skipna)
    prices = minute_df.iloc[0:i+1]['close']
    if len(prices) >= 2:
        intraday_vol = prices.pct_change().dropna().std()
    else:
        intraday_vol = 0.0
    
    # Volume so far
    volume_so_far = minute_df.iloc[0:i+1]['volume'].sum()
    
    # Previous day return
    prev_date = daily_df[daily_df['date'] < trade_date]['date'].max()
    prev_return = 0.0
    if pd.notna(prev_date):
        prev_row = daily_df[daily_df['date'] == prev_date].iloc[0]
        prev_return = (prev_row['close'] - prev_row['open']) / prev_row['open']
    
    # Avg return last 5 days
    last5 = daily_df[daily_df['date'] < trade_date].tail(5)
    avg_return = 0.0
    if len(last5) >= 2:
        avg_return = last5['close'].pct_change().dropna().mean()
    
    return [time_of_day, intraday_return, intraday_vol or 0, volume_so_far, prev_return, avg_return or 0]

# Incremental training setup
X_all = []
y_all = []
processed_dates = set()
model = None

# Dynamic threshold
historical_win_rate = (daily_trades_df['pp'] > 0).sum() / len(daily_trades_df)
REVERSE_THRESHOLD = 0.5 - (1 - historical_win_rate) * 0.2
print(f"Historical win rate: {historical_win_rate:.2f}, Reverse threshold: {REVERSE_THRESHOLD:.2f}")

# Process each trade
for idx, row in daily_trades_df.iterrows():
    trade_date = row['trade_date']
    position_timestamp = row['position_timestamp']
    position_price = row['position_price']
    open_price = row['open_price']
    close_price = row['close_price']
    exit_timestamp = row['exit_timestamp']
    
    print(f"Processing {trade_date}...")
    
    # Incremental: Add new past days' samples
    past_dates = set(daily_df[daily_df['date'] < trade_date]['date'])
    new_dates = sorted(past_dates - processed_dates)
    
    for date in new_dates[-1000:]:  # Limit to last 1000 days for speed/recency
        day_minute = minute_all_df[minute_all_df['date'] == date].reset_index(drop=True)
        if len(day_minute) < 10:
            continue
        day_close = daily_df[daily_df['date'] == date]['close'].iloc[0]
        for minute_idx in range(1, len(day_minute) - 10, 5):  # Subsample every 5th minute
            target = 1 if day_close > day_minute.iloc[minute_idx]['close'] else 0
            features = compute_features(day_minute, minute_idx, daily_df, date)
            X_all.append(features)
            y_all.append(target)
    
    processed_dates.update(new_dates)
    
    # Train if we have data
    if X_all:
        X_np = np.array(X_all)
        y_np = np.array(y_all)
        X_np = np.nan_to_num(X_np)
        model = HistGradientBoostingClassifier(max_iter=100, class_weight='balanced', random_state=42)
        model.fit(X_np, y_np)
    
    # Get minute data for day
    current_minute = minute_all_df[minute_all_df['date'] == trade_date].sort_values('start_timestamp').reset_index(drop=True)
    if current_minute.empty:
        mycursor.execute("INSERT INTO daily_trades_adapted VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)",
                         (row['symbol'], trade_date, open_price, position_price, position_timestamp, close_price, row['exit_price'], exit_timestamp, row['position'], row['pp']))
        continue
    
    # Find position index (minute containing position_timestamp)
    position_row = current_minute[(current_minute['start_timestamp'] <= position_timestamp) & (current_minute['end_timestamp'] > position_timestamp)]
    if position_row.empty:
        mycursor.execute("INSERT INTO daily_trades_adapted VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)",
                         (row['symbol'], trade_date, open_price, position_price, position_timestamp, close_price, row['exit_price'], exit_timestamp, row['position'], row['pp']))
        continue
    position_idx = position_row.index[0]
    
    if model is None:
        mycursor.execute("INSERT INTO daily_trades_adapted VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)",
                         (row['symbol'], trade_date, open_price, position_price, position_timestamp, close_price, row['exit_price'], exit_timestamp, row['position'], row['pp']))
        continue
    
    # Simulate
    reversed_trade = False
    for i in range(position_idx + 1, len(current_minute)):
        if i - position_idx < 5:  # Wait 5 min
            continue
        if current_minute.iloc[i]['start_timestamp'] >= exit_timestamp:
            break
        
        features = compute_features(current_minute, i, daily_df, trade_date)
        prob_up = model.predict_proba([features])[0][1]
        
        if prob_up < REVERSE_THRESHOLD:
            long_exit_time = current_minute.iloc[i]['end_timestamp']
            long_exit_price = current_minute.iloc[i]['close']
            long_pp = ((long_exit_price - position_price) / position_price) * 100
            
            mycursor.execute("INSERT INTO daily_trades_adapted VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)",
                             (row['symbol'], trade_date, open_price, position_price, position_timestamp,
                              close_price, long_exit_price, long_exit_time, '1', long_pp))
            
            short_entry_price = long_exit_price
            short_entry_time = long_exit_time
            short_exit_price = current_minute.iloc[-1]['close']
            short_exit_time = exit_timestamp
            short_pp = ((short_entry_price - short_exit_price) / short_entry_price) * 100
            
            mycursor.execute("INSERT INTO daily_trades_adapted VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)",
                             (row['symbol'], trade_date, open_price, short_entry_price, short_entry_time,
                              close_price, short_exit_price, short_exit_time, '-1', short_pp))
            
            reversed_trade = True
            break
    
    if not reversed_trade:
        mycursor.execute("INSERT INTO daily_trades_adapted VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)",
                         (row['symbol'], trade_date, open_price, position_price, position_timestamp, close_price, row['exit_price'], exit_timestamp, row['position'], row['pp']))
    
    mydb.commit()

# Verify
mycursor.execute("SELECT AVG(pp) FROM daily_trades")
orig_avg = mycursor.fetchone()[0]
mycursor.execute("SELECT AVG(pp) FROM daily_trades_adapted")
adapted_avg = mycursor.fetchone()[0]
print(f"\nOriginal Avg PP: {orig_avg:.2f}%")
print(f"Adapted Avg PP: {adapted_avg:.2f}%")
print("Improvement:", adapted_avg - orig_avg)

mydb.close()
print("Done!")
