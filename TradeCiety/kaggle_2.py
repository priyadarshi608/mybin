import pandas as pd
import mysql.connector
import numpy as np

def load_symbol_data(symbol='AXISBANK', duration='5minute'):
    """
    Load 5-minute OHLCV data from MySQL and resample to 15-minute candles.
    """
    conn = mysql.connector.connect(
        host='localhost',
        user='root',
        password='root',
        database='market'
    )
    
    query = """
        SELECT 
            start_timestamp AS date,
            open,
            high,
            low,
            close,
            volume
        FROM market_data
        WHERE symbol = %s
          AND duration = %s
          AND is_correct = 1
        ORDER BY start_timestamp ASC;
    """
    
    df = pd.read_sql(query, conn, params=(symbol, duration))
    conn.close()
    
    if df.empty:
        raise ValueError(f"No data found for {symbol} with duration {duration}")
    
    df['date'] = pd.to_datetime(df['date'])
    df.set_index('date', inplace=True)
    df.sort_index(inplace=True)
    
    # Resample 5min → 15min
    df_15 = pd.DataFrame({
        'open': df['open'].resample('15T').first(),
        'high': df['high'].resample('15T').max(),
        'low': df['low'].resample('15T').min(),
        'close': df['close'].resample('15T').last(),
        'volume': df['volume'].resample('15T').sum()
    }).dropna()
    
    print(f"✅ Loaded and resampled {len(df)} → {len(df_15)} rows for {symbol}")
    return df_15


def evaluate_avg_ppnl(df):
    """
    Compute average percentage profit/loss (ppnl)
    """
    df['ppnl'] = (df['close'] - df['open']) * 100 / df['open']
    avg_ppnl = df['ppnl'].mean()
    win_rate = (df['ppnl'] > 0).mean() * 100
    
    print("📊 Evaluation Metrics:")
    print(f"Total Candles: {len(df)}")
    print(f"Win Rate: {win_rate:.2f}%")
    print(f"Avg PPNL: {avg_ppnl:.4f}%")
    
    return avg_ppnl, win_rate


if __name__ == "__main__":
    df = load_symbol_data('AXISBANK', '5minute')
    avg_ppnl, win_rate = evaluate_avg_ppnl(df)
