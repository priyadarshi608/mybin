#!/usr/bin/env python3

import mysql.connector
from mysql.connector import Error
from datetime import datetime, time
import logging
import sys

# ---------------------------------------------
# CONFIG
# ---------------------------------------------
DB_CONF = {
    "host": "localhost",
    "user": "root",
    "password": "root",
    "database": "market"
}

MIN_CONTRACTION_DAYS = 1

configs = [
    {"target": 1.0, "stoploss": 0.5, "exit_strategy": "fixed"},
    {"target": 1.0, "stoploss": 0.5, "exit_strategy": "trailing"},
    {"target": 1.5, "stoploss": 0.5, "exit_strategy": "fixed"},
    {"target": 1.5, "stoploss": 0.5, "exit_strategy": "trailing"},
    {"target": 1.5, "stoploss": 0.75, "exit_strategy": "fixed"},
    {"target": 1.5, "stoploss": 0.75, "exit_strategy": "trailing"},
    {"target": 2.25, "stoploss": 0.75, "exit_strategy": "fixed"},
    {"target": 2.25, "stoploss": 0.75, "exit_strategy": "trailing"},
    {"target": 2, "stoploss": 1, "exit_strategy": "fixed"},
    {"target": 2, "stoploss": 1, "exit_strategy": "trailing"},
    {"target": 3, "stoploss": 1, "exit_strategy": "fixed"},
    {"target": 3, "stoploss": 1, "exit_strategy": "trailing"},
]

BUY_SIDE_STR = 'BUY'
SELL_SIDE_STR = 'SELL'

MAX_EXIT_TIME = time(15, 15)

POSITION_SIZE = 100000
DRY_RUN = True

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s: %(message)s",
    handlers=[logging.StreamHandler(sys.stdout)]
)
logger = logging.getLogger(__name__)


# ---------------------------------------------
# DB CONNECT
# ---------------------------------------------
def connect_mysql():
    try:
        return mysql.connector.connect(**DB_CONF)
    except Error as e:
        logger.exception("MySQL connection failed")
        sys.exit(1)


def fetch_symbols():
    conn = connect_mysql()
    cursor = conn.cursor()

    sql = """
        SELECT symbol
        FROM equityStocks
        WHERE category IN (5, 4, 3)
    """

    cursor.execute(sql)
    rows = cursor.fetchall()

    cursor.close()
    conn.close()

    # Extract just the symbol column
    symbols = [row[0] for row in rows]

    return symbols


SYMBOLS = fetch_symbols()
# SYMBOLS = ['AXISBANK']
# print(SYMBOLS)


# ---------------------------------------------
# STORE TRADE RESULT IN DB (rounded)
# ---------------------------------------------
def store_trade_result(conn, t, TARGET_PCT, STOPLOSS_PCT, EXIT_STRATEGY):
    entry_price = round(float(t["pos_price"]), 2)
    exit_price = round(float(t["exit_price"]), 2)
    pnl = round(float(t["pnl"]), 2)
    pnl_pct = round(float(t["pnl_pct"]), 2)

    target_price = round(
        entry_price * (1 + TARGET_PCT) if t["side"] == "BUY"
        else entry_price * (1 - TARGET_PCT), 2
    )

    stoploss_price = round(
        entry_price * (1 - STOPLOSS_PCT) if t["side"] == "BUY"
        else entry_price * (1 + STOPLOSS_PCT), 2
    )

    sql = """
        INSERT INTO backtest_results(
            symbol, trade_date, entry_timestamp, entry_price, side,
            target_pct, target_price, stoploss_pct, stoploss_price,
            exit_strategy, exit_timestamp, exit_price, exit_reason,
            pnl, pnl_pct
        )
        VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
    """

    vals = (
        t["symbol"],
        t["date"],
        t["pos_ts"],
        entry_price,
        t["side"],
        round(TARGET_PCT * 100, 4),
        target_price,
        round(STOPLOSS_PCT * 100, 4),
        stoploss_price,
        EXIT_STRATEGY,
        t["exit_ts"],
        exit_price,
        t["reason"],
        pnl,
        pnl_pct
    )

    cur = conn.cursor()
    cur.execute(sql, vals)
    conn.commit()
    cur.close()


