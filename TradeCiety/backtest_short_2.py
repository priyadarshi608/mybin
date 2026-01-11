#!/usr/bin/env python3
"""
backtest.py — Single-file institutional-imbalance backtester (final safe version, split in 3 parts)

Part 1 of 3: imports, constants, DB helpers, data loader & preprocessing
"""

import argparse
import logging
from datetime import datetime, time
from concurrent.futures import ThreadPoolExecutor, as_completed
import math
import mysql.connector
import pandas as pd
import numpy as np
import sys
import traceback

# -------------------------------
# Logging setup
# -------------------------------
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s — %(levelname)s — %(message)s"
)

# -------------------------------
# DB config (hard-coded)
# -------------------------------
DB_CONF = {
    "host": "localhost",
    "user": "root",
    "password": "root",
    "database": "market",
    "autocommit": True,
}

# -------------------------------
# Strategy constants (tweakable)
# -------------------------------
SPIKE_VOL_MULTIPLIER = 20           # first spike multiplier
SECOND_SPIKE_MULTIPLIER = 10        # second spike multiplier
VALUE_THRESHOLD = 4e7               # 4 crore
ACCUM_VOLUME_RATIO = 0.1
ACCUM_RANGE_RATIO = 0.4
ACCUM_COUNT_THRESHOLD = 3
MIN_ROWS_PER_DAY = 375              # days with < this many is_correct rows are dropped

NO_ENTRY_AFTER = time(15, 10)       # no entries after this time (HH:MM)
SQUARE_OFF_TIME = time(15, 15)      # square off at this candle's OPEN

RESULTS_TABLE = "backtest_short_2_results"

# final desired table creation SQL (includes initial_stoploss & target_price)
CREATE_RESULTS_SQL = f"""
CREATE TABLE IF NOT EXISTS `{RESULTS_TABLE}` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(32),
    entry_time DATETIME,
    exit_time DATETIME,
    entry_price DOUBLE,
    exit_price DOUBLE,
    initial_stoploss DOUBLE,
    target_price DOUBLE,
    vwap_dist_entry DOUBLE,
    pnl DOUBLE,
    ppnl DOUBLE,
    rr DOUBLE,
    reason VARCHAR(32)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
"""

# -------------------------------
# DB helpers
# -------------------------------
def get_db_conn():
    return mysql.connector.connect(**DB_CONF)


def ensure_results_table():
    """
    Create table if missing. If existing table lacks initial_stoploss or target_price,
    ALTER to add them.
    """
    conn = get_db_conn()
    cur = conn.cursor()
    try:
        cur.execute(CREATE_RESULTS_SQL)
    except Exception as e:
        logging.error("Failed to ensure results table (CREATE): %s", e)
        cur.close()
        conn.close()
        raise

    # verify columns and alter if needed
    try:
        cur.execute(f"SHOW COLUMNS FROM `{RESULTS_TABLE}`")
        cols = {row[0] for row in cur.fetchall()}
        alter_clauses = []
        if "initial_stoploss" not in cols:
            alter_clauses.append("ADD COLUMN initial_stoploss DOUBLE")
        if "target_price" not in cols:
            alter_clauses.append("ADD COLUMN target_price DOUBLE")
        if alter_clauses:
            alter_sql = f"ALTER TABLE `{RESULTS_TABLE}` " + ", ".join(alter_clauses)
            cur.execute(alter_sql)
            logging.info("Altered results table to add missing columns: %s", alter_clauses)
    except Exception as e:
        logging.error("Failed to verify/alter results table: %s", e)
    finally:
        cur.close()
        conn.close()


