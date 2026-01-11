    # # New candle data you just got
    # new_row = {
    #     'open': 1200.30,
    #     'high': 1200.90,
    #     'low': 1200.30,
    #     'close': 1202.40,
    #     'volume': 89637,
    #     'is_correct': 0
    # }
    # # Timestamp of the new bar
    # new_timestamp = pd.Timestamp('2025-10-17 15:25:00')

    # # Step 1️⃣ — Append the new row to your DataFrame
    # df.loc[new_timestamp, ['open', 'high', 'low', 'close', 'volume', 'is_correct']] = [
    #     new_row['open'],
    #     new_row['high'],
    #     new_row['low'],
    #     new_row['close'],
    #     new_row['volume'],
    #     new_row['is_correct']
    # ]

    # # Recalculate EMA columns (ema9 and ema21)
    # df['ema9'] = df['close'].ewm(span=9, adjust=False).mean()
    # df['ema21'] = df['close'].ewm(span=21, adjust=False).mean()
    # # Update the date column
    # df['date'] = df.index.date
    # print(df)

    # # IN CASE, INCORRECT VALUES FOR ANY TIME'S ENTRY
    # # Update close prices ---
    # df.loc[pd.Timestamp('2025-10-17 15:25:00'), 'close'] = 1200.40
    # df.loc[pd.Timestamp('2025-10-17 15:25:00'), 'is_correct'] = 1
    # # Recalculate EMA columns fully ---
    # df['ema9'] = df['close'].ewm(span=9, adjust=False).mean()
    # df['ema21'] = df['close'].ewm(span=21, adjust=False).mean()
    # # Update date column (optional if needed) ---
    # df['date'] = df.index.date
    # print(df)
