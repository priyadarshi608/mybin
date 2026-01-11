import pandas as pd
import numpy as np
from arch import arch_model
import pandas_ta
from sqlalchemy import create_engine

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
# 3. Hyperparameters / constants
# -------------------------------
variance_rolling_window = 120
garch_input_window = 60
premium_std_window = 180

garch_model_multiplier = 100
garch_p = 1
garch_q = 3

dst = 1

rsi_length = 50
bbands_length = 50
bbands_std = 2.5
rsi_upper = 65
rsi_lower = 25

# -------------------------------
# 4. Collect all results
# -------------------------------
all_trades = []

# -------------------------------
# 5. Process each symbol
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
    if daily_df.empty:
        print(f"No daily data for {symbol_name}, skipping.")
        continue

    daily_df['Date'] = pd.to_datetime(daily_df['Date'])
    daily_df = daily_df.set_index('Date')
    # Use 'Close' for returns
    daily_df.rename(columns={
        'open': 'Open', 'high': 'High', 'low': 'Low', 'close': 'Close', 'volume': 'Volume'
    }, inplace=True)

    daily_df = daily_df[['Open', 'High', 'Low', 'Close', 'Volume']]
    daily_df['log_ret'] = np.log(daily_df['Close']).diff()

    # variance used in daily signal calculation
    daily_df['variance'] = daily_df['log_ret'].rolling(variance_rolling_window).var()

    # restrict to recent history if you do that elsewhere
    daily_df = daily_df['2015':]

    if daily_df.empty:
        print(f"No daily data after slicing for {symbol_name}, skipping.")
        continue

    # -------------------------------
    # Predict volatility (GARCH) function
    # -------------------------------
    def predict_volatility(x, multiplier=garch_model_multiplier, p=garch_p, q=garch_q):
        # defensive: if x has NaNs or too few observations, return NaN
        if len(x) < max(5, p + q + 1) or np.isnan(x).any():
            return np.nan
        try:
            # multiply by multiplier (100 in your code) to scale returns for arch
            best_model = arch_model(y=x * multiplier, p=p, q=q).fit(update_freq=5, disp='off')
            variance_forecast = best_model.forecast(horizon=1).variance.iloc[-1, 0]
            return variance_forecast
        except Exception as e:
            # if model fitting fails, return NaN and continue
            # printing minimal info to help debugging
            print(f"Warning: GARCH fit failed for {symbol_name} window ending {x.index[-1] if hasattr(x, 'index') else 'N/A'}: {e}")
            return np.nan

    # -------------------------------
    # Compute predictions for DAILY
    # -------------------------------
    # Apply rolling GARCH forecast on the log returns
    daily_df['predictions'] = daily_df['log_ret'].rolling(garch_input_window).apply(
        lambda arr: predict_volatility(arr)
    )

    # drop rows that don't have predictions/variance etc.
    daily_df = daily_df.dropna(subset=['predictions', 'variance'])

    if daily_df.empty:
        print(f"No daily rows with predictions for {symbol_name}, skipping.")
        continue

    # -------------------------------
    # DAILY signal generation
    # -------------------------------
    daily_df['prediction_premium'] = (daily_df['predictions'] - daily_df['variance']) / daily_df['variance']
    daily_df['premium_std'] = daily_df['prediction_premium'].rolling(premium_std_window).std()

    daily_df['signal_daily'] = np.nan
    daily_df.loc[daily_df['prediction_premium'] > dst * daily_df['premium_std'], 'signal_daily'] = 1
    daily_df.loc[daily_df['prediction_premium'] < -dst * daily_df['premium_std'], 'signal_daily'] = -1

    # shift daily signal so that today's trade uses yesterday's daily signal (avoid lookahead)
    daily_df['signal_daily'] = daily_df['signal_daily'].shift()

    # -------------------------------
    # WEEKLY aggregation and signal generation (same logic applied on weekly candles)
    # -------------------------------
    # Create weekly candles (week ending Sunday). We'll map weekly signal to intraday by week start time.
    weekly_df = daily_df[['Open', 'High', 'Low', 'Close', 'Volume']].resample('W').agg({
        'Open': 'first', 'High': 'max', 'Low': 'min', 'Close': 'last', 'Volume': 'sum'
    }).dropna()

    # If weekly_df empty skip weekly logic
    if not weekly_df.empty:
        weekly_df['log_ret'] = np.log(weekly_df['Close']).diff()

        # Use scaled rolling windows for weekly (reasonable heuristic)
        # but keep using same hyperparams logic; you may want to tune these separately later.
        weekly_variance_window = max(3, int(variance_rolling_window / 5))
        weekly_garch_window = max(3, int(garch_input_window / 5))
        weekly_premium_std_window = max(3, int(premium_std_window / 5))

        weekly_df['variance'] = weekly_df['log_ret'].rolling(weekly_variance_window).var()
        weekly_df['predictions'] = weekly_df['log_ret'].rolling(weekly_garch_window).apply(
            lambda arr: predict_volatility(arr)
        )
        weekly_df = weekly_df.dropna(subset=['predictions', 'variance'])

        if not weekly_df.empty:
            weekly_df['prediction_premium'] = (weekly_df['predictions'] - weekly_df['variance']) / weekly_df['variance']
            weekly_df['premium_std'] = weekly_df['prediction_premium'].rolling(weekly_premium_std_window).std()

            weekly_df['signal_weekly'] = np.nan
            weekly_df.loc[weekly_df['prediction_premium'] > dst * weekly_df['premium_std'], 'signal_weekly'] = 1
            weekly_df.loc[weekly_df['prediction_premium'] < -dst * weekly_df['premium_std'], 'signal_weekly'] = -1

            # shift weekly signal to avoid lookahead (use prior week's signal for current week)
            weekly_df['signal_weekly'] = weekly_df['signal_weekly'].shift()

            # compute a week_start timestamp for merging with intraday rows
            # use the period's start_time as the week identifier
            weekly_df = weekly_df.reset_index()
            weekly_df['week_start'] = weekly_df['Date'].dt.to_period('W').apply(lambda p: p.start_time)
            weekly_signals = weekly_df[['week_start', 'signal_weekly']].copy()
        else:
            weekly_signals = pd.DataFrame(columns=['week_start', 'signal_weekly'])
    else:
        weekly_signals = pd.DataFrame(columns=['week_start', 'signal_weekly'])

    # -------------------------------
    # Load intraday (5-minute) data
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
    if intraday_min_df.empty:
        print(f"No intraday data for {symbol_name}, skipping.")
        continue

    intraday_min_df['datetime'] = pd.to_datetime(intraday_min_df['datetime'])
    intraday_min_df = intraday_min_df.set_index('datetime')
    intraday_min_df['date'] = intraday_min_df.index.normalize()
    intraday_min_df = intraday_min_df[['open', 'low', 'high', 'close', 'volume', 'date']]

    # -------------------------------
    # Merge daily signals into intraday
    # -------------------------------
    merged = intraday_min_df.reset_index().merge(
        daily_df[['signal_daily']].reset_index(),
        left_on='date', right_on='Date', how='left'
    ).drop(['date', 'Date'], axis=1).set_index('datetime')

    final_df = merged.copy()

    # -------------------------------
    # Map weekly signal to intraday rows using week_start
    # -------------------------------
    # compute week_start for each intraday timestamp (same approach as weekly_df)
    final_df = final_df.reset_index()
    final_df['week_start'] = final_df['datetime'].dt.to_period('W').apply(lambda p: p.start_time)

    if not weekly_signals.empty:
        final_df = final_df.merge(weekly_signals, on='week_start', how='left')
    else:
        final_df['signal_weekly'] = np.nan

    final_df = final_df.set_index('datetime')

    # -------------------------------
    # Intraday indicators and intraday signal
    # -------------------------------
    final_df['rsi'] = pandas_ta.rsi(close=final_df['close'], length=rsi_length)
    bbands = pandas_ta.bbands(close=final_df['close'], length=bbands_length, std=bbands_std)
    # pandas_ta returns a dataframe or None; handle defensively
    if bbands is None:
        final_df['lband'] = np.nan
        final_df['uband'] = np.nan
    else:
        final_df['lband'] = bbands.iloc[:, 0]
        final_df['uband'] = bbands.iloc[:, 2]

    final_df['signal_intraday'] = final_df.apply(
        lambda x: 1 if (x['rsi'] > rsi_upper) and (x['close'] > x['uband'])
        else (-1 if (x['rsi'] < rsi_lower) and (x['close'] < x['lband']) else np.nan),
        axis=1
    )

    # -------------------------------
    # Final return_sign: intersection of weekly, daily and intraday signals
    # Note: using the same polarity-flip logic you used earlier where:
    #   - when daily & intraday were both 1 you used -1, and when both -1 you used 1.
    # To stay consistent with your prior code, I apply the same mapping but with weekly included.
    # -------------------------------
    def compute_return_sign(row):
        w = row.get('signal_weekly', np.nan)
        d = row.get('signal_daily', np.nan)
        i = row.get('signal_intraday', np.nan)

        # require all three to be non-nan and equal
        if pd.notna(w) and pd.notna(d) and pd.notna(i):
            if (w == 1) and (d == 1) and (i == 1):
                return -1  # keep your original convention
            if (w == -1) and (d == -1) and (i == -1):
                return 1
        return np.nan

    final_df['return_sign'] = final_df.apply(compute_return_sign, axis=1)
    final_df['return_sign'] = final_df.groupby(pd.Grouper(freq='D'))['return_sign'].transform(lambda x: x.ffill())

    # -------------------------------
    # forward returns & strategy return
    # -------------------------------
    final_df['return'] = np.log(final_df['close']).diff()
    final_df['forward_return'] = final_df['return'].shift(-1)
    final_df['strategy_return'] = final_df['forward_return'] * final_df['return_sign']

    # -------------------------------
    # Trade log creation (same behavior as you had)
    # -------------------------------
    temp_df = final_df.reset_index()
    for trade_date, group in temp_df.groupby(temp_df['datetime'].dt.date):
        # skip if no entry signal for that day
        if group['return_sign'].isna().all():
            continue

        original_group = group.copy()

        # Entry after 9:16:00
        intraday_group = group[group['datetime'].dt.time >= pd.to_datetime("09:16:00").time()]
        if intraday_group.empty:
            continue

        # Exit fixed at 15:15:00 or last available candle
        exit_row = intraday_group[intraday_group['datetime'].dt.time == pd.to_datetime("15:15:00").time()]
        if exit_row.empty:
            exit_row = intraday_group.iloc[[-1]]

        first_signal_idx = intraday_group['return_sign'].first_valid_index()
        if first_signal_idx is None:
            continue

        position = intraday_group.loc[first_signal_idx, 'return_sign'] * -1
        position_price = intraday_group.loc[first_signal_idx, 'open']
        position_timestamp = intraday_group.loc[first_signal_idx, 'datetime']

        exit_price = exit_row['close'].iloc[0]
        exit_timestamp = exit_row['datetime'].iloc[0]

        open_price = original_group['open'].iloc[0]
        close_price = intraday_group['close'].iloc[-1]

        pp = round(((exit_price - position_price) * position / position_price) * 100, 2)

        all_trades.append({
            'symbol': symbol_name,
            'trade_date': trade_date,
            'open_price': open_price,
            'close_price': close_price,
            'position_price': position_price,
            'position_timestamp': position_timestamp,
            'exit_price': exit_price,
            'exit_timestamp': exit_timestamp,
            'position': position,
            'pp': pp
        })

# -------------------------------
# 6. Write all trades to MySQL
# -------------------------------
daily_trades_df = pd.DataFrame(all_trades)

if not daily_trades_df.empty:
    daily_trades_df.to_sql(
        name='daily_trades_with_weekly',
        con=engine,
        if_exists='append',
        index=False
    )
    print(f"\n✅ Wrote {len(daily_trades_df)} trades to daily_trades_with_weekly")
else:
    print("\nNo trades to write.")

print("\n✅ All symbols processed!")