def insert_trade_row_rounded(row):
    """
    Insert trade row into DB with numeric rounding to 2 decimals.
    Expected keys:
      symbol, entry_time (datetime), exit_time (datetime),
      entry_price, exit_price, initial_stoploss, target_price,
      pnl, ppnl, rr, reason
    """
    conn = get_db_conn()
    cur = conn.cursor()
    ins = f"""
    INSERT INTO `{RESULTS_TABLE}`
    (symbol, entry_time, exit_time, entry_price, exit_price, initial_stoploss, target_price, vwap_dist_entry, pnl, ppnl, rr, reason)
    VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
    """
    try:
        params = (
            row["symbol"],
            row["entry_time"].strftime("%Y-%m-%d %H:%M:%S"),
            row["exit_time"].strftime("%Y-%m-%d %H:%M:%S"),
            round(float(row["entry_price"]), 2),
            round(float(row["exit_price"]), 2),
            round(float(row["initial_stoploss"]), 2),
            round(float(row["target_price"]), 2),
            round(float(row["vwap_dist_entry"]), 2),
            round(float(row["pnl"]), 2),
            round(float(row["ppnl"]), 2),
            round(float(row["rr"]), 2),
            row["reason"]
        )
        cur.execute(ins, params)
    except Exception as e:
        logging.error("Failed to insert trade row: %s", e)
        logging.debug("Offending row: %s", row)
        raise
    finally:
        cur.close()
        conn.close()


# -------------------------------
# Data loader & preprocessing
# -------------------------------
def load_symbol_minute_df(symbol, start=None, end=None):
    """
    Loads minute data for symbol, filters by start/end if provided.
    Drops whole days where count(is_correct=1) < MIN_ROWS_PER_DAY.
    Returns df indexed by timestamp with columns:
    ['open','high','low','close','volume','is_correct','value','day']
    """
    conn = get_db_conn()
    q = """
        SELECT start_timestamp, open, high, low, close, volume, is_correct
        FROM market_data
        WHERE symbol = %s AND duration = 'minute'
    """
    params = [symbol]
    if start:
        q += " AND start_timestamp >= %s"
        params.append(start)
    if end:
        q += " AND start_timestamp <= %s"
        params.append(end)
    q += " ORDER BY start_timestamp ASC"

    try:
        df = pd.read_sql(q, conn, params=params)
    except Exception as e:
        conn.close()
        logging.error("Failed to load data for %s: %s", symbol, e)
        raise
    conn.close()

    if df.empty:
        return df

    df["timestamp"] = pd.to_datetime(df["start_timestamp"])
    df.set_index("timestamp", inplace=True)
    df.drop(columns=["start_timestamp"], inplace=True)

    # types
    for c in ["open", "high", "low", "close"]:
        df[c] = pd.to_numeric(df[c], errors="coerce")
    df["volume"] = pd.to_numeric(df["volume"], errors="coerce").fillna(0).astype(int)
    df["is_correct"] = pd.to_numeric(df["is_correct"], errors="coerce").fillna(0).astype(int)
    df["value"] = df["close"] * df["volume"]
    df["day"] = df.index.date

    # count is_correct per day and drop bad days
    counts = df.groupby("day")["is_correct"].apply(lambda s: (s == 1).sum())
    bad_days = counts[counts < MIN_ROWS_PER_DAY].index.tolist()
    if bad_days:
        logging.info("%s: Removing %d days with < %d 'is_correct' rows", symbol, len(bad_days), MIN_ROWS_PER_DAY)
        df = df[~df["day"].isin(bad_days)]

    # keep only rows where is_correct == 1
    df = df[df["is_correct"] == 1].copy()
    df.sort_index(inplace=True)

    if df.empty:
        logging.warning("%s: No data after cleaning.", symbol)
    return df


def add_avg_volume_3day(df):
    """
    Compute avg volume of previous 1125 candles (no lookahead).
    For each row i, avg_vol_3d = mean(volume of rows [i-1125, ..., i-1]).
    """
    if df.empty:
        return df

    df = df.copy()

    WINDOW = 1125  # 1125 previous 1-min candles (~3 trading days)

    # rolling window WITHOUT including the current candle
    df["avg_vol_3d"] = (
        df["volume"]
        .rolling(window=WINDOW, min_periods=1)
        .mean()
        .shift(1)   # <-- ensures NO LOOKAHEAD
    )

    return df

# Part 2 of 3: spike helpers and core backtest logic

