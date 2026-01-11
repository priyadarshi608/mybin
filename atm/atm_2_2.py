import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from arch import arch_model
import pandas_ta
import pymysql
from sqlalchemy import create_engine
import matplotlib.ticker as mtick

# -------------------------------
# 1. Database connection
# -------------------------------
db_user = 'root'
db_password = 'root'
db_host = 'localhost'
db_name = 'market'

# Create SQLAlchemy engine
engine = create_engine(f'mysql+pymysql://{db_user}:{db_password}@{db_host}/{db_name}')

# -------------------------------
# 2. List of symbols
# -------------------------------
# symbols = ['AXISBANK']
symbols = [
    'ABB', 'ABFRL', 'ACC', 'ADANIENT', 'ADANIPORTS', 'ADANIPOWER', 'AMBUJACEM', 'APOLLOHOSP', 'APOLLOTYRE', 'ASHOKLEY', 'ASIANPAINT', 'ASTRAL', 'AUROPHARMA', 'AXISBANK', 'BAJAJ-AUTO', 'BAJAJFINSV', 'BAJAJHLDNG', 'BAJFINANCE', 'BANKBARODA', 'BANKINDIA', 'BEL', 'BHARATFORG', 'BHARTIARTL', 'BHEL', 'BIOCON', 'BOSCHLTD', 'BPCL', 'BRITANNIA', 'CANBK', 'CGPOWER', 'CHOLAFIN', 'CIPLA', 'COFORGE', 'COLPAL', 'CONCOR', 'CUMMINSIND', 'DABUR', 'DIVISLAB', 'DLF', 'DRREDDY', 'EICHERMOT', 'ESCORTS', 'EXIDEIND', 'FEDERALBNK', 'GAIL', 'GLENMARK', 'GMRAIRPORT', 'GODREJCP', 'GODREJPROP', 'GRASIM', 'HAVELLS', 'HCLTECH', 'HDFCBANK', 'HEROMOTOCO', 'HINDALCO', 'HINDPETRO', 'HINDUNILVR', 'HINDZINC', 'ICICIBANK', 'IDEA', 'IGL', 'INDHOTEL', 'INDIANB', 'INDUSINDBK', 'INDUSTOWER', 'INFY', 'IOC', 'IRB', 'ITC', 'JINDALSTEL', 'JSWENERGY', 'JSWSTEEL', 'JUBLFOOD', 'KOTAKBANK', 'L&TFH', 'LICHSGFIN', 'LT', 'LTF', 'LUPIN', 'M&M', 'M&MFIN', 'MAHABANK', 'MARICO', 'MARUTI', 'MCDOWELL-N', 'MFSL', 'MOTHERSON', 'MOTILALOFS', 'MPHASIS', 'MRF', 'MUTHOOTFIN', 'NATIONALUM', 'NAUKRI', 'NESTLEIND', 'NHPC', 'NMDC', 'NTPC', 'OBEROIRLTY', 'OFSS', 'OIL', 'PAGEIND', 'PERSISTENT', 'PFC', 'PIDILITIND', 'PIIND', 'PNB', 'POWERGRID', 'PRESTIGE', 'RECLTD', 'RELIANCE', 'SAIL', 'SBIN', 'SHREECEM', 'SHRIRAMFIN', 'SIEMENS', 'SJVN', 'SRF', 'SRTRANSFIN', 'SUNPHARMA', 'SUPREMEIND', 'SUZLON', 'TATACOMM', 'TATACONSUM', 'TATAELXSI', 'TATAMOTORS', 'TATAPOWER', 'TATASTEEL', 'TCS', 'TITAN', 'TORNTPHARM', 'TORNTPOWER', 'TRENT', 'TVSMOTOR', 'ULTRACEMCO', 'UNIONBANK', 'UNITDSPR', 'UPL', 'VEDL', 'VOLTAS', 'WIPRO', 'YESBANK', 'ZYDUSLIFE'
]

# To collect all results
all_trades = []