# ---------------------------------------------
# FETCH CONTRACTIONS USING SQL
# ---------------------------------------------
def fetch_contractions(conn, SYMBOL):

    sql = """
    WITH data AS (
        SELECT
            symbol,
            start_timestamp AS trading_day,
            high,
            low,
            LAG(high) OVER (PARTITION BY symbol ORDER BY start_timestamp) AS prev_high,
            LAG(low)  OVER (PARTITION BY symbol ORDER BY start_timestamp) AS prev_low,
            LEAD(start_timestamp) OVER (PARTITION BY symbol ORDER BY start_timestamp) AS next_day
        FROM market_data
        WHERE symbol = %s
          AND duration = 'day'
          AND is_correct = 1
    ),

    -- Step 1: Inside-bar contraction condition
    marked AS (
        SELECT *,
               CASE
                   WHEN high < prev_high AND low > prev_low THEN 1
                   ELSE 0
               END AS is_contraction
        FROM data
    ),

    -- Step 2: Group consecutive contraction sequences
    grp AS (
        SELECT *,
               SUM(CASE WHEN is_contraction = 0 THEN 1 ELSE 0 END)
                   OVER (PARTITION BY symbol ORDER BY trading_day) AS grp_id
        FROM marked
    ),

    -- Step 3: Extract individual contraction days
    blocks AS (
        SELECT
            symbol,
            grp_id,
            trading_day,
            next_day,
            is_contraction
        FROM grp
        WHERE is_contraction = 1
    ),

    -- Step 4: Generate ALL contraction windows (rolling sub-sequences)
    windows AS (
        SELECT
            b1.symbol,
            b1.trading_day AS contraction_start_date,
            b2.trading_day AS contraction_end_date,
            ROW_NUMBER() OVER () AS window_id
        FROM blocks b1
        JOIN blocks b2
            ON b1.grp_id = b2.grp_id
           AND b2.trading_day >= b1.trading_day
    )

    -- Step 5: Attach breakout day (next day after end date)
    SELECT
        w.symbol,
        w.contraction_start_date,
        w.contraction_end_date,
        DATEDIFF(w.contraction_end_date, w.contraction_start_date) + 1 AS contraction_days,
        d.next_day AS breakout_date
    FROM windows w
    LEFT JOIN data d
           ON d.trading_day = w.contraction_end_date
    ORDER BY w.contraction_start_date, w.contraction_end_date;
    """

    cur = conn.cursor(dictionary=True)
    # cur.execute(sql, (SYMBOL, MIN_CONTRACTION_DAYS))
    cur.execute(sql, (SYMBOL,))
    rows = cur.fetchall()
    cur.close()
    return rows


# ---------------------------------------------
# FETCH DAILY OHLC
# ---------------------------------------------
def fetch_daily_ohlc(conn, SYMBOL, dt):
    sql = """
    SELECT open, high, low, close
    FROM market_data
    WHERE symbol=%s
      AND duration='day'
      AND start_timestamp=%s
      AND is_correct=1
    LIMIT 1;
    """
    cur = conn.cursor(dictionary=True)
    cur.execute(sql, (SYMBOL, dt))
    row = cur.fetchone()
    cur.close()
    return row


# ---------------------------------------------
# FETCH 5-MIN CANDLES
# ---------------------------------------------
def fetch_5min_candles(conn, SYMBOL, breakout_day):
    sql = """
    SELECT start_timestamp, open, high, low, close
    FROM market_data
    WHERE symbol=%s
      AND duration='5minute'
      AND DATE(start_timestamp)=DATE(%s)
      AND is_correct=1
    ORDER BY start_timestamp;
    """
    cur = conn.cursor(dictionary=True)
    cur.execute(sql, (SYMBOL, breakout_day))
    rows = cur.fetchall()
    cur.close()
    return rows