# -------------------------------
# Spike helpers
# -------------------------------
def first_spike_confirmed_past5(df, idx):
    """
    Confirm first spike at index idx only if candles idx-4..idx each satisfy spike_condition_single.
    This uses only current/past data (no lookahead).
    """
    if idx < 4:
        return False, 0
    reference_row = df.iloc[idx - 4]
    avg_vol_3day = reference_row.get("avg_vol_3d", np.nan)
    if pd.isna(avg_vol_3day):
        return False, 0
    for k in range(idx - 4, idx + 1):
        row = df.iloc[k]
        if not (
                (row["volume"] >= SPIKE_VOL_MULTIPLIER * avg_vol_3day) and
                (row["value"] >= VALUE_THRESHOLD)
            ):
            return False, 0
    return True, avg_vol_3day


def is_second_spike(row, avg_vol_3day):
    """
    Second spike check uses avg_vol_3d in the row (past info only).
    Actual comparison with accumulation max vol is done outside (accum_max_vol).
    """
    return row["volume"] >= SECOND_SPIKE_MULTIPLIER * avg_vol_3day


def compute_vwap_for_day_up_to(df, idx):
    """Compute VWAP for the day of df.iloc[idx] using only rows up to idx inclusive."""
    day = df.iloc[idx].name.date()
    day_slice = df[df["day"] == day]
    day_slice = day_slice[day_slice.index <= df.index[idx]]
    if day_slice.empty:
        return float('nan')
    pv = (day_slice["close"] * day_slice["volume"]).sum()
    vol = day_slice["volume"].sum()
    if vol == 0:
        return float('nan')
    return pv / vol


