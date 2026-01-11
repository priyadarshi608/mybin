#!/usr/bin/env python3
"""
tc_slingshot_v2.py

Improved, production-ready intraday "Slingshot" backtester (v2).
Key upgrades (causal / no look-ahead):
 - Stricter multi-factor entry: bullish engulfing / strong bullish bar + RSI recovery (rsi <40 -> rsi >45)
 - Stronger volume requirements (volume spike >= 2x recent avg)
 - ATR-based dynamic stops and target (stop = retest_low - 0.2*ATR, target = entry + min(2*risk, 1.2*ATR))
 - Impulse must have volume >= 1.8x recent avg
 - Use previous-day context filter (prev day close < 20-bar SMA)
 - Max 3 trades/day (selectivity)
All computations are causal: indicator values are based only on past and current bars at the moment they are observed.
Connects to MySQL: host=localhost user=root password=root database=market
Reads from table: market_data
Writes trades to: trades_slingshot_v2 (created if missing)
Author: Generated for Shubham (no extra explanation)
"""

import sys
import traceback
import logging
from datetime import datetime, time, timedelta
import math

import mysql.connector
import pandas as pd
import numpy as np

# ----------------------------- Configuration -----------------------------
DB_CONFIG = {
    "host": "localhost",
    "user": "root",
    "password": "root",
    "database": "market",
    "raise_on_warnings": True,
}

SYMBOL = "AXISBANK"
DURATION = "5minute"

MAX_TRADES_PER_DAY = 3
MARKET_CLOSE_CUTOFF = time(15, 15)  # latest allowed exit time
ROLLING_LOOKBACK_BARS = 48  # lookback to find resistance
RANGE_AVG_WINDOW = 20  # for avg range & volume
VOL_EXPANSION_MULT = 1.6
IMPULSE_RANGE_MULT = 1.4
IMPULSE_VOL_MULT = 1.8
RETEST_MAX_BARS = 48
RETEST_ZONE_PCT = 0.12
BULL_MOMENTUM_MIN_RANGE_MULT = 0.6
VOLUME_SPIKE_MULT = 2.0  # tightened
RR = 2.0
STARTING_CAPITAL = 1.0
VERBOSE = False
TRADES_TABLE = "trades_slingshot_v2"
# -------------------------------------------------------------------------

# Logging
logger = logging.getLogger("tc_slingshot_v2")
handler = logging.StreamHandler(sys.stdout)
formatter = logging.Formatter("[%(asctime)s] %(levelname)s - %(message)s")
handler.setFormatter(formatter)
logger.addHandler(handler)
logger.setLevel(logging.DEBUG if VERBOSE else logging.INFO)


# ----------------------------- Utilities -----------------------------
def connect_db():
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        return conn
    except Exception:
        logger.error("DB connection failed:\n%s", traceback.format_exc())
        raise


def fetch_5m_data(conn, symbol=SYMBOL, duration=DURATION):
    q = (
        "SELECT start_timestamp, end_timestamp, open, high, low, close, volume "
        "FROM market_data "
        "WHERE symbol = %s AND duration = %s "
        "ORDER BY end_timestamp ASC"
    )
    # pandas may warn about DB-API; that's acceptable
    df = pd.read_sql(q, conn, params=(symbol, duration), parse_dates=["start_timestamp", "end_timestamp"])
    if df.empty:
        raise ValueError(f"No data for {symbol}/{duration}")
    df = df.set_index("end_timestamp").sort_index()
    return df


def ensure_trades_table(conn):
    create_sql = f"""
    CREATE TABLE IF NOT EXISTS {TRADES_TABLE} (
      id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
      symbol VARCHAR(20),
      entry_timestamp TIMESTAMP,
      entry_price DOUBLE,
      exit_timestamp TIMESTAMP,
      exit_price DOUBLE,
      stoploss DOUBLE,
      target DOUBLE,
      pnl DOUBLE,
      ppnl DOUBLE,
      trade_direction VARCHAR(10),
      reason VARCHAR(255),
      day DATE,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    """
    cur = conn.cursor()
    cur.execute(create_sql)
    conn.commit()
    cur.close()


