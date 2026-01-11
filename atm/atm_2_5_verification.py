import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from arch import arch_model
import pandas_ta
import pymysql
from sqlalchemy import create_engine
from itertools import product
import warnings
from arch.__future__ import reindexing
warnings.filterwarnings("ignore", message=".*Inequality constraints incompatible.*")
warnings.filterwarnings("ignore", message=".*ConvergenceWarning.*")

# -------------------------------
# 1. Database connection
# -------------------------------
db_user = 'root'
db_password = 'root'
db_host = 'localhost'
db_name = 'market'

engine = create_engine(f'mysql+pymysql://{db_user}:{db_password}@{db_host}/{db_name}')

# -------------------------------
# 2. List of symbols
# -------------------------------
symbols = ['AXISBANK']
# symbols = [
#     'ABB', 'ABFRL', 'ACC', 'ADANIENT', 'ADANIPORTS', 'ADANIPOWER', 'AMBUJACEM', 'APOLLOHOSP', 'APOLLOTYRE', 'ASHOKLEY', 'ASIANPAINT', 'ASTRAL', 'AUROPHARMA', 'AXISBANK', 'BAJAJ-AUTO', 'BAJAJFINSV', 'BAJAJHLDNG', 'BAJFINANCE', 'BANKBARODA', 'BANKINDIA', 'BEL', 'BHARATFORG', 'BHARTIARTL', 'BHEL', 'BIOCON', 'BOSCHLTD', 'BPCL', 'BRITANNIA', 'CANBK', 'CGPOWER', 'CHOLAFIN', 'CIPLA', 'COFORGE', 'COLPAL', 'CONCOR', 'CUMMINSIND', 'DABUR', 'DIVISLAB', 'DLF', 'DRREDDY', 'EICHERMOT', 'ESCORTS', 'EXIDEIND', 'FEDERALBNK', 'GAIL', 'GLENMARK', 'GMRAIRPORT', 'GODREJCP', 'GODREJPROP', 'GRASIM', 'HAVELLS', 'HCLTECH', 'HDFCBANK', 'HEROMOTOCO', 'HINDALCO', 'HINDPETRO', 'HINDUNILVR', 'HINDZINC', 'ICICIBANK', 'IDEA', 'IGL', 'INDHOTEL', 'INDIANB', 'INDUSINDBK', 'INDUSTOWER', 'INFY', 'IOC', 'IRB', 'ITC', 'JINDALSTEL', 'JSWENERGY', 'JSWSTEEL', 'JUBLFOOD', 'KOTAKBANK', 'L&TFH', 'LICHSGFIN', 'LT', 'LTF', 'LUPIN', 'M&M', 'M&MFIN', 'MAHABANK', 'MARICO', 'MARUTI', 'MCDOWELL-N', 'MFSL', 'MOTHERSON', 'MOTILALOFS', 'MPHASIS', 'MRF', 'MUTHOOTFIN', 'NATIONALUM', 'NAUKRI', 'NESTLEIND', 'NHPC', 'NMDC', 'NTPC', 'OBEROIRLTY', 'OFSS', 'OIL', 'PAGEIND', 'PERSISTENT', 'PFC', 'PIDILITIND', 'PIIND', 'PNB', 'POWERGRID', 'PRESTIGE', 'RECLTD', 'RELIANCE', 'SAIL', 'SBIN', 'SHREECEM', 'SHRIRAMFIN', 'SIEMENS', 'SJVN', 'SRF', 'SRTRANSFIN', 'SUNPHARMA', 'SUPREMEIND', 'SUZLON', 'TATACOMM', 'TATACONSUM', 'TATAELXSI', 'TATAMOTORS', 'TATAPOWER', 'TATASTEEL', 'TCS', 'TITAN', 'TORNTPHARM', 'TORNTPOWER', 'TRENT', 'TVSMOTOR', 'ULTRACEMCO', 'UNIONBANK', 'UNITDSPR', 'UPL', 'VEDL', 'VOLTAS', 'WIPRO', 'YESBANK', 'ZYDUSLIFE'
# ]

# -------------------------------
# 3. Hyperparameter grids
# -------------------------------
variance_rolling_window_arr = [60]
garch_input_window_arr = [60]
premium_std_window_arr = [240]
garch_p_arr = [4]
garch_q_arr = [1]
garch_model_multiplier_arr = [0.01]
dst_arr = [1.0]
rsi_length_arr = [40]
bbands_length_arr = [10]
bbands_std_arr = [1.5]
rsi_upper_arr = [65]
rsi_lower_arr = [30, 25]
# variance_rolling_window_arr = [60, 120, 180, 240]
# garch_input_window_arr = [60, 120, 180, 240]
# premium_std_window_arr = [60, 120, 180, 240]
# garch_p_arr = [1, 2, 3, 4]
# garch_q_arr = [1, 2, 3, 4]
# garch_model_multiplier_arr = [0.001, 0.01, 0.1, 1, 10, 100, 500]
# dst_arr = [0.5, 0.75, 1.0, 1.25, 1.5]
# rsi_length_arr = [10, 20, 30, 40, 50]
# bbands_length_arr = [10, 20, 30, 40, 50]
# bbands_std_arr = [1.0, 1.25]
# rsi_upper_arr = [65, 70, 75]
# rsi_lower_arr = [25, 30, 35]