# -------------------------------
# Backtest single symbol core
# -------------------------------
def backtest_symbol(df, symbol, capital, risk_pct, trade_day):
    """
    Runs the strategy on df (preprocessed). Returns list of trades (dicts).
    This function uses a while-loop over row index so we can jump the outer index forward
    to the exit index after simulating a trade. This prevents overlapping trades.
    """
    results = []
    if df.empty:
        return results

    idx_list = list(df.index)
    n = len(df)
    i = 0

    # detection state
    spike_index = None
    avg_vol_3day = 0
    spike_volume = None
    spike_range = None
    accum_low = None
    accum_high_vol = 0
    in_accum = False
    accum_count = 0
    position_open = False  # ensures only one position per symbol at a time
    entry_price = None
    stoploss = None
    risk_per_share = None
    vwap_dist_entry = None

    while i < n:
        try:
            row = df.iloc[i]
        except Exception:
            break
        # ----------------------------------------------------
        # NEW: Only allow entries on the target trade_day
        # ----------------------------------------------------
        if row.name.date() != trade_day:
            i += 1
            continue

        # HARD GUARD: while a position is open, we do not start any new spike/accumulation.
        # Note: in this implementation, after entry we simulate forward until exit and set i to exit index,
        # so overlapping should not occur. This guard is an extra safety.
        if position_open:
            i += 1
            continue

        # If no spike currently tracked -> check for 5-bar confirmed first spike
        if spike_index is None:
            is_spike_found, last_3d_vol = first_spike_confirmed_past5(df, i)
            # reference_row = df.iloc[i - 5]
            # avg_vol_3day = reference_row.get("avg_vol_3d", np.nan)
            # if row.name.date().isoformat() == "2025-01-15":
            #     print(f"[DEBUG] {symbol} | Time={row.name} | is_spike_found={is_spike_found} | last_3d_vol={avg_vol_3day}")
            if is_spike_found:
                # initialize accumulation tracking
                spike_index = i
                avg_vol_3day = last_3d_vol
                # compute average volume of the 5 spike candles
                spike_volume = df.iloc[i-4:i+1]["volume"].mean()
                # compute average range of the 5 spike candles
                spike_range = (df.iloc[i-4:i+1]["high"] - df.iloc[i-4:i+1]["low"]).mean()
                # spike_volume = df.iloc[i]["volume"]
                # spike_range = df.iloc[i]["high"] - df.iloc[i]["low"]
                accum_low = df.iloc[i]["low"]
                accum_high_vol = 0
                in_accum = True
                logging.info("%s: FIRST SPIKE (5-bar) confirmed at %s (vol=%d)", symbol, idx_list[i], spike_volume)
            i += 1
            continue

        # If in accumulation phase (and no position open), collect accumulation stats
        if in_accum and (not position_open):
            curr_range = row["high"] - row["low"]
            is_accum_candle = (row["volume"] < ACCUM_VOLUME_RATIO * spike_volume)
            # if row.name.date().isoformat() == "2025-03-24":
            #     print(f"[DEBUG] {symbol} | Time={row.name} | row_volume={row['volume']} | spike_volume_to_compare={ACCUM_VOLUME_RATIO * spike_volume}")
            #     print("is_accum_candle: " + str(is_accum_candle))
            #     print("accum_high_vol: " + str(accum_high_vol))

            if is_accum_candle:
                accum_low = min(accum_low, row["low"])
                accum_high_vol = max(accum_high_vol, row["volume"])
                accum_count += 1
                i += 1
                # if row.name.date().isoformat() == "2025-03-24":
                #     print("accum_count: " + str(accum_count))
                continue
            else:
                if accum_count < ACCUM_COUNT_THRESHOLD:
                    accum_count = 0
                # if row.name.date().isoformat() == "2025-03-24":
                #     print("accum_count: " + str(accum_count))

            # if row.name.date().isoformat() == "2025-03-24":
            #     print("row_volume:" + str(row["volume"]) + ", accum_high_vol: " + str(accum_high_vol))

            # Not accumulation: check for second spike
            if (
                (not position_open)
                and accum_count >= ACCUM_COUNT_THRESHOLD
                and is_second_spike(row, avg_vol_3day)
                and (row["close"] > row["open"])
                and (i >= 10)
                and (row["high"] > df.iloc[i-10:i]["high"].max())
            ):
                print(f"[DEBUG] {symbol} | Time={row.name} | row_volume:" + str(row["volume"]) + ", accum_high_vol: " + str(accum_high_vol))

                # 🚨 MUST BE SAME DAY as first spike
                spike_day = df.iloc[spike_index].name.date()
                second_spike_day = row.name.date()

                if second_spike_day != spike_day:
                    logging.info("%s: Second spike ignored at %s (different day than first spike %s).",
                                 symbol, row.name, spike_day)

                    # reset detection because pattern broken
                    spike_index = None
                    in_accum = False
                    spike_volume = None
                    spike_range = None
                    accum_low = None
                    accum_high_vol = 0
                    accum_count = 0
                    entry_price = None
                    stoploss = None
                    risk_per_share = None
                    i += 1
                    continue

                # check no entry after specified time
                candle_time = row.name.time()
                if candle_time > NO_ENTRY_AFTER:
                    logging.info("%s: Entry skipped at %s (after %s)", symbol, row.name, NO_ENTRY_AFTER.strftime("%H:%M"))
                    # reset detection
                    spike_index = None
                    in_accum = False
                    spike_volume = None
                    spike_range = None
                    accum_low = None
                    accum_high_vol = 0
                    accum_count = 0
                    entry_price = None
                    stoploss = None
                    risk_per_share = None
                    i += 1
                    continue

                # ENTRY at current close
                entry_price = float(row["close"])
                stoploss = float(accum_low)
                risk_per_share = entry_price - stoploss
                lower_stoploss = stoploss - risk_per_share * 3
                if risk_per_share <= 0:
                    logging.warning("%s: invalid risk at %s (risk_per_share <= 0). Skipping.", symbol, idx_list[i])
                    spike_index = None
                    in_accum = False
                    spike_volume = None
                    spike_range = None
                    accum_low = None
                    accum_high_vol = 0
                    accum_count = 0
                    entry_price = None
                    stoploss = None
                    risk_per_share = None
                    i += 1
                    continue

                capital_risk = capital * risk_pct
                qty = capital_risk / risk_per_share  # fractional allowed
                entry_idx = i
                entry_time = idx_list[entry_idx]

                logging.info("%s: ENTRY at %s price=%.4f SL=%.4f qty=%.4f", symbol, entry_time, entry_price, stoploss, qty)

                # mark position open
                position_open = True

            if position_open:
                # VWAP distance at entry
                vwap_at_entry = compute_vwap_for_day_up_to(df, entry_idx)
                vwap_dist_entry = (entry_price / vwap_at_entry - 1.0) * 100.0 if (not math.isnan(vwap_at_entry) and vwap_at_entry!=0) else float('nan')

                print(f"position_open : [DEBUG] {symbol} | Time={row.name}")
                # define targets
                target1 = entry_price + 1 * risk_per_share
                target2 = entry_price + 2 * risk_per_share
                target3 = entry_price + 3 * risk_per_share
                target4 = entry_price + 4 * risk_per_share

                sl1 = entry_price - 1 * risk_per_share
                sl2 = entry_price - 2 * risk_per_share
                sl3 = entry_price - 3 * risk_per_share
                sl4 = entry_price - 4 * risk_per_share

                # sl = stoploss
                sl = lower_stoploss
                sl_moved_to_be = False
                sl_moved_to_1r = False

                exit_price = None
                exit_reason = None
                exit_time = None
                exit_idx = None

                # forward-scan for exit (no lookahead)
                for j in range(entry_idx + 1, n):
                    frow = df.iloc[j]

                    # 🚫 STOP if candle time is >= square-off candle (15:15)
                    if frow.name.time() >= SQUARE_OFF_TIME:
                        break

                    if frow["high"] >= target1:
                        exit_price = target1
                        exit_reason = "TP1"
                        exit_idx = j
                        exit_time = idx_list[j]
                        break

                    # # target checks
                    # if (not sl_moved_to_be) and (frow["low"] <= sl2):
                    #     target1 = entry_price
                    #     sl_moved_to_be = True

                    # if sl_moved_to_be and (not sl_moved_to_1r) and (frow["low"] <= sl3):
                    #     target1 = entry_price - risk_per_share
                    #     sl_moved_to_1r = True

                    # SL intrabar
                    if frow["low"] <= sl:
                        exit_price = sl
                        exit_reason = "SL"
                        exit_idx = j
                        exit_time = idx_list[j]
                        break

                    # # target checks
                    # if (not sl_moved_to_be) and (frow["high"] >= target2):
                    #     sl = entry_price
                    #     sl_moved_to_be = True

                    # if sl_moved_to_be and (not sl_moved_to_1r) and (frow["high"] >= target3):
                    #     sl = entry_price + risk_per_share
                    #     sl_moved_to_1r = True

                    # if frow["high"] >= target4:
                    #     exit_price = target4
                    #     exit_reason = "TP4"
                    #     exit_idx = j
                    #     exit_time = idx_list[j]
                    #     break

                # if no exit detected, square off at 15:15 open (or fallback to last open)
                if exit_price is None:
                    exit_reason = "EOD_SQOFF"
                    exit_price = None
                    exit_time = None
                    exit_idx = None
                    for k in range(entry_idx + 1, n):
                        ts = idx_list[k]
                        if ts.time() == SQUARE_OFF_TIME:
                            exit_price = float(df.iloc[k]["open"])
                            exit_time = ts
                            exit_idx = k
                            break
                    if exit_price is None:
                        # fallback to last open
                        last_idx = n - 1
                        exit_price = float(df.iloc[last_idx]["open"])
                        exit_time = idx_list[last_idx]
                        exit_idx = last_idx

                pnl = (exit_price - entry_price) * qty
                ppnl = (pnl / capital) * 100.0
                rr = pnl / capital_risk if capital_risk != 0 else float('nan')

                trade_row = {
                    "symbol": symbol,
                    "entry_time": entry_time,
                    "exit_time": exit_time,
                    "entry_price": entry_price,
                    "exit_price": exit_price,
                    "initial_stoploss": lower_stoploss,
                    "target_price": target4,
                    "vwap_dist_entry": vwap_dist_entry,
                    "pnl": pnl,
                    "ppnl": ppnl,
                    "rr": rr,
                    "reason": exit_reason
                }

                results.append(trade_row)

                # advance outer index to the exit index + 1 so we do not process any rows during open position
                next_i = exit_idx + 1 if exit_idx is not None else i + 1

                # reset detection & position flag
                spike_index = None
                spike_volume = None
                spike_range = None
                accum_low = None
                accum_high_vol = 0
                in_accum = False
                position_open = False
                accum_count = 0
                entry_price = None
                stoploss = None
                risk_per_share = None

                # set i to next_i and continue loop (no overlapping)
                i = next_i
                continue

            # default increment
            i += 1
            continue

        # fallback safety increment
        i += 1

    return results