def insert_trade(conn, trade):
    keys = ["symbol", "entry_timestamp", "entry_price", "exit_timestamp", "exit_price",
            "stoploss", "target", "pnl", "ppnl", "trade_direction", "reason", "day"]
    vals = [trade.get(k) for k in keys]
    placeholders = ", ".join(["%s"] * len(keys))
    sql = f"INSERT INTO {TRADES_TABLE} ({', '.join(keys)}) VALUES ({placeholders})"
    cur = conn.cursor()
    cur.execute(sql, vals)
    conn.commit()
    cur.close()


def compute_metrics(trades_df):
    if trades_df.empty:
        return {"avg_ppnl": 0.0, "win_rate": 0.0, "compounded_return": STARTING_CAPITAL}
    avg_ppnl = trades_df["ppnl"].mean()
    win_rate = (trades_df["ppnl"] > 0).sum() / len(trades_df)
    compounded = STARTING_CAPITAL
    for r in trades_df["ppnl"]:
        compounded *= (1.0 + r / 100.0)
    return {"avg_ppnl": float(avg_ppnl), "win_rate": float(win_rate), "compounded_return": float(compounded)}


# ----------------------------- Indicator helpers (causal) -----------------------------
def compute_atr(df, period=14):
    """
    ATR computed causally: ATR at index i uses True Range up to i and rolling mean.
    We return ATR aligned at the same index (includes current bar's TR).
    """
    high = df["high"]
    low = df["low"]
    close = df["close"]
    prev_close = close.shift(1)
    tr1 = (high - low).abs()
    tr2 = (high - prev_close).abs()
    tr3 = (low - prev_close).abs()
    tr = pd.concat([tr1, tr2, tr3], axis=1).max(axis=1)
    # Wilder's EMA-style ATR is common, but simple rolling mean (causal) is acceptable for backtest
    atr = tr.rolling(period, min_periods=1).mean()
    return atr


def compute_rsi(df, period=14):
    """
    RSI (Wilder) computed causally. At index i, RSI uses price changes up to i.
    """
    delta = df["close"].diff()
    up = delta.clip(lower=0.0)
    down = -1 * delta.clip(upper=0.0)
    # Wilder smoothing
    roll_up = up.rolling(window=period, min_periods=period).mean()
    roll_down = down.rolling(window=period, min_periods=period).mean()
    # For early values, fallback to ewm with alpha=1/period to emulate Wilder smoothing
    roll_up = roll_up.fillna(up.ewm(alpha=1.0 / period, adjust=False).mean())
    roll_down = roll_down.fillna(down.ewm(alpha=1.0 / period, adjust=False).mean())
    rs = roll_up / (roll_down.replace(0, np.nan))
    rsi = 100.0 - (100.0 / (1.0 + rs))
    rsi = rsi.fillna(50.0)  # neutral default
    return rsi


# ----------------------------- Backtest logic -----------------------------
def prepare_features(df):
    df = df.copy()
    df["range"] = (df["high"] - df["low"]).abs()
    df["body"] = df["close"] - df["open"]
    df["is_bull"] = df["close"] > df["open"]
    df["is_bear"] = df["close"] < df["open"]
    df["range_ma"] = df["range"].rolling(RANGE_AVG_WINDOW, min_periods=5).mean()
    df["vol_ma"] = df["volume"].rolling(RANGE_AVG_WINDOW, min_periods=5).mean()
    df["atr"] = compute_atr(df, period=14)
    df["rsi"] = compute_rsi(df, period=14)
    df["sma20"] = df["close"].rolling(20, min_periods=5).mean()

    # Previous-day close mapping (causal): for each bar, map the last close of prior calendar day (if exists)
    df["date"] = df.index.date
    last_close_by_day = df.groupby("date")["close"].last()
    prev_day_close_map = {}
    unique_dates = sorted(df["date"].unique())
    for idx, d in enumerate(unique_dates):
        if idx == 0:
            prev_day_close_map[d] = np.nan
        else:
            prev_day_close_map[d] = last_close_by_day.iloc[idx - 1]
    df["prev_day_close"] = df["date"].map(prev_day_close_map)

    # Shift certain indicators so they represent values available at the start of the next bar if needed.
    # However, for confirmation bar we assume we observe its close before deciding to enter at next open,
    # so using indicators computed including the confirmation bar is acceptable.
    return df