# ---------------------------------------------
# FIND EXIT PRICE
# ---------------------------------------------
def simulate_trade(side, entry_price, target, stoploss, candles, exit_strategy):

    # Initial trailing stop starts at the normal stoploss
    trailing_sl = stoploss  

    for c in candles:
        ts = c["start_timestamp"]
        t = ts.time()

        if t >= MAX_EXIT_TIME:
            return c["open"], ts, "TIME_EXIT"

        h = float(c["high"])
        l = float(c["low"])

        # ------------------------------------------------------
        # FIXED EXIT STRATEGY
        # ------------------------------------------------------
        if exit_strategy == 'fixed':
            if side == "BUY":
                if l <= stoploss:
                    return stoploss, ts, "STOPLOSS"
                if h >= target:
                    return target, ts, "TARGET"

            elif side == "SELL":
                if h >= stoploss:
                    return stoploss, ts, "STOPLOSS"
                if l <= target:
                    return target, ts, "TARGET"

        # ------------------------------------------------------
        # TRAILING STOP STRATEGY
        # ------------------------------------------------------
        elif exit_strategy == 'trailing':

            # ---- BUY SIDE TRAILING STOP ----
            if side == "BUY":

                # Move trailing SL upward as new highs form
                # Distance between entry and stoploss remains constant
                trail_distance = entry_price - stoploss
                new_sl = h - trail_distance

                # Keep SL only moving upward
                trailing_sl = max(trailing_sl, new_sl)

                # Check SL hit
                if l <= trailing_sl:
                    return trailing_sl, ts, "TRAIL_STOPLOSS"

                # Check target hit
                if h >= target:
                    return target, ts, "TARGET"

            # ---- SELL SIDE TRAILING STOP ----
            elif side == "SELL":

                # Distance between entry and stoploss remains constant
                trail_distance = stoploss - entry_price
                new_sl = l + trail_distance

                # Keep SL only moving downward
                trailing_sl = min(trailing_sl, new_sl)

                # Check SL hit
                if h >= trailing_sl:
                    return trailing_sl, ts, "TRAIL_STOPLOSS"

                # Check target hit
                if l <= target:
                    return target, ts, "TARGET"


    # ----------------------------------------------------------
    # Time based exit if none of the above conditions are met
    # ----------------------------------------------------------
    last = candles[-1]
    return last["close"], last["start_timestamp"], "TIME_EXIT"


def detect_breakout_entry(symbol, bd, upper, lower, candles):

    for idx, c in enumerate(candles):

        ts = c["start_timestamp"]
        candle_time = ts.time()

        if candle_time > MAX_EXIT_TIME:
            return None

        o = float(c["open"])
        h = float(c["high"])
        l = float(c["low"])
        close = float(c["close"])
        if close == 0 or o == 0 or h == 0 or l == 0:
            return None

        # ---- determine entry price rule ----
        is_first_candle = (candle_time == time(9, 15))

        # -------------------------
        # LONG breakout
        # -------------------------
        if h >= upper:

            if is_first_candle:
                entry_price = close
            else:
                entry_price = upper  # breakout level

            return {
                "symbol": symbol,
                "date": bd,
                "entry_ts": ts,
                "entry_price": entry_price,
                "side": BUY_SIDE_STR,
                "candles_after_entry": candles[idx + 1:]
            }

        # -------------------------
        # SHORT breakout
        # -------------------------
        if l <= lower:

            if is_first_candle:
                entry_price = close
            else:
                entry_price = lower  # breakout level

            return {
                "symbol": symbol,
                "date": bd,
                "entry_ts": ts,
                "entry_price": entry_price,
                "side": SELL_SIDE_STR,
                "candles_after_entry": candles[idx + 1:]
            }

    return None