# Part 3 of 3: runner, orchestration, CLI

def run_backtest(symbol=None, capital=100000.0, risk_pct_percent=1.0):
    """
    Main orchestration:
      - ensures results table
      - fetches symbols (or uses single symbol)
      - for each symbol: load, preprocess, backtest, insert rows (rounded)
    """
    ensure_results_table()
    conn = get_db_conn()
    cur = conn.cursor()
    try:
        if symbol:
            symbols = [symbol]
        else:
            q = "SELECT DISTINCT symbol FROM market_data WHERE duration='minute'"
            cur.execute(q)
            symbols = [r[0] for r in cur.fetchall()]
    finally:
        cur.close()
        conn.close()

    total_inserted = 0
    risk_pct = risk_pct_percent / 100.0

    start = '2024-11-28'
    end = '2025-11-28'

    for sym in symbols:
        logging.info("Processing symbol: %s", sym)
        try:
            df = load_symbol_minute_df(sym, start, end)
            if df.empty:
                logging.info("%s: no data after cleaning; skipping.", sym)
                continue
            df = add_avg_volume_3day(df)

            # sorted list of days
            dates = sorted(df["day"].unique())

            for i in range(3, len(dates)):      # start from index 3 (4th date)
                d = dates[i]

                # include previous 3 days + current day
                prev_3_days = dates[i-3:i+1]

                day_df = df[df["day"].isin(prev_3_days)]

                if day_df.empty:
                    print("day_df.empty...............................")
                    continue

                trades = backtest_symbol(day_df, sym, capital, risk_pct, d)

                if len(trades) > 0:
                    logging.info("%s: %d trades generated on date %s", sym, len(trades), d)
                for t in trades:
                    try:
                        insert_trade_row_rounded(t)
                        total_inserted += 1
                    except Exception as e:
                        logging.error("Failed to insert trade for %s: %s", sym, e)
        except Exception as e:
            logging.error("Error processing %s: %s", sym, e)
            traceback.print_exc()

    logging.info("Backtest complete. Total rows inserted: %d", total_inserted)