def backtest_slingshot_v2(df):
    df = prepare_features(df)
    idx = df.index
    n = len(df)
    trades = []
    trades_per_day = {}

    i = 0
    while i < n:
        ts = idx[i]
        day = ts.date()
        trades_per_day.setdefault(day, 0)
        if trades_per_day[day] >= MAX_TRADES_PER_DAY:
            i += 1
            continue

        # Need enough history
        if i < max(RANGE_AVG_WINDOW, ROLLING_LOOKBACK_BARS, 20) + 5:
            i += 1
            continue

        # 1) Resistance detection (using historical highs only up to i-1)
        history_start = max(0, i - ROLLING_LOOKBACK_BARS)
        history = df.iloc[history_start:i]  # strictly prior bars
        if history.empty:
            i += 1
            continue
        resistance_high = history["high"].max()
        if not np.isfinite(resistance_high):
            i += 1
            continue
        tol = max(resistance_high * 0.0025, 0.05)
        rz_low = resistance_high - tol
        rz_high = resistance_high + tol

        # 2) Market moves back into resistance at bar i (previous bar below zone, current bar touches)
        prev_bar = df.iloc[i - 1]
        cur_bar = df.iloc[i]
        touched_zone = (prev_bar["close"] < rz_low) and (cur_bar["high"] >= rz_low)
        if not touched_zone:
            i += 1
            continue

        # 3) Volatility expansion at resistance (current range > VOL_EXPANSION_MULT * avg_range_prior)
        avg_range_prior = df["range"].iloc[max(0, i - RANGE_AVG_WINDOW):i].mean()
        if not np.isfinite(avg_range_prior) or avg_range_prior <= 0:
            i += 1
            continue
        if cur_bar["range"] <= VOL_EXPANSION_MULT * avg_range_prior:
            i += 1
            continue

        # 4) Look for impulsive bearish move away from resistance (search forward causally)
        impulse_idx = None
        impulse_low = None
        impulse_high = None
        impulse_vol = None
        j = i + 1
        impulse_search_limit = min(n, i + 12)
        while j < impulse_search_limit:
            cand = df.iloc[j]
            range_prior_j = df["range"].iloc[max(0, j - RANGE_AVG_WINDOW):j].mean()
            vol_ma_prior_j = df["vol_ma"].iloc[j] if "vol_ma" in df.columns else np.nan
            cond_range = np.isfinite(range_prior_j) and cand["range"] > IMPULSE_RANGE_MULT * range_prior_j
            cond_bear = cand["is_bear"]
            cond_vol = True
            if np.isfinite(vol_ma_prior_j) and vol_ma_prior_j > 0:
                cond_vol = cand["volume"] >= IMPULSE_VOL_MULT * vol_ma_prior_j
            if cond_range and cond_bear and cond_vol:
                impulse_idx = j
                impulse_low = cand["low"]
                impulse_high = cand["high"]
                impulse_vol = cand["volume"]
                break
            j += 1
        if impulse_idx is None:
            i += 1
            continue

        impulse_range = impulse_high - impulse_low if (impulse_high is not None and impulse_low is not None) else None
        if not impulse_range or impulse_range <= 0:
            i = impulse_idx + 1
            continue

        # 5) Liquidity zone around impulse low
        lz_low = impulse_low - (RETEST_ZONE_PCT * impulse_range)
        lz_high = impulse_low + (RETEST_ZONE_PCT * impulse_range)

        # 6) Wait for retest into liquidity zone within RETEST_MAX_BARS (causal forward scan)
        retest_idx = None
        k = impulse_idx + 1
        retest_deadline = min(n, impulse_idx + RETEST_MAX_BARS)
        while k < retest_deadline:
            kbar = df.iloc[k]
            if (kbar["low"] <= lz_high) and (kbar["high"] >= lz_low):
                retest_idx = k
                break
            k += 1
        if retest_idx is None:
            i = impulse_idx + 1
            continue

        # 7) Day/context filter: require previous day's close < 20-bar sma (bearish context)
        prev_day_close = df.iloc[retest_idx]["prev_day_close"]
        sma20_prior = df["sma20"].iloc[retest_idx]
        if not np.isfinite(prev_day_close) or not np.isfinite(sma20_prior):
            i = retest_idx + 1
            continue
        if not (prev_day_close < sma20_prior):
            # context not satisfied
            i = retest_idx + 1
            continue

        # 8) Confirmation: look for bullish reversal after retest (within next 12 bars)
        entry_idx = None
        entry_price = None
        stoploss = None
        target = None
        m = retest_idx + 1
        entry_deadline = min(n, retest_idx + 12)
        while m < entry_deadline:
            mbar = df.iloc[m]
            # bullish engulfing OR strong bullish bar conditions
            prev = df.iloc[m - 1]
            bullish_engulf = (mbar["is_bull"] and (mbar["open"] < prev["close"]) and (mbar["close"] > prev["open"]))
            avg_range_prior_m = df["range"].iloc[max(0, m - RANGE_AVG_WINDOW):m].mean()
            vol_ma_prior_m = df["vol_ma"].iloc[m] if "vol_ma" in df.columns else np.nan
            bull_range_ok = np.isfinite(avg_range_prior_m) and (mbar["range"] > BULL_MOMENTUM_MIN_RANGE_MULT * avg_range_prior_m)
            vol_spike = True
            if np.isfinite(vol_ma_prior_m) and vol_ma_prior_m > 0:
                vol_spike = mbar["volume"] >= VOLUME_SPIKE_MULT * vol_ma_prior_m
            # RSI condition: require recent low RSI then recovery (rsi_prev < 40 and rsi_now > 45)
            rsi_prev = df["rsi"].iloc[max(0, m - 2)]  # use the previous bar's RSI (approx)
            rsi_now = df["rsi"].iloc[m]
            rsi_condition = (rsi_prev < 40.0) and (rsi_now > 45.0)
            # close above retest high (momentum reclaim)
            close_above_retest = mbar["close"] > df.iloc[retest_idx]["high"]
            if (bullish_engulf or (mbar["is_bull"] and bull_range_ok)) and vol_spike and rsi_condition and close_above_retest:
                entry_idx = m
                # entry executed at next bar open (causal realistic)
                break
            m += 1

        if entry_idx is None:
            i = retest_idx + 1
            continue

        # entry execution price and time
        if entry_idx + 1 < n:
            exec_price = df.iloc[entry_idx + 1]["open"]
            exec_time = idx[entry_idx + 1]
            search_from = entry_idx + 1
        else:
            exec_price = df.iloc[entry_idx]["close"]
            exec_time = idx[entry_idx]
            search_from = entry_idx

        # ATR at entry (causal)
        atr_at_entry = df["atr"].iloc[entry_idx]
        if not np.isfinite(atr_at_entry) or atr_at_entry <= 0:
            i = entry_idx + 1
            continue

        # stoploss just below retest low minus small ATR buffer
        retest_low = df.iloc[retest_idx]["low"]
        sl = retest_low - 0.2 * atr_at_entry
        if sl >= exec_price:
            # invalid stop (above entry), skip
            i = entry_idx + 1
            continue

        risk = exec_price - sl
        if risk <= 0:
            i = entry_idx + 1
            continue

        # target: min(2*risk, 1.2*ATR) added to entry
        tp_candidate = exec_price + min(RR * risk, 1.2 * atr_at_entry)

        # intraday time check: don't enter if entry execution after cutoff
        if exec_time.time() >= MARKET_CLOSE_CUTOFF:
            i = entry_idx + 1
            continue

        # ensure impulse volume was strong (already checked earlier), but double-check the impulse vol was >= IMPULSE_VOL_MULT * vol_ma at that time (causal)
        vol_ma_impulse = df["vol_ma"].iloc[impulse_idx]
        if np.isfinite(vol_ma_impulse) and vol_ma_impulse > 0:
            if impulse_vol < IMPULSE_VOL_MULT * vol_ma_impulse:
                i = entry_idx + 1
                continue

        # All checks passed; simulate trade forward for exits (intrabar checks)
        exit_idx = None
        exit_price = None
        exit_time = None
        reason = None
        s = search_from
        while s < n:
            s_dt = idx[s]
            s_date = s_dt.date()
            if s_date != day:
                # exit at previous bar's close
                exit_idx = s - 1
                exit_price = df.iloc[exit_idx]["close"]
                exit_time = idx[exit_idx]
                reason = "EOD_exit"
                break
            if s_dt.time() >= MARKET_CLOSE_CUTOFF:
                exit_idx = s
                exit_price = df.iloc[exit_idx]["close"]
                exit_time = idx[exit_idx]
                reason = "time_cutoff"
                break
            sbar = df.iloc[s]
            # stop hit?
            if sbar["low"] <= sl:
                exit_idx = s
                exit_price = sl
                exit_time = idx[s]
                reason = "stop_hit"
                break
            # target hit?
            if sbar["high"] >= tp_candidate:
                exit_idx = s
                exit_price = tp_candidate
                exit_time = idx[s]
                reason = "target_hit"
                break
            s += 1

        if exit_idx is None:
            exit_idx = n - 1
            exit_price = df.iloc[exit_idx]["close"]
            exit_time = idx[exit_idx]
            reason = "final_close"

        pnl = exit_price - exec_price
        ppnl = (pnl / exec_price) * 100.0

        trade = {
            "symbol": SYMBOL,
            "entry_timestamp": exec_time.to_pydatetime(),
            "entry_price": float(exec_price),
            "exit_timestamp": exit_time.to_pydatetime() if isinstance(exit_time, pd.Timestamp) else exit_time,
            "exit_price": float(exit_price),
            "stoploss": float(sl),
            "target": float(tp_candidate),
            "pnl": float(pnl),
            "ppnl": float(ppnl),
            "trade_direction": "LONG",
            "reason": reason or "executed",
            "day": day
        }

        trades.append(trade)
        trades_per_day[day] += 1

        # advance index beyond exit to avoid overlapping trades within same bars
        i = max(exit_idx + 1, entry_idx + 1)

    return trades