# -------------------------------
# 3. Process each symbol
# -------------------------------
for symbol_name in symbols:
    print(f"\nProcessing {symbol_name}...")

    # -------------------------------
    # Load daily (day) data
    # -------------------------------
    daily_query = f"""
    SELECT 
        start_timestamp AS Date,
        open,
        high,
        low,
        close,
        volume
    FROM market_data
    WHERE symbol = '{symbol_name}' AND duration = 'day'
    ORDER BY start_timestamp
    """

    daily_df = pd.read_sql(daily_query, engine)
    daily_df['Date'] = pd.to_datetime(daily_df['Date'])
    daily_df = daily_df.set_index('Date')
    daily_df['Adj Close'] = daily_df['close']

    # Rename to camel case
    daily_df.rename(columns={
        'open': 'Open', 'high': 'High', 'low': 'Low', 'close': 'Close', 'Adj Close': 'Adj Close', 'volume': 'Volume'
    }, inplace=True)

    daily_df = daily_df[['Open', 'High', 'Low', 'Close', 'Adj Close', 'Volume']]
    daily_df['log_ret'] = np.log(daily_df['Adj Close']).diff()
    daily_df['variance'] = daily_df['log_ret'].rolling(180).var()
    daily_df = daily_df['2015':]

    # -------------------------------
    # Predict volatility (GARCH)
    # -------------------------------
    def predict_volatility(x):
        best_model = arch_model(y=x * 100, p=1, q=3).fit(update_freq=5, disp='off')
        variance_forecast = best_model.forecast(horizon=1).variance.iloc[-1, 0]
        print(f"Volatility predicted till: {x.index[-1].date()}")
        return variance_forecast

    daily_df['predictions'] = daily_df['log_ret'].rolling(180).apply(lambda x: predict_volatility(x))
    daily_df = daily_df.dropna()

    # -------------------------------
    # Signal generation (daily)
    # -------------------------------
    daily_df['prediction_premium'] = (daily_df['predictions'] - daily_df['variance']) / daily_df['variance']
    daily_df['premium_std'] = daily_df['prediction_premium'].rolling(180).std()

    dst = 1
    daily_df['signal_daily'] = daily_df.apply(
        lambda x: 1 if x['prediction_premium'] > dst * x['premium_std']
        else (-1 if x['prediction_premium'] < -1 * dst * x['premium_std'] else np.nan),
        axis=1
    )
    daily_df['signal_daily'] = daily_df['signal_daily'].shift()

    # -------------------------------
    # Load intraday (minute) data
    # -------------------------------
    minute_query = f"""
    SELECT 
        start_timestamp AS datetime,
        open,
        high,
        low,
        close,
        volume
    FROM market_data
    WHERE symbol = '{symbol_name}' AND duration = '5minute'
    ORDER BY start_timestamp
    """

    intraday_min_df = pd.read_sql(minute_query, engine)
    intraday_min_df['datetime'] = pd.to_datetime(intraday_min_df['datetime'])
    intraday_min_df = intraday_min_df.set_index('datetime')

    # # ✅ Convert 1-minute candles into 5-minute candles
    # intraday_5min_df = intraday_min_df.resample('5T').agg({
    #     'open': 'first',
    #     'high': 'max',
    #     'low': 'min',
    #     'close': 'last',
    #     'volume': 'sum'
    # }).dropna()
    # # Add a date column for merging with daily signals
    # intraday_5min_df['date'] = intraday_5min_df.index.normalize()
    # intraday_min_df = intraday_5min_df[['open', 'low', 'high', 'close', 'volume', 'date']]

    # Remove upper lines and uncomment next 2 lines
    intraday_min_df['date'] = intraday_min_df.index.normalize()
    intraday_min_df = intraday_min_df[['open', 'low', 'high', 'close', 'volume', 'date']]

    # -------------------------------
    # Merge and create signals
    # -------------------------------
    final_df = intraday_min_df.reset_index() \
        .merge(daily_df[['signal_daily']].reset_index(), left_on='date', right_on='Date') \
        .drop(['date', 'Date'], axis=1) \
        .set_index('datetime')

    final_df['rsi'] = pandas_ta.rsi(close=final_df['close'], length=41)
    bbands = pandas_ta.bbands(close=final_df['close'], length=41, std=2)
    final_df['lband'] = bbands.iloc[:, 0]
    final_df['uband'] = bbands.iloc[:, 2]

    # final_df['signal_intraday'] = final_df.apply(
    #     lambda x: 1 if (x['rsi'] > 80) & (x['close'] > x['uband']) else np.nan,
    #     axis=1
    # )
    # final_df['signal_intraday'] = final_df.apply(
    #     lambda x: -1 if (x['rsi'] < 70) & (x['close'] < x['uband']) else np.nan,
    #     axis=1
    # )
    final_df['signal_intraday'] = final_df.apply(
        lambda x: 1 if (x['rsi'] > 70) & (x['close'] > x['uband'])
        else (-1 if (x['rsi'] < 30) & (x['close'] < x['lband']) else np.nan),
        axis=1
    )

    final_df['return'] = np.log(final_df['close']).diff()

    final_df['return_sign'] = final_df.apply(
        lambda x: -1 if (x['signal_daily'] == 1) & (x['signal_intraday'] == 1)
        else (1 if (x['signal_daily'] == -1) & (x['signal_intraday'] == -1) else np.nan),
        axis=1
    )

    final_df['return_sign'] = final_df.groupby(pd.Grouper(freq='D'))['return_sign'].transform(lambda x: x.ffill())
    final_df['forward_return'] = final_df['return'].shift(-1)
    final_df['strategy_return'] = final_df['forward_return'] * final_df['return_sign']

    # -------------------------------
    # Trade log creation
    # -------------------------------
    temp_df = final_df.reset_index()
    for date, group in temp_df.groupby(temp_df['datetime'].dt.date):
        if group['return_sign'].isna().all():
            continue

        # Preserve original full-day group before applying the 09:16 filter
        original_group = group.copy()

        # Entry after 9:16:00
        group = group[group['datetime'].dt.time >= pd.to_datetime("09:16:00").time()]
        if group.empty:
            continue

        # Exit fixed at 15:15:00 or last available candle
        exit_row = group[group['datetime'].dt.time == pd.to_datetime("15:15:00").time()]
        if exit_row.empty:
            exit_row = group.iloc[[-1]]

        first_signal_idx = group['return_sign'].first_valid_index()
        if first_signal_idx is None:
            continue

        position = group.loc[first_signal_idx, 'return_sign'] * -1
        position_price = group.loc[first_signal_idx, 'open']
        position_timestamp = group.loc[first_signal_idx, 'datetime']

        exit_price = exit_row['close'].iloc[0]
        exit_timestamp = exit_row['datetime'].iloc[0]

        # Use original_group to get the day's true open price (first candle of the day)
        open_price = original_group['open'].iloc[0]

        # keep close_price as before (last available close in the filtered group)
        close_price = group['close'].iloc[-1]

        # Calculate pp (percentage profit) and round to 2 decimals
        pp = round(((exit_price - position_price) * position / position_price) * 100, 2)

        all_trades.append({
            'symbol': symbol_name,
            'trade_date': date,
            'open_price': open_price,
            'close_price': close_price,
            'position_price': position_price,
            'position_timestamp': position_timestamp,
            'exit_price': exit_price,
            'exit_timestamp': exit_timestamp,
            'position': position,
            'pp': pp  # renamed and rounded field
        })

# -------------------------------
# 4. Write all trades to MySQL
# -------------------------------
daily_trades_df = pd.DataFrame(all_trades)

daily_trades_df.to_sql(
    name='daily_trades',
    con=engine,
    if_exists='append',  # use 'append' to accumulate results over time
    index=False
)

print("\n✅ All symbols processed and daily trades written to MySQL successfully!")
