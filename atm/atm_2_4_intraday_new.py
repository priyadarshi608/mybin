import pandas as pd
import numpy as np
from datetime import timedelta
import pandas_ta
from sqlalchemy import create_engine

# === MAXIMIZE PANDAS DISPLAY ===
pd.set_option('display.max_rows', None)
pd.set_option('display.max_columns', None)
pd.set_option('display.width', None)
pd.set_option('display.max_colwidth', None)
pd.set_option('display.float_format', '{:.6f}'.format)

# -------------------------------
# 1. Database connection
# -------------------------------
db_user = 'root'
db_password = 'root'
db_host = 'localhost'
db_name = 'market'
engine = create_engine(f'mysql+pymysql://{db_user}:{db_password}@{db_host}/{db_name}')

# -------------------------------
# 2. Parameters
# -------------------------------
symbols = ['APOLLOHOSP']
rsi_length = 50
bbands_length = 50
bbands_std = 2.5
rsi_upper = 65
rsi_lower = 25
trading_date = '2025-10-17'

all_trades = []

# -------------------------------
# 3. Process each symbol
# -------------------------------
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

    # Load data up to 09:15:00
    initial_query = f"""
    SELECT start_timestamp AS datetime, open, high, low, close, volume
    FROM market_data
    WHERE symbol = '{symbol_name}' AND duration = '5minute'
      AND start_timestamp <= '{trading_date} 09:15:00'
    ORDER BY start_timestamp
    """
    print(f"{symbol_name} : initial_query: " + initial_query)

    intraday_min_df = pd.read_sql(initial_query, engine)
    intraday_min_df['datetime'] = pd.to_datetime(intraday_min_df['datetime'])
    intraday_min_df = intraday_min_df.set_index('datetime')
    intraday_min_df['date'] = intraday_min_df.index.normalize()
    intraday_min_df = intraday_min_df[['open', 'low', 'high', 'close', 'volume', 'date']]

    # 5-min intervals
    intervals = pd.date_range(f"{trading_date} 09:20:00", f"{trading_date} 15:15:00", freq='5min')
    prev_end_time = pd.Timestamp(f"{trading_date} 09:15:00")

    # State variables
    entry_found = False
    entry_time = None
    entry_price = None
    position = None
    final_df_at_entry = None  # Save final_df when signal fires

    for current_end_time in intervals:
        print(f"current_end_time: {current_end_time}")
        start_range = prev_end_time.strftime('%Y-%m-%d %H:%M:%S')
        end_range = current_end_time.strftime('%Y-%m-%d %H:%M:%S')

        new_query = f"""
        SELECT start_timestamp AS datetime, open, high, low, close, volume
        FROM market_data
        WHERE symbol = '{symbol_name}' AND duration = '5minute'
          AND start_timestamp > '{start_range}' AND start_timestamp <= '{end_range}'
        ORDER BY start_timestamp
        """
        print(f"{symbol_name} : new_query: " + new_query)

        new_df = pd.read_sql(new_query, engine)
        print(new_df)
        print("========================================")
        if not new_df.empty:
            new_df['datetime'] = pd.to_datetime(new_df['datetime'])
            new_df = new_df.set_index('datetime')
            new_df['date'] = new_df.index.normalize()
            new_df = new_df[['open', 'low', 'high', 'close', 'volume', 'date']]
            intraday_min_df = pd.concat([intraday_min_df, new_df])

        final_df = intraday_min_df.reset_index().copy()
        cutoff_date = pd.Timestamp('2016-06-20')
        final_df['signal_daily'] = np.where(final_df['date'] >= cutoff_date, 1.000000, np.nan)
        final_df = final_df.drop(columns=['date']).set_index('datetime')

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
        final_df['return_sign'] = final_df.groupby(pd.Grouper(freq='D'))['return_sign'].transform('ffill')

        target_date = pd.Timestamp(trading_date)
        final_df_date = final_df[final_df.index.date == target_date.date()]

        if not final_df_date.empty:
            print(f"\n--- FINAL_DF FOR {symbol_name} ON {trading_date} UPDATED AT {current_end_time} ---")
            print(final_df_date)
            print(f"--- END OF FINAL_DF ---\n")

            # === FIRST NON-NAN signal_intraday → ENTER NEXT CANDLE ===
            if final_df_date['signal_intraday'].notna().any():
                print("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%")
                signal_row = final_df_date[final_df_date['signal_intraday'].notna()].iloc[0]
                signal_time = signal_row.name
                position = 1 if signal_row['signal_intraday'] == 1 else -1  # short if 1, long if -1

                # === FIX FORWARD BIAS: ENTER ON NEXT CANDLE OPEN ===
                next_candle_time = signal_time + timedelta(minutes=5)
                next_candle_query = f"""
                SELECT open
                FROM market_data
                WHERE symbol = '{symbol_name}'
                  AND duration = '5minute'
                  AND start_timestamp = '{next_candle_time}'
                """
                print(f"{symbol_name} : next_candle_query: " + next_candle_query)
                next_df = pd.read_sql(next_candle_query, engine)
                if not next_df.empty:
                    entry_price = next_df['open'].iloc[0]
                    entry_time = next_candle_time
                    print(f"Signal confirmed at {signal_time} → Entry at next candle {entry_time} open: {entry_price}")
                else:
                    print(f"Warning: No next candle found for {symbol_name} at {next_candle_time}. Skipping trade.")
                    break

                entry_found = True
                final_df_at_entry = final_df.copy()
                print(f"SIGNAL! Entry at {entry_time}, Price: {entry_price}, Position: {position}")
                break

            if current_end_time.time() >= pd.Timestamp("15:15:00").time():
                print(f"Reached 15:15:00. Fetching exit candle...")
                break

        prev_end_time = current_end_time

    # === EXIT AT 15:15:00 OR LAST CANDLE ===
    if entry_found:
        exit_query = f"""
        SELECT open
        FROM market_data
        WHERE symbol = '{symbol_name}'
          AND duration = '5minute'
          AND start_timestamp = '{trading_date} 15:15:00'
        """
        print(f"{symbol_name} : exit_query: " + exit_query)

        exit_df = pd.read_sql(exit_query, engine)
        if not exit_df.empty:
            exit_price = exit_df['open'].iloc[0]
            exit_time = pd.Timestamp(f"{trading_date} 15:15:00")
            print(f"Exit price fetched from DB: {exit_price} at 15:15:00")
        else:
            last_row = intraday_min_df.iloc[-1]
            exit_price = last_row['open']
            exit_time = last_row.name
            print(f"Warning: 15:15:00 candle not found. Using last candle open: {exit_price} at {exit_time}")

        # === FETCH DAY OPEN ===
        daily_open_query = f"""
        SELECT open
        FROM market_data
        WHERE symbol = '{symbol_name}'
          AND duration = '5minute'
          AND start_timestamp = '{trading_date} 09:15:00'
        """
        print(f"{symbol_name} : daily_open_query: " + daily_open_query)

        daily_df = pd.read_sql(daily_open_query, engine)
        if not daily_df.empty:
            day_open = daily_df['open'].iloc[0]
            print(f"Daily data: Open={day_open}")
        else:
            day_open = -1.00
            print(f"Warning: Daily data not found. Using intraday: Open={day_open}")

        # === FETCH DAY CLOSE ===
        daily_close_query = f"""
        SELECT close
        FROM market_data
        WHERE symbol = '{symbol_name}'
          AND duration = '5minute'
          AND start_timestamp = '{trading_date} 15:25:00'
        """
        daily_df = pd.read_sql(daily_close_query, engine)
        if not daily_df.empty:
            day_close = daily_df['close'].iloc[0]
        else:
            day_close = -1.00
            print(f"Warning: Daily data not found. Using intraday: Open={day_close}")

        pp = round(((exit_price - entry_price) * position / entry_price) * 100, 2)

        all_trades.append({
            'symbol': symbol_name,
            'trade_date': trading_date,
            'open_price': day_open,
            'close_price': day_close,
            'position_price': entry_price,
            'position_timestamp': entry_time,
            'exit_price': exit_price,
            'exit_timestamp': exit_time,
            'position': position,
            'pp': pp
        })
        print(f"Trade saved: Entry {entry_time}, Exit {exit_time}, P&L: {pp}%")
    else:
        print("No signal found today.")

# -------------------------------
# 4. Write to MySQL
# -------------------------------
if all_trades:
    daily_trades_df = pd.DataFrame(all_trades)
    daily_trades_df.to_sql(
        name='daily_trades_65_intraday',
        con=engine,
        if_exists='append',
        index=False
    )
    print(f"\n{len(all_trades)} trade(s) saved to daily_trades_intraday")
else:
    print("\nNo trades to save.")

print("All done!")
