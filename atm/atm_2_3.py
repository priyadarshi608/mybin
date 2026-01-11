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

engine = create_engine(f'mysql+pymysql://{db_user}:{db_password}@{db_host}/{db_name}')

# -------------------------------
# 2. List of symbols
# -------------------------------
symbols = ['AXISBANK']

# -------------------------------
# 3. Hyperparameter arrays
# -------------------------------
variance_rolling_window_arr = [120]
# variance_rolling_window_arr = [60, 90, 120, 150, 180, 210, 240]
garch_input_window_arr = [60]
# garch_input_window_arr = [60, 90, 120, 150, 180, 210, 240]
premium_std_window_arr = [180]
# premium_std_window_arr = [60, 90, 120, 150, 180, 210, 240]

# Looks like value of garch_p and garch_q doesn't matter
garch_p_arr = [1]
garch_q_arr = [3]
garch_model_multiplier_arr = [100]
dst_arr = [1]
rsi_length_arr = [50]
bbands_length_arr = [50]
# rsi_length_arr = [41]
# bbands_length_arr = [41]
bbands_std_arr = [2.5]
rsi_upper_arr = [75]
rsi_lower_arr = [25]
# garch_p_arr = [3]
# garch_q_arr = [1, 4]
# garch_model_multiplier_arr = [1, 10, 100, 500, 1000]
# dst_arr = [0.8, 1.0, 1.2]
# rsi_length_arr = [30, 41, 50]
# bbands_length_arr = [30, 41, 50]
# bbands_std_arr = [1.5, 2.0, 2.5]
# rsi_upper_arr = [65, 70, 75]
# rsi_lower_arr = [25, 30, 35]

# -------------------------------
# 4. Collect all results
# -------------------------------
all_trades = []