def print_trade_summary(t):
    """
    Nicely print trade dict (rounded) for dry-run.
    """
    print("=== Trade ===")
    for k, v in t.items():
        if isinstance(v, float):
            print(f"{k}: {round(v,2)}")
        else:
            print(f"{k}: {v}")
    print("=============")


def parse_args():
    p = argparse.ArgumentParser(description="Single-file institutional imbalance backtester (final)")
    sub = p.add_subparsers(dest="cmd")

    pb = sub.add_parser("backtest", help="Run backtest")
    pb.add_argument("--symbol", type=str, default=None, help="Symbol (optional)")
    pb.add_argument("--start", type=str, default=None, help="Start date YYYY-MM-DD")
    pb.add_argument("--end", type=str, default=None, help="End date YYYY-MM-DD")
    pb.add_argument("--capital", type=float, default=100000.0, help="Capital")
    pb.add_argument("--risk", type=float, default=1.0, help="Risk percent per trade")
    pb.add_argument("--dry-run", action="store_true", help="Do not insert into DB; print trades")
    pb.add_argument("--limit-symbols", type=int, default=None, help="Limit number of symbols (for quick testing)")

    return p.parse_args()


def get_symbols_from_equitystocks():
    """
    Returns list of symbols from equityStocks table where category = 2
    """
    conn = get_db_conn()
    cur = conn.cursor()
    # q = "SELECT symbol FROM equityStocks WHERE category = 2 and symbol = '360ONE'"
    q = "SELECT symbol FROM equityStocks WHERE category = 2"
    cur.execute(q)
    symbols = [row[0] for row in cur.fetchall()]
    cur.close()
    conn.close()
    return symbols


def backtest_single_symbol(sym):
    print(f"\n===== Running backtest for {sym} =====\n")
    run_backtest(
        symbol=sym
    )


if __name__ == "__main__":

    symbols = get_symbols_from_equitystocks()

    MAX_WORKERS = 8   # you can increase this

    with ThreadPoolExecutor(max_workers=MAX_WORKERS) as executor:
        futures = []
        for sym in symbols:
            futures.append(
                executor.submit(
                    backtest_single_symbol,
                    sym
                )
            )

        for f in as_completed(futures):
            try:
                f.result()
            except Exception as e:
                print("Error in thread:", e)
