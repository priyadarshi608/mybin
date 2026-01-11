import mysql.connector
import pandas as pd
from datetime import datetime

# Configurable stoploss percentage (e.g., 0.5 for 0.5%)
STOPLOSS_PCT = 0.5  # Change this value to test different percentages

# Connect to the database
mydb = mysql.connector.connect(
    host="localhost",
    user="root",
    password="root",  # Your password
    database="market"
)
mycursor = mydb.cursor()

# Fetch daily_trades (all symbols)
mycursor.execute("SELECT * FROM daily_trades ORDER BY trade_date")
daily_trades = mycursor.fetchall()
trade_columns = ['symbol', 'trade_date', 'open_price', 'position_price', 'position_timestamp', 'close_price', 'exit_price', 'exit_timestamp', 'position', 'pp']
daily_trades_df = pd.DataFrame(daily_trades, columns=trade_columns)
# Convert all numeric columns to float
numeric_cols = ['open_price', 'position_price', 'close_price', 'exit_price', 'pp']
daily_trades_df[numeric_cols] = daily_trades_df[numeric_cols].astype(float)

# Drop and recreate daily_trades_adapted
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

# Counter for reversed trades
reversed_count = 0

# Convert STOPLOSS_PCT to decimal for calculations
stoploss_decimal = STOPLOSS_PCT / 100

# Process each trade
for _, row in daily_trades_df.iterrows():
    trade_date = row['trade_date']
    position_timestamp = row['position_timestamp']
    position_price = row['position_price']
    open_price = row['open_price']
    close_price = row['close_price']
    exit_timestamp = row['exit_timestamp']
    symbol = row['symbol']
    
    # Fetch minute data for the specific symbol and trade_date
    mycursor.execute("""
        SELECT * FROM market_data 
        WHERE symbol = %s AND duration = 'minute' AND DATE(start_timestamp) = %s 
        ORDER BY start_timestamp
    """, (symbol, trade_date))
    minute_data = mycursor.fetchall()
    minute_columns = ['id', 'symbol', 'category', 'duration', 'start_timestamp', 'end_timestamp', 'open', 'high', 'low', 'close', 'volume', 'is_correct']
    current_minute = pd.DataFrame(minute_data, columns=minute_columns)
    current_minute['date'] = pd.to_datetime(current_minute['start_timestamp']).dt.date
    price_cols = ['open', 'high', 'low', 'close']
    current_minute[price_cols] = current_minute[price_cols].astype(float)
    current_minute = current_minute.sort_values('start_timestamp').reset_index(drop=True)
    
    if current_minute.empty:
        # No data, insert original
        mycursor.execute("""
            INSERT INTO daily_trades_adapted 
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """, (symbol, trade_date, open_price, position_price, position_timestamp, close_price, row['exit_price'], exit_timestamp, row['position'], row['pp']))
        continue
    
    # Find position index (minute containing position_timestamp)
    position_row = current_minute[(current_minute['start_timestamp'] <= position_timestamp) & (current_minute['end_timestamp'] > position_timestamp)]
    if position_row.empty:
        mycursor.execute("""
            INSERT INTO daily_trades_adapted 
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """, (symbol, trade_date, open_price, position_price, position_timestamp, close_price, row['exit_price'], exit_timestamp, row['position'], row['pp']))
        continue
    position_idx = position_row.index[0]
    
    # Check for stoploss drop after position
    reversed_trade = False
    for i in range(position_idx + 1, len(current_minute)):
        current_time = current_minute.iloc[i]['start_timestamp']
        if current_time >= exit_timestamp:
            break
        current_price = current_minute.iloc[i]['close']
        drop_pct = (current_price - position_price) / position_price
        if drop_pct <= -stoploss_decimal:  # Configurable stoploss
            # Reverse at this minute
            long_exit_time = current_minute.iloc[i]['end_timestamp']
            long_exit_price = current_price
            long_pp = drop_pct * 100  # Already negative
            
            # Insert long trade
            mycursor.execute("""
                INSERT INTO daily_trades_adapted 
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            """, (symbol, trade_date, open_price, position_price, position_timestamp, close_price, long_exit_price, long_exit_time, '1', long_pp))
            
            # Short trade
            short_entry_price = long_exit_price
            short_entry_time = long_exit_time
            short_exit_price = current_minute.iloc[-1]['close']  # Assume last minute close is exit_price
            short_exit_time = exit_timestamp
            short_pp = ((short_entry_price - short_exit_price) / short_entry_price) * 100
            
            mycursor.execute("""
                INSERT INTO daily_trades_adapted 
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            """, (symbol, trade_date, open_price, short_entry_price, short_entry_time, close_price, short_exit_price, short_exit_time, '-1', short_pp))
            
            reversed_trade = True
            reversed_count += 1
            break
    
    if not reversed_trade:
        # Insert original
        mycursor.execute("""
            INSERT INTO daily_trades_adapted 
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """, (symbol, trade_date, open_price, position_price, position_timestamp, close_price, row['exit_price'], exit_timestamp, row['position'], row['pp']))

mydb.commit()

# Verify averages and reversal count
mycursor.execute("SELECT AVG(pp) FROM daily_trades")
orig_avg = mycursor.fetchone()[0]
mycursor.execute("SELECT AVG(pp) FROM daily_trades_adapted")
adapted_avg = mycursor.fetchone()[0]
mycursor.execute("SELECT COUNT(*) FROM daily_trades_adapted")
total_adapted = mycursor.fetchone()[0]
print(f"\nOriginal Avg PP: {orig_avg:.4f}%")
print(f"Adapted Avg PP: {adapted_avg:.4f}%")
print(f"Improvement: {adapted_avg - orig_avg:.4f}")
print(f"Total trades in adapted: {total_adapted}")
print(f"Reversed trades: {reversed_count}")
print(f"Stoploss PCT used: {STOPLOSS_PCT}%")

mydb.close()
print("Configurable stoploss logic completed!")