import time
from datetime import datetime, timedelta, time as dtime
from zoneinfo import ZoneInfo  # Python 3.9+
import logging

# ensure logger exists
logger = logging.getLogger(__name__)

# global symbol DF map (you already asked to keep it global)
symbol_df_map = {}

# ---------- Placeholder / helper functions you must implement ----------
# These functions are intentionally left minimal and must be filled with your real logic.
def fetch_full_day_data_for_symbol(symbol, data_fetching_start_timestamp_str, today_start_timestamp_str, conn):
    """
    Fetch full-day data between the timestamps and return a DF (same format as load_symbol_data_map).
    Should return a DataFrame indexed by start_timestamp with columns: open, high, low, close, volume, ema9, ema21, date.
    """
    # Reuse the same SQL pattern as in load_symbol_data_map but for a single symbol.
    query = """
        SELECT start_timestamp, open, high, low, close, volume
        FROM market_data
        WHERE symbol = %s
          AND duration = '5minute'
          AND start_timestamp BETWEEN %s AND %s
        ORDER BY start_timestamp
    """
    try:
        import pandas as pd
        df = pd.read_sql(query, conn, params=(symbol, data_fetching_start_timestamp_str, today_start_timestamp_str),
                         index_col='start_timestamp', parse_dates=['start_timestamp'])
        if df.empty:
            return df
        df = df[['open', 'high', 'low', 'close', 'volume']]
        df['ema9'] = df['close'].ewm(span=9, adjust=False).mean()
        df['ema21'] = df['close'].ewm(span=21, adjust=False).mean()
        df['date'] = df.index.date
        return df
    except Exception:
        logger.exception("Error fetching full day data for symbol: %s", symbol)
        return None

def check_eligibility_on_partial_candle(symbol, df):
    """
    Decide whether symbol is eligible based on the partial (current) candle.
    Return True/False.
    Implement your strategy checks here using df (which contains up-to-date rows).
    """
    # Placeholder: you should implement your real checks.
    # Example quick check: latest ema9 > ema21
    try:
        if df is None or df.empty:
            return False
        last = df.iloc[-1]
        return last['ema9'] > last['ema21']
    except Exception:
        logger.exception("Error while checking eligibility for %s", symbol)
        return False

def run_super_batch_and_take_positions(super_batch_symbols, conn, cursor):
    """
    Run the super batch (process symbols sequentially) and take positions if eligible.
    Must place orders (via your broker API) and set stoploss/target.
    This is intentionally sequential (no multithreading).
    Returns a list of symbols for which positions were actually taken.
    """
    taken = []
    for sym in super_batch_symbols:
        try:
            # --- placeholder decision ---
            # e.g., place_market_order(sym, size)
            # then place stoploss and target orders
            logger.info("Taking position for %s (placeholder)", sym)
            # record that we took a position for bookkeeping (replace with real order ids / DB update)
            taken.append(sym)
        except Exception:
            logger.exception("Failed taking position for %s", sym)
    return taken

def fetch_order_history_and_handle_executions(conn, cursor):
    """
    Fetch current order/execution status from broker/db and:
      - if a stoploss executed, cancel target order
      - if a target executed, cancel stoploss
    This is broker specific; implement using your broker/order DB.
    """
    # placeholder: implement actual logic
    logger.debug("Checking order history and handling cross cancels (placeholder)")

def exit_all_non_exited_positions(conn, cursor):
    """
    Send exit orders for any currently open positions and cancel any pending orders.
    Returns list of positions exited.
    """
    # placeholder: implement actual logic
    logger.info("Exiting all non-exited positions (placeholder)")

def fetch_current_positions_count(conn, cursor):
    """
    Return integer count of open positions.
    """
    # placeholder: query your positions DB or broker
    return 0

def cancel_all_non_cancelled_orders(conn, cursor):
    """
    Cancel all pending orders.
    """
    logger.info("Cancelling all non-cancelled orders (placeholder)")

# ---------- End placeholder helpers ----------


def now_ist():
    """Return current datetime in Asia/Kolkata timezone."""
    return datetime.now(ZoneInfo("Asia/Kolkata"))