# ----------------------------- Runner -----------------------------
def run():
    conn = None
    try:
        conn = connect_db()
        df = fetch_5m_data(conn, symbol=SYMBOL, duration=DURATION)
        ensure_trades_table(conn)
        logger.info("Fetched %d bars for %s", len(df), SYMBOL)

        trades = backtest_slingshot_v2(df)
        logger.info("Generated %d trades", len(trades))

        # Persist trades
        for t in trades:
            try:
                insert_trade(conn, t)
            except Exception:
                logger.error("Insert failed for trade %s\n%s", t, traceback.format_exc())

        # Metrics
        if trades:
            trades_df = pd.DataFrame(trades)
            trades_df["ppnl"] = pd.to_numeric(trades_df["ppnl"], errors="coerce").fillna(0.0)
        else:
            trades_df = pd.DataFrame(columns=["ppnl"])

        metrics = compute_metrics(trades_df)

        logger.info("=== BACKTEST METRICS (v2) ===")
        logger.info("Total trades: %d", len(trades_df))
        logger.info("Average ppnl (%%): %.4f", metrics["avg_ppnl"])
        logger.info("Win rate: %.2f%%", metrics["win_rate"] * 100.0)
        logger.info("Total compounded return (starting capital=%.2f): %.6f", STARTING_CAPITAL, metrics["compounded_return"])

        if not trades_df.empty:
            cols = ["symbol", "entry_timestamp", "entry_price", "exit_timestamp", "exit_price",
                    "stoploss", "target", "pnl", "ppnl", "reason", "day"]
            trades_df = trades_df[cols]
            for c in ["entry_price", "exit_price", "stoploss", "target", "pnl", "ppnl"]:
                if c in trades_df.columns:
                    trades_df[c] = trades_df[c].apply(lambda x: round(float(x), 6))
            logger.info("Trades detail:\n%s", trades_df.to_string(index=False))
        else:
            logger.info("No trades generated with the stricter v2 filters. Consider adjusting parameters.")

    except Exception as e:
        logger.error("Fatal error:\n%s", traceback.format_exc())
        raise
    finally:
        if conn:
            conn.close()


if __name__ == "__main__":
    run()
