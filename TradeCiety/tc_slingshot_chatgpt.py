#!/usr/bin/env python3
"""
Improved Intraday Backtester — Volatility-Adaptive Mean Reversion Strategy
- Look-ahead safe (all indicators shifted by 1)
- Uses ATR-based stoploss and targets
- Adds trend and volatility filters
- Aims for 50–60% win rate, 0.5–0.6% avg ppnl
"""

import mysql.connector
import pandas as pd
import numpy as np
from datetime import time, timedelta
import logging, sys, traceback

# ---------------------------
# Configuration
# ---------------------------
DB = dict(host="localhost", user="root", password="root", database="market")

SYMBOL = "AXISBANK"
DURATION = "5minute"
TRADES_TABLE = "trades_intraday"

EARLIEST_ENTRY_TIME = time(9, 20)
LATEST_ENTRY_TIME = time(14, 50)
FORCED_EXIT_TIME = time(15, 15)
TP_MULT = 1.2       # target multiplier (ATR)
SL_MULT = 0.8       # stop multiplier (ATR)
VOL_FILTER = 0.003  # skip days where ATR% < 0.3%
TREND_FLAT_THRESH = 0.002  # ~0.2% difference between SMA20/SMA50 = sideways
MAX_TRADES_PER_DAY = 1

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
log = logging.getLogger("backtest")

# ---------------------------
# Database Helpers
# ---------------------------
def db_conn():
    return mysql.connector.connect(**DB)

def ensure_trades_table(conn):
    cur = conn.cursor()
    cur.execute(f"""
    CREATE TABLE IF NOT EXISTS `{TRADES_TABLE}` (
      id INT AUTO_INCREMENT PRIMARY KEY,
      symbol VARCHAR(20),
      entry_timestamp TIMESTAMP,
      entry_price DOUBLE,
      exit_timestamp TIMESTAMP,
      exit_price DOUBLE,
      side ENUM('LONG','SHORT'),
      stoploss DOUBLE,
      target DOUBLE,
      pnl DOUBLE,
      ppnl DOUBLE,
      notes VARCHAR(128)
    ) ENGINE=InnoDB;""")
    conn.commit()
    cur.close()

def insert_trade(conn, trade):
    cols, vals = zip(*trade.items())
    q = f"INSERT INTO {TRADES_TABLE} ({','.join(cols)}) VALUES ({','.join(['%s']*len(vals))})"
    cur = conn.cursor()
    cur.execute(q, vals)
    conn.commit()
    cur.close()

# ---------------------------
# Data & Indicators
# ---------------------------
def fetch_data(conn):
    q = """SELECT start_timestamp AS ts, open, high, low, close, volume
           FROM market_data
           WHERE symbol=%s AND duration=%s AND is_correct=1
           ORDER BY ts"""
    df = pd.read_sql(q, conn, params=[SYMBOL, DURATION], parse_dates=['ts'])
    df.set_index('ts', inplace=True)
    return df

def compute_indicators(df):
    df['sma20'] = df['close'].rolling(20).mean().shift(1)
    df['sma50'] = df['close'].rolling(50).mean().shift(1)
    df['std20'] = df['close'].rolling(20).std(ddof=0).shift(1)
    prev_close = df['close'].shift(1)
    tr = pd.concat([(df['high']-df['low']),
                    (df['high']-prev_close).abs(),
                    (df['low']-prev_close).abs()], axis=1).max(axis=1)
    df['atr14'] = tr.rolling(14).mean().shift(1)
    return df

# ---------------------------
# Strategy Logic
# ---------------------------
def time_ok(t, start, end): return start <= t.time() <= end

def backtest(df):
    trades = []
    in_position = None
    last_trade_day = None

    for i in range(len(df)-1):
        ts = df.index[i]
        nxt = df.index[i+1]
        row = df.iloc[i]
        nxt_open, nxt_high, nxt_low, nxt_close = df.iloc[i+1][['open','high','low','close']]

        if pd.isna(row['sma20']) or pd.isna(row['sma50']) or pd.isna(row['atr14']): 
            continue
        if row['close'] == 0: continue

        day = ts.date()
        # Force exit if holding
        if in_position:
            side = in_position['side']
            entry = in_position['entry_price']
            tp, sl = in_position['target'], in_position['stoploss']

            # forced time exit
            if ts.time() >= FORCED_EXIT_TIME:
                exit_px, reason = row['close'], 'time_exit'
                pnl = (exit_px-entry) if side=='LONG' else (entry-exit_px)
                ppnl = pnl/entry*100
                trades.append(dict(symbol=SYMBOL, entry_timestamp=in_position['entry_ts'],
                                   entry_price=entry, exit_timestamp=ts, exit_price=exit_px,
                                   side=side, stoploss=sl, target=tp, pnl=pnl, ppnl=ppnl, notes=reason))
                in_position = None
                continue

            if side == 'LONG':
                if row['low'] <= sl:
                    exit_px, reason = sl, 'SL_hit'
                elif row['high'] >= tp:
                    exit_px, reason = tp, 'TP_hit'
                else:
                    continue
            else:  # SHORT
                if row['high'] >= sl:
                    exit_px, reason = sl, 'SL_hit'
                elif row['low'] <= tp:
                    exit_px, reason = tp, 'TP_hit'
                else:
                    continue

            pnl = (exit_px-entry) if side=='LONG' else (entry-exit_px)
            ppnl = pnl/entry*100
            trades.append(dict(symbol=SYMBOL, entry_timestamp=in_position['entry_ts'],
                               entry_price=entry, exit_timestamp=ts, exit_price=exit_px,
                               side=side, stoploss=sl, target=tp, pnl=pnl, ppnl=ppnl, notes=reason))
            in_position = None
            last_trade_day = day
            continue

        # Skip if already traded today
        if last_trade_day == day: continue
        if not time_ok(ts, EARLIEST_ENTRY_TIME, LATEST_ENTRY_TIME): continue

        sma20, sma50, std, atr = row[['sma20','sma50','std20','atr14']]
        trend_flat = abs(sma20 - sma50)/row['close'] < TREND_FLAT_THRESH
        atr_pct = atr / row['close']

        if not trend_flat or atr_pct < VOL_FILTER: 
            continue

        long_sig = row['close'] < (sma20 - 1.2*std)
        short_sig = row['close'] > (sma20 + 1.2*std)
        if not (long_sig or short_sig):
            continue

        entry_px = nxt_open
        if long_sig:
            side='LONG'
            target=entry_px + TP_MULT*atr
            stop=entry_px - SL_MULT*atr
        else:
            side='SHORT'
            target=entry_px - TP_MULT*atr
            stop=entry_px + SL_MULT*atr

        in_position = dict(side=side, entry_price=entry_px, entry_ts=nxt,
                           target=target, stoploss=stop)
    return trades

# ---------------------------
# Main
# ---------------------------
def main():
    conn = db_conn()
    ensure_trades_table(conn)
    df = fetch_data(conn)
    df = compute_indicators(df)
    trades = backtest(df)

    if not trades:
        log.warning("No trades generated.")
        return

    for t in trades:
        insert_trade(conn, t)

    df_t = pd.DataFrame(trades)
    avg_ppnl = df_t['ppnl'].mean()
    win_rate = (df_t['ppnl'] > 0).mean()*100
    log.info(f"Total trades={len(df_t)}  Win rate={win_rate:.2f}%  Avg ppnl={avg_ppnl:.3f}%")
    log.info("Expected target: win rate 50–60%, avg ppnl 0.5–0.6%")

if __name__ == "__main__":
    try:
        main()
    except Exception:
        log.error(traceback.format_exc())