# -------------------------------
# 5. Process each symbol
# -------------------------------
for symbol_name in symbols:
    print(f"\nProcessing {symbol_name}...")

    daily_query = f"""
    SELECT start_timestamp AS Date, open, high, low, close, volume
    FROM market_data
    WHERE symbol = '{symbol_name}' AND duration = 'day'
    ORDER BY start_timestamp
    """
    daily_df = pd.read_sql(daily_query, engine)
    daily_df['Date'] = pd.to_datetime(daily_df['Date'])
    daily_df = daily_df.set_index('Date')
    daily_df['Adj Close'] = daily_df['close']

    daily_df.rename(columns={
        'open': 'Open', 'high': 'High', 'low': 'Low', 'close': 'Close', 
        'Adj Close': 'Adj Close', 'volume': 'Volume'
    }, inplace=True)

    daily_df = daily_df[['Open', 'High', 'Low', 'Close', 'Adj Close', 'Volume']]
    daily_df['log_ret'] = np.log(daily_df['Adj Close']).diff()
    daily_df = daily_df['2015':]

    # -------------------------------
    # Iterate over hyperparameters for tuning
    # -------------------------------
    for variance_rolling_window in variance_rolling_window_arr:
        daily_df['variance'] = daily_df['log_ret'].rolling(variance_rolling_window).var()

        for garch_input_window in garch_input_window_arr:
            def predict_volatility(x, garch_model_multiplier, garch_p, garch_q):
                best_model = arch_model(y=x * garch_model_multiplier, p=garch_p, q=garch_q).fit(update_freq=5, disp='off')
                variance_forecast = best_model.forecast(horizon=1).variance.iloc[-1, 0]
                return variance_forecast

            for garch_p in garch_p_arr:
                for garch_q in garch_q_arr:
                    for garch_model_multiplier in garch_model_multiplier_arr:
                        daily_df['predictions'] = daily_df['log_ret'].rolling(garch_input_window)\
                            .apply(lambda x: predict_volatility(x, garch_model_multiplier, garch_p, garch_q))
                        daily_df = daily_df.dropna()

                        for premium_std_window in premium_std_window_arr:
                            daily_df['premium_std'] = daily_df['predictions'].rolling(premium_std_window).std()
                            for dst in dst_arr:
                                daily_df['prediction_premium'] = (daily_df['predictions'] - daily_df['variance']) / daily_df['variance']
                                daily_df['signal_daily'] = daily_df.apply(
                                    lambda x: 1 if x['prediction_premium'] > dst * x['premium_std']
                                    else (-1 if x['prediction_premium'] < -1 * dst * x['premium_std'] else np.nan),
                                    axis=1
                                )
                                daily_df['signal_daily'] = daily_df['signal_daily'].shift()

                                # -------------------------------
                                # Intraday data
                                # -------------------------------
                                minute_query = f"""
                                SELECT start_timestamp AS datetime, open, high, low, close, volume
                                FROM market_data
                                WHERE symbol = '{symbol_name}' AND duration = '5minute'
                                ORDER BY start_timestamp
                                """
                                intraday_min_df = pd.read_sql(minute_query, engine)
                                intraday_min_df['datetime'] = pd.to_datetime(intraday_min_df['datetime'])
                                intraday_min_df = intraday_min_df.set_index('datetime')
                                intraday_min_df['date'] = intraday_min_df.index.normalize()
                                intraday_min_df = intraday_min_df[['open','low','high','close','volume','date']]

                                print("Daily DF shape:", daily_df.shape)
                                print("Number of non-null daily signals:", daily_df['signal_daily'].notna().sum())
                                print("First few signal_daily dates:")
                                print(daily_df[daily_df['signal_daily'].notna()].head())
                                # Merge daily signals
                                final_df = intraday_min_df.reset_index()\
                                    .merge(daily_df[['signal_daily']].reset_index(), left_on='date', right_on='Date')\
                                    .drop(['date','Date'], axis=1).set_index('datetime')
                                print("Final DF unique trade dates:", final_df.reset_index()['datetime'].dt.date.nunique())
                                exit(0)

                                for rsi_length in rsi_length_arr:
                                    final_df['rsi'] = pandas_ta.rsi(close=final_df['close'], length=rsi_length)
                                    for bbands_length in bbands_length_arr:
                                        for bbands_std in bbands_std_arr:
                                            bbands = pandas_ta.bbands(close=final_df['close'], length=bbands_length, std=bbands_std)
                                            final_df['lband'] = bbands.iloc[:, 0]
                                            final_df['uband'] = bbands.iloc[:, 2]

                                            for rsi_upper in rsi_upper_arr:
                                                for rsi_lower in rsi_lower_arr:
                                                    print(
                                                        f"Processing combination: "
                                                        f"variance_rolling_window={variance_rolling_window}, "
                                                        f"garch_input_window={garch_input_window}, "
                                                        f"garch_p={garch_p}, garch_q={garch_q}, "
                                                        f"garch_model_multiplier={garch_model_multiplier}, "
                                                        f"premium_std_window={premium_std_window}, dst={dst}, "
                                                        f"rsi_length={rsi_length}, bbands_length={bbands_length}, "
                                                        f"bbands_std={bbands_std}, rsi_upper={rsi_upper}, rsi_lower={rsi_lower}"
                                                    )
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

                                                    # -------------------------------
                                                    # Trade log creation
                                                    # -------------------------------
                                                    temp_df = final_df.reset_index()
                                                    for date, group in temp_df.groupby(temp_df['datetime'].dt.date):
                                                        if group['return_sign'].isna().all():
                                                            continue

                                                        original_group = group.copy()
                                                        group = group[group['datetime'].dt.time >= pd.to_datetime("09:16:00").time()]
                                                        if group.empty:
                                                            continue

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
                                                            # Hyperparameters used
                                                            'variance_rolling_window': variance_rolling_window,
                                                            'garch_input_window': garch_input_window,
                                                            'garch_p': garch_p,
                                                            'garch_q': garch_q,
                                                            'garch_model_multiplier': garch_model_multiplier,
                                                            'premium_std_window': premium_std_window,
                                                            'dst': dst,
                                                            'rsi_length': rsi_length,
                                                            'bbands_length': bbands_length,
                                                            'bbands_std': bbands_std,
                                                            'rsi_upper': rsi_upper,
                                                            'rsi_lower': rsi_lower
                                                        })

# -------------------------------
# 6. Write all trades to MySQL
# -------------------------------
daily_trades_df = pd.DataFrame(all_trades)

daily_trades_df.to_sql(
    name='daily_trades_hps',
    con=engine,
    if_exists='append',
    index=False
)

print("\n✅ All symbols processed and daily trades written to MySQL successfully!")