# ---------------------------------------------
# CREATE / RESET backtest_results TABLE
# ---------------------------------------------
def reset_backtest_table(conn):

    cur = conn.cursor()

    # Drop table if exists
    cur.execute("DROP TABLE IF EXISTS backtest_results;")

    # Create fresh table
    create_sql = """
        CREATE TABLE backtest_results (
            id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
            symbol VARCHAR(32),
            trade_date DATE,
            entry_timestamp TIMESTAMP NULL,
            entry_price DOUBLE,
            side VARCHAR(8),
            target_pct DOUBLE,
            target_price DOUBLE,
            stoploss_pct DOUBLE,
            stoploss_price DOUBLE,
            exit_strategy VARCHAR(20),
            exit_timestamp TIMESTAMP NULL,
            exit_price DOUBLE,
            exit_reason VARCHAR(32),
            pnl DOUBLE,
            pnl_pct DOUBLE
        );
    """

    cur.execute(create_sql)
    conn.commit()
    cur.close()

    logger.info("backtest_results table recreated successfully.")


# ---------------------------------------------
# MAIN
# ---------------------------------------------
def main():

    conn = connect_mysql()
    reset_backtest_table(conn)

    # ----------------------------------------------------------------------
    # STEP 1: Precompute all entries (very efficient - no config here)
    # ----------------------------------------------------------------------
    for SYMBOL in SYMBOLS:

        all_entries = []  # precomputed trade entries

        logger.info(f"Preloading data for symbol: {SYMBOL}")

        contractions = fetch_contractions(conn, SYMBOL)
        logger.info("Found %d contractions for %s", len(contractions), SYMBOL)

        for c in contractions:

            ce = c["contraction_end_date"]
            bd = c["breakout_date"]
            if not bd:
                continue

            # fetch previous day's high/low
            daily = fetch_daily_ohlc(conn, SYMBOL, ce)
            if not daily:
                continue

            upper = float(daily["high"])
            lower = float(daily["low"])

            # fetch 5-minute candles for the breakout day
            candles = fetch_5min_candles(conn, SYMBOL, bd)
            if not candles or len(candles) < 75:
                continue

            # detect breakout only once
            entry = detect_breakout_entry(
                SYMBOL, bd, upper, lower, candles
            )
            if entry is not None:
                print("symbol: " + SYMBOL + ", entry_timestamp: " + str(entry['entry_ts']))

            if entry:
                all_entries.append(entry)

        logger.info("Precomputed %d valid breakout entries.", len(all_entries))

        # ----------------------------------------------------------------------
        # STEP 2: Apply all configs to the precomputed entries
        # ----------------------------------------------------------------------
        for cfg in configs:

            TARGET_PCT = cfg["target"] / 100
            STOPLOSS_PCT = cfg["stoploss"] / 100
            EXIT_STRATEGY = cfg["exit_strategy"]

            logger.info(f"Running CONFIG: {cfg}")

            for e in all_entries:

                entry_price = e["entry_price"]
                if entry_price == 0:
                    continue
                side = e["side"]

                if side == BUY_SIDE_STR:
                    target = entry_price * (1 + TARGET_PCT)
                    stoploss = entry_price * (1 - STOPLOSS_PCT)
                else:
                    target = entry_price * (1 - TARGET_PCT)
                    stoploss = entry_price * (1 + STOPLOSS_PCT)

                # print("entry_price: " + str(entry_price) + ", TARGET_PCT: " + str(TARGET_PCT) + ", target: " + str(target) + ", STOPLOSS_PCT: " + str(STOPLOSS_PCT) + ", stoploss: " + str(stoploss))

                exit_price, exit_ts, reason = simulate_trade(
                    side,
                    entry_price,
                    target,
                    stoploss,
                    e["candles_after_entry"],
                    EXIT_STRATEGY
                )

                pnl = exit_price - entry_price if side == BUY_SIDE_STR else entry_price - exit_price
                pnl_pct = pnl / entry_price * 100

                t = {
                    "symbol": e["symbol"],
                    "side": side,
                    "pos_ts": e["entry_ts"],
                    "pos_price": entry_price,
                    "exit_ts": exit_ts,
                    "exit_price": exit_price,
                    "pnl": pnl,
                    "pnl_pct": pnl_pct,
                    "reason": reason,
                    "date": e["date"]
                }

                store_trade_result(conn, t, TARGET_PCT, STOPLOSS_PCT, EXIT_STRATEGY)

    conn.close()


if __name__ == "__main__":
    main()