# -------------------------------
# 4. Iterate over all combinations
# -------------------------------
for (variance_rolling_window, garch_input_window, premium_std_window,
     garch_p, garch_q, garch_model_multiplier, dst,
     rsi_length, bbands_length, bbands_std, rsi_upper, rsi_lower) in product(
        variance_rolling_window_arr,
        garch_input_window_arr,
        premium_std_window_arr,
        garch_p_arr,
        garch_q_arr,
        garch_model_multiplier_arr,
        dst_arr,
        rsi_length_arr,
        bbands_length_arr,
        bbands_std_arr,
        rsi_upper_arr,
        rsi_lower_arr
    ):

    print(f"\n=== Running combination ===")
    print(f"variance_rolling_window={variance_rolling_window}, garch_input_window={garch_input_window}, "
          f"premium_std_window={premium_std_window}, garch_p={garch_p}, garch_q={garch_q}, "
          f"garch_model_multiplier={garch_model_multiplier}, dst={dst}, rsi_length={rsi_length}, "
          f"bbands_length={bbands_length}, bbands_std={bbands_std}, "
          f"rsi_upper={rsi_upper}, rsi_lower={rsi_lower}")

    all_trades = []

    for symbol_name in symbols:
        print(f"Processing {symbol_name}...")

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
        AND start_timestamp <= '2016-06-24 00:00:00'
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
        daily_df['variance'] = daily_df['log_ret'].rolling(variance_rolling_window).var()
        daily_df = daily_df['2015':]

        # -------------------------------
        # Predict volatility (GARCH)
        # -------------------------------
        def predict_volatility(x):
            best_model = arch_model(y=x * garch_model_multiplier, p=garch_p, q=garch_q).fit(update_freq=5, disp='off')
            variance_forecast = best_model.forecast(horizon=1).variance.iloc[-1, 0]
            # print(f"Volatility predicted till: {x.index[-1].date()}")
            return variance_forecast

        daily_df['predictions'] = daily_df['log_ret'].rolling(garch_input_window).apply(lambda x: predict_volatility(x))
        daily_df = daily_df.dropna()

        # -------------------------------
        # Signal generation (daily)
        # -------------------------------
        daily_df['premium_std'] = daily_df['predictions'].rolling(premium_std_window).std()
        daily_df['prediction_premium'] = (daily_df['predictions'] - daily_df['variance']) / daily_df['variance']
        # daily_df['prediction_premium'] = (daily_df['predictions'] - daily_df['variance']) / daily_df['variance']
        # daily_df['premium_std'] = daily_df['prediction_premium'].rolling(premium_std_window).std()

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
        AND start_timestamp <= '2016-06-24 15:25:00'
        ORDER BY start_timestamp
        """

        intraday_min_df = pd.read_sql(minute_query, engine)
        intraday_min_df['datetime'] = pd.to_datetime(intraday_min_df['datetime'])
        intraday_min_df = intraday_min_df.set_index('datetime')
        intraday_min_df['date'] = intraday_min_df.index.normalize()
        intraday_min_df = intraday_min_df[['open', 'low', 'high', 'close', 'volume', 'date']]
        final_df = intraday_min_df.reset_index() \
            .merge(daily_df[['signal_daily']].reset_index(), left_on='date', right_on='Date') \
            .drop(['date', 'Date'], axis=1) \
            .set_index('datetime')

        final_df['rsi'] = pandas_ta.rsi(close=final_df['close'], length=rsi_length)
        bbands = pandas_ta.bbands(close=final_df['close'], length=bbands_length, std=bbands_std)
        final_df['lband'] = bbands.iloc[:, 0]
        final_df['uband'] = bbands.iloc[:, 2]

        final_df['signal_intraday'] = final_df.apply(
            lambda x: 1 if (x['rsi'] > rsi_upper) & (x['close'] > x['uband'])
            else (-1 if (x['rsi'] < rsi_lower) & (x['close'] < x['lband']) else np.nan),
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

        temp_df = final_df.reset_index()
        for date, group in temp_df.groupby(temp_df['datetime'].dt.date):
            if group['return_sign'].isna().all():
                continue

            original_group = group.copy()
            exit_row = group[group['datetime'].dt.time == pd.to_datetime("15:15:00").time()]
            if exit_row.empty:
                exit_row = group.iloc[[-1]]

            first_signal_idx = group['return_sign'].first_valid_index()
            if first_signal_idx is None:
                continue
            print(f"📍 First signal timestamp for {symbol_name} on {date}: {group.loc[first_signal_idx, 'datetime']}")

            entry_idx = group.index.get_loc(first_signal_idx) + 1
            if entry_idx >= len(group):
                continue

            position = group.iloc[entry_idx]['return_sign'] * -1
            position_price = group.iloc[entry_idx]['open']
            position_timestamp = group.iloc[entry_idx]['datetime']
            exit_price = exit_row['open'].iloc[0]
            exit_timestamp = exit_row['datetime'].iloc[0]
            open_price = original_group['open'].iloc[0]
            close_price = group['close'].iloc[-1]
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
                'pp': pp,
                'variance_rolling_window': variance_rolling_window,
                'garch_input_window': garch_input_window,
                'premium_std_window': premium_std_window,
                'garch_p': garch_p,
                'garch_q': garch_q,
                'garch_model_multiplier': garch_model_multiplier,
                'dst': dst,
                'rsi_length': rsi_length,
                'bbands_length': bbands_length,
                'bbands_std': bbands_std,
                'rsi_upper': rsi_upper,
                'rsi_lower': rsi_lower
            })

    if all_trades:
        pd.DataFrame(all_trades).to_sql(name='daily_trades_verification', con=engine, if_exists='append', index=False)
        print("✅ Trades saved for this parameter combination.")

print("\n🎯 All combinations processed successfully.")