def time_in_bucket(dt: datetime):
    """
    Return the 5-minute bucket string for a datetime, e.g. '2025-11-06-09:25'
    Use this to ensure a super-batch runs at most once per 5-min bucket.
    """
    # floor minutes to nearest multiple of 5
    minute = (dt.minute // 5) * 5
    bucket = datetime(dt.year, dt.month, dt.day, dt.hour, minute, tzinfo=dt.tzinfo)
    return bucket.strftime("%Y-%m-%d %H:%M")


def main(argv):
    # parse args (reuse your existing parsing if needed)
    run_option = "test"
    today_date_str = None
    try:
        opts, args = getopt.getopt(argv, "o:d:")
    except Exception:
        print('mahabali_action_1.py -o <option> -d <date>')
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-o':
            run_option = arg
        elif opt == '-d':
            today_date_str = arg

    if run_option != "test" and run_option != "real":
        print('mahabali_action_1.py -o <real> -d <date>')
        sys.exit(2)

    if run_option == "test" and today_date_str is None:
        print('mahabali_action_1.py -o <IF NOT "real"> -d <MUST BE PROVIDED>')
        sys.exit(2)

    conn = connect.mysql_connection()
    cursor = conn.cursor()

    # compute timestamps (use date objects consistently)
    if run_option == "real":
        today_start_dt = now_ist().replace(hour=0, minute=0, second=0, microsecond=0)
    else:
        today_start_dt = datetime.strptime(today_date_str, "%Y-%m-%d")
    data_fetching_start_dt = today_start_dt - timedelta(days=no_of_days_correct_data_required)

    today_start_timestamp_str = today_start_dt.strftime("%Y-%m-%d %H:%M:%S")
    data_fetching_start_timestamp_str = data_fetching_start_dt.strftime("%Y-%m-%d %H:%M:%S")
    logger.info("today_start_timestamp: " + today_start_timestamp_str)
    logger.info("data_fetching_start_date: " + data_fetching_start_timestamp_str)

    # 1) select symbols
    selected_stock_list_array = get_selected_stocks(run_option, today_date_str,
                                                   data_fetching_start_timestamp_str,
                                                   today_start_timestamp_str,
                                                   conn, cursor)
    logger.info("selected_stock_list_array: %s", selected_stock_list_array)

    # 2) load dataframes into global symbol_df_map
    load_symbol_data_map(selected_stock_list_array, data_fetching_start_timestamp_str,
                         today_start_timestamp_str, conn, cursor)

    # bookkeeping: track which 5-min buckets we've run super-batch for
    executed_buckets = set()

    # super-batch candidate symbols (set)
    super_batch_candidates = set()

    # run loop until after market close logic finishes
    logger.info("Entering main market loop (09:15 -> 15:15 logic).")
    market_open_time = dtime(hour=9, minute=15)
    pre_main_end = dtime(hour=9, minute=20)
    main_end_time = dtime(hour=15, minute=15)
    final_exit_time = dtime(hour=15, minute=30)  # optional final cleanup after 15:15

    try:
        while True:
            now = now_ist()
            t = now.time()

            # ------------- Early window 09:15 -> 09:20 (tight sleep) -------------
            if market_open_time <= t <= pre_main_end:
                # do minimal activity, allow time for market data to arrive
                logger.debug("Early window: %s", now.strftime("%Y-%m-%d %H:%M:%S"))
                time.sleep(3)
                continue

            # ------------- Main trading window 09:20 -> 15:15 -------------
            if dtime(hour=9, minute=20) < t <= main_end_time:
                # refresh small bit of data for symbols if needed
                # update symbol_df_map for symbols that we will evaluate in this bucket
                bucket = time_in_bucket(now)

                # fetch current positions count
                no_of_positions = fetch_current_positions_count(conn, cursor)

                # If we have capacity to take more positions, prepare a batch of symbols to evaluate
                if no_of_positions < 10:
                    # We'll iterate over the selected symbols and check eligibility on the partial candle
                    # Build a candidate list (super_batch_candidates) for this bucket
                    logger.info("Preparing candidate list for super-batch in bucket %s", bucket)
                    super_batch_candidates.clear()

                    for symbol in selected_stock_list_array:
                        # update/refresh the symbol df with latest partial-day rows
                        try:
                            df = fetch_full_day_data_for_symbol(symbol, data_fetching_start_timestamp_str,
                                                                today_start_timestamp_str, conn)
                            if df is None or df.empty:
                                # skip symbol if no data
                                continue
                            symbol_df_map[symbol] = df  # update global map
                        except Exception:
                            logger.exception("Error updating df for %s", symbol)
                            continue

                        # check eligibility on partial candle
                        try:
                            if check_eligibility_on_partial_candle(symbol, symbol_df_map[symbol]):
                                super_batch_candidates.add(symbol)
                        except Exception:
                            logger.exception("Eligibility check failed for %s", symbol)

                    logger.info("Super-batch candidates for bucket %s: %s", bucket, list(super_batch_candidates)[:50])

                    # Decide whether to run super-batch for this bucket
                    if bucket not in executed_buckets and super_batch_candidates:
                        logger.info("Running super-batch for bucket %s with %d candidates", bucket, len(super_batch_candidates))

                        # Run the super-batch sequentially and take positions
                        taken = run_super_batch_and_take_positions(list(super_batch_candidates), conn, cursor)

                        # mark bucket executed regardless (so it only runs once)
                        executed_buckets.add(bucket)

                        # optionally record which symbols got positions
                        logger.info("Positions taken for: %s", taken)
                    else:
                        logger.info("Skipping super-batch for bucket %s (already executed or no candidates).", bucket)

                # After that, always check order history to reconcile stop/target executions
                try:
                    fetch_order_history_and_handle_executions(conn, cursor)
                except Exception:
                    logger.exception("Error checking order history")

                # Sleep small amount to avoid busy loop — could be tuned (e.g., 5–15s)
                time.sleep(5)
                continue

            # ------------- After main_end_time ( >15:15 ) -------------
            if t > main_end_time:
                logger.info("After %s: exiting all non-exited positions and cancelling remaining orders.", main_end_time.strftime("%H:%M"))
                try:
                    exit_all_non_exited_positions(conn, cursor)
                    cancel_all_non_cancelled_orders(conn, cursor)
                except Exception:
                    logger.exception("Error during final exit/cleanup")

                # optional additional wait to ensure orders are processed
                time.sleep(1)
                logger.info("Final cleanup done. Exiting main loop.")
                break

            # ------------- Otherwise sleep (outside market hours) -------------
            logger.debug("Outside trading windows: %s", now.strftime("%Y-%m-%d %H:%M:%S"))
            time.sleep(30)

    except KeyboardInterrupt:
        logger.warning("Received KeyboardInterrupt — doing best-effort cleanup.")
        try:
            exit_all_non_exited_positions(conn, cursor)
            cancel_all_non_cancelled_orders(conn, cursor)
        except Exception:
            logger.exception("Cleanup failed on interrupt")

    finally:
        try:
            cursor.close()
            conn.close()
        except Exception:
            logger.exception("Error closing DB connection")
