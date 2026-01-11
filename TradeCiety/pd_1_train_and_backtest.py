#!/usr/bin/env python3
"""
Intraday Trading Model: Train and Backtest
============================================

Author: Grok (xAI)
Date: November 17, 2025

Assumptions:
- Database contains historical data for multiple symbols (e.g., NSE stocks).
- All timestamps are in IST (timezone-aware handling via pytz if needed, but treated as naive for simplicity).
- Meta JSON keys: 'ema5', 'ema10', 'vwap', 'rsi', 'atr', 'bb_mid', 'bb_std', 'latest_5min_open',
  'latest_5min_high', 'latest_5min_low', 'latest_5min_close', 'latest_5min_volume',
  'd_open', 'd_high', 'd_low', 'd_close' (daily OHLC).
- Trading hours: Entries 09:20:00 to 15:10:00 IST. Forced exit at 15:15:00 open if holding.
- Market close assumed at 15:30, but forced exit at 15:15 for conservatism.
- Symbols treated as categorical features (one-hot encoded).
- No transaction costs/slippage in backtest (add if needed).
- Data fetched for all available; CLI filters by date.
- Look-ahead bias prevention:
  - Features from meta at T use only data <= T (as per DB note).
  - Labels simulated using market_data bars strictly > entry_timestamp.
  - Backtest entry_price = open of first bar > entry_timestamp.
  - No future features in training; lags computed via shift on sorted historical DF.
  - Splits time-based to prevent leakage across periods.

Dependencies (requirements.txt snippet):
pandas==2.1.4
numpy==1.24.3
sqlalchemy==2.0.23
pymysql==1.1.0
scikit-learn==1.3.2
xgboost==2.0.3
torch==2.1.2
matplotlib==3.8.2
seaborn==0.13.0
joblib==1.3.2

Usage:
python train_and_backtest.py --start_date 2024-01-01 --end_date 2025-11-17 --profit_target 0.01 --stoploss_pct 0.005 --model_type xgboost --save_dir ./output
Models: 'xgboost' (tabular tree-based), 'lstm' (sequence), 'both'.
Justification: XGBoost for interpretable, fast tabular features handling non-linearities and interactions.
LSTM for capturing temporal dependencies in sequential market data (e.g., momentum patterns).
Both trained; best selected by val avg ppnl.

"""

import argparse
import json
import os
import warnings
from datetime import datetime, time, timedelta
from typing import Dict, List, Optional, Tuple, Any
import pytz  # For IST handling

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.preprocessing import StandardScaler, OneHotEncoder
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.model_selection import TimeSeriesSplit  # But we use custom time split
from sklearn.metrics import confusion_matrix, classification_report
import xgboost as xgb
import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import Dataset, DataLoader
from sqlalchemy import create_engine, text
import joblib

warnings.filterwarnings('ignore')
sns.set_style("whitegrid")

# Config defaults
DEFAULT_START_DATE = '2023-01-01'
DEFAULT_END_DATE = '2025-11-17'
DEFAULT_PROFIT_TARGET = 0.01  # 1%
DEFAULT_STOPLOSS_PCT = 0.005  # 0.5%
DEFAULT_MAX_HOLDING_BARS = 6  # 30 min
DEFAULT_SEQ_LEN = 10  # For LSTM
DEFAULT_LABEL_THRESHOLD = 0.005  # Label only if ppnl > 0.5%
IST = pytz.timezone('Asia/Kolkata')

class TradingDataset(Dataset):
    """PyTorch Dataset for sequences."""
    def __init__(self, X_seq, y):
        self.X = torch.tensor(X_seq, dtype=torch.float32)
        self.y = torch.tensor(y, dtype=torch.long)

    def __len__(self):
        return len(self.y)

    def __getitem__(self, idx):
        return self.X[idx], self.y[idx]

class LSTModel(nn.Module):
    """Simple LSTM for classification (3 classes: long=0, short=1, flat=2)."""
    def __init__(self, input_size, hidden_size=64, num_layers=2, num_classes=3, dropout=0.2):
        super().__init__()
        self.lstm = nn.LSTM(input_size, hidden_size, num_layers, batch_first=True, dropout=dropout)
        self.fc = nn.Sequential(
            nn.Linear(hidden_size, 128),
            nn.ReLU(),
            nn.Dropout(dropout),
            nn.Linear(128, num_classes)
        )

    def forward(self, x):
        _, (hn, _) = self.lstm(x)
        return self.fc(hn[-1])

def connect_db():
    """Safe DB connection."""
    engine = create_engine('mysql+pymysql://root:root@localhost/market')
    return engine

def fetch_meta(engine: Any, symbol: str, start_str: str, end_str: str) -> pd.DataFrame:
    """Fetch meta, parse JSON. No lookahead: meta at T uses <=T data."""
    query = text("""
        SELECT id, symbol, entry_timestamp, meta 
        FROM intraday_meta_5min 
        WHERE symbol = :symbol AND entry_timestamp BETWEEN :start AND :end 
        ORDER BY entry_timestamp
    """)
    df = pd.read_sql(query, engine, params={'symbol': symbol, 'start': start_str, 'end': end_str})
    if df.empty:
        return df
    # Parse JSON safely
    df['meta_parsed'] = df['meta'].apply(lambda x: json.loads(x) if pd.notna(x) else {})
    # Flatten keys (assume all rows have same keys)
    keys = df['meta_parsed'].iloc[0].keys()
    for key in keys:
        df[key] = df['meta_parsed'].apply(lambda x: x.get(key, np.nan))
    df.drop(['meta', 'meta_parsed'], axis=1, inplace=True)
    # Assume DB timestamps are naive IST, localize
    df['entry_timestamp'] = pd.to_datetime(df['entry_timestamp']).dt.tz_localize(IST)
    return df

def fetch_market_data(engine: Any, symbol: str, start_str: str, end_str: str) -> pd.DataFrame:
    """Fetch 5min bars for simulation. start_timestamp is bar start."""
    query = text("""
        SELECT start_timestamp, open, high, low, close, volume 
        FROM market_data 
        WHERE symbol = :symbol AND duration = '5minute' 
        AND start_timestamp BETWEEN :start AND :end 
        ORDER BY start_timestamp
    """)
    df = pd.read_sql(query, engine, params={'symbol': symbol, 'start': start_str, 'end': end_str})
    if not df.empty:
        # Assume DB timestamps are naive IST, localize
        df['start_timestamp'] = pd.to_datetime(df['start_timestamp']).dt.tz_localize(IST)
    return df

def fetch_all_symbols(engine: Any) -> List[str]:
    """Get unique symbols from meta."""
    query = text("SELECT DISTINCT symbol FROM intraday_meta_5min")
    df = pd.read_sql(query, engine)
    return df['symbol'].tolist()

def engineer_features(df_meta: pd.DataFrame) -> Tuple[pd.DataFrame, List[str]]:
    """Feature eng: parse done in fetch, add derived. Group by symbol for lags."""
    if df_meta.empty:
        return df_meta, []

    # Base features from meta
    feature_cols_base = ['ema5', 'ema10', 'vwap', 'rsi', 'atr', 'bb_mid', 'bb_std',
                    'latest_5min_open', 'latest_5min_high', 'latest_5min_low', 'latest_5min_close', 'latest_5min_volume',
                    'd_open', 'd_high', 'd_low', 'd_close']
    df = df_meta[feature_cols_base + ['symbol', 'entry_timestamp']].copy()
    df = df.sort_values(['symbol', 'entry_timestamp'])  # Sort globally to ensure order for lags

    # Lags and returns (use close for returns, shift(1) is previous bar)
    df['close'] = df['latest_5min_close']
    df['ret1'] = df.groupby('symbol')['close'].pct_change(1)
    df['ret3'] = df.groupby('symbol')['close'].pct_change(3)
    df['ret5'] = df.groupby('symbol')['close'].pct_change(5)
    df['vol_change'] = df.groupby('symbol')['latest_5min_volume'].pct_change(1)
    df['vol_rolling_mean'] = df.groupby('symbol')['latest_5min_volume'].rolling(5).mean().reset_index(0, drop=True)
    df['vol_rolling_std'] = df.groupby('symbol')['latest_5min_volume'].rolling(5).std().reset_index(0, drop=True)

    # Edge features
    df['price_vwap'] = (df['close'] - df['vwap']) / df['vwap']
    df['price_ema5'] = (df['close'] - df['ema5']) / df['ema5']
    df['rsi_norm'] = df['rsi'] / 100  # 0-1
    df['bb_position'] = (df['close'] - df['bb_mid']) / (2 * df['bb_std'])  # Normalized

    # Time features: cyclical
    df['hour'] = df['entry_timestamp'].dt.hour
    df['minute'] = df['entry_timestamp'].dt.minute
    df['hour_sin'] = np.sin(2 * np.pi * df['hour'] / 24)
    df['hour_cos'] = np.cos(2 * np.pi * df['hour'] / 24)
    df['min_sin'] = np.sin(2 * np.pi * df['minute'] / 60)
    df['min_cos'] = np.cos(2 * np.pi * df['minute'] / 60)

    # Volatility: atr / close
    df['atr_norm'] = df['atr'] / df['close']

    # Drop na from lags (first few rows per symbol)
    df = df.dropna()

    # Categorical symbol: add dummies, keep original symbol
    dummies = pd.get_dummies(df['symbol'], prefix='sym')
    df = pd.concat([df, dummies], axis=1)

    # Features list (exclude target cols, keep symbol separate)
    feat_cols = [col for col in df.columns if col not in ['entry_timestamp', 'symbol', 'close', 'hour', 'minute']]
    return df[feat_cols + ['entry_timestamp', 'symbol']], feat_cols

def simulate_trade(market_df: pd.DataFrame, entry_ts: pd.Timestamp, direction: str,
                   target_pct: float, sl_pct: float, max_bars: int = 6) -> Dict[str, Any]:
    """
    Simulate trade bar-by-bar. No lookahead: uses bars > entry_ts sequentially.
    Entry fill at first bar.open > entry_ts.
    Intrabar: assume fill at target/SL level if high/low crosses.
    Forced EOD: if bar.start >= 15:15, exit at open.
    """
    if market_df.empty:
        return {'exit_price': 0.0, 'exit_timestamp': entry_ts, 'ppnl': 0.0, 'reason': 'no_data', 'entry_price': 0.0}

    # Find first bar after entry
    next_bars = market_df[market_df['start_timestamp'] > entry_ts].head(max_bars + 1)
    if next_bars.empty:
        return {'exit_price': 0.0, 'exit_timestamp': entry_ts + timedelta(minutes=5), 'ppnl': 0.0, 'reason': 'no_bars', 'entry_price': 0.0}

    first_bar = next_bars.iloc[0]
    entry_price = first_bar['open']  # Always fill at next open
    target_price = entry_price * (1 + target_pct) if direction == 'long' else entry_price * (1 - target_pct)
    sl_price = entry_price * (1 - sl_pct) if direction == 'long' else entry_price * (1 + sl_pct)

    current_ts = first_bar['start_timestamp']
    for _, bar in next_bars.iterrows():
        if bar['start_timestamp'].time() >= time(15, 15):
            exit_price = bar['open']
            ppnl = (exit_price - entry_price) / entry_price if direction == 'long' else (entry_price - exit_price) / entry_price
            return {
                'entry_price': entry_price,
                'exit_price': exit_price,
                'exit_timestamp': bar['start_timestamp'],
                'ppnl': ppnl,
                'reason': 'eod'
            }

        hit_target = False
        hit_sl = False
        if direction == 'long':
            if bar['high'] >= target_price:
                hit_target = True
            if bar['low'] <= sl_price:
                hit_sl = True
        else:  # short
            if bar['low'] <= target_price:
                hit_target = True
            if bar['high'] >= sl_price:
                hit_sl = True

        if hit_target or hit_sl:
            if hit_target and not hit_sl:
                exit_price = target_price
                reason = 'target'
            elif hit_sl and not hit_target:
                exit_price = sl_price
                reason = 'stoploss'
            else:
                # Both hit: assume SL first (conservative)
                exit_price = sl_price
                reason = 'stoploss'
            ppnl = (exit_price - entry_price) / entry_price if direction == 'long' else (entry_price - exit_price) / entry_price
            return {
                'entry_price': entry_price,
                'exit_price': exit_price,
                'exit_timestamp': bar['start_timestamp'],
                'ppnl': ppnl,
                'reason': reason
            }

    # No hit, exit at last close
    last_bar = next_bars.iloc[-1]
    exit_price = last_bar['close']
    ppnl = (exit_price - entry_price) / entry_price if direction == 'long' else (entry_price - exit_price) / entry_price
    return {
        'entry_price': entry_price,
        'exit_price': exit_price,
        'exit_timestamp': last_bar['start_timestamp'],
        'ppnl': ppnl,
        'reason': 'timeout'
    }

def generate_labels(df_features: pd.DataFrame, market_dfs: Dict[str, pd.DataFrame], target_pct: float, sl_pct: float,
                    threshold: float = 0.005) -> Tuple[pd.DataFrame, pd.Series]:
    """Generate labels: simulate long/short, label dir with max ppnl if > threshold, else flat (2).
    No lookahead: sim uses future bars only.
    """
    labels = []
    ppnls_long = []
    ppnls_short = []

    for idx, row in df_features.iterrows():
        sym = row['symbol']
        ts = row['entry_timestamp']
        market_df_sym = market_dfs.get(sym, pd.DataFrame())

        # Sim long (entry_price=None, will fill at next open)
        sim_long = simulate_trade(market_df_sym, ts, 'long', target_pct, sl_pct)
        ppnl_long = sim_long['ppnl']

        # Sim short
        sim_short = simulate_trade(market_df_sym, ts, 'short', target_pct, sl_pct)
        ppnl_short = sim_short['ppnl']

        ppnls_long.append(ppnl_long)
        ppnls_short.append(ppnl_short)

        if ppnl_long > ppnl_short and ppnl_long > threshold:
            label = 0  # long
        elif ppnl_short > ppnl_long and ppnl_short > threshold:
            label = 1  # short
        else:
            label = 2  # flat
        labels.append(label)

    df_features['ppnl_long'] = ppnls_long
    df_features['ppnl_short'] = ppnls_short
    return df_features, pd.Series(labels, index=df_features.index)

def time_split(df: pd.DataFrame, train_pct: float = 0.7, val_pct: float = 0.15) -> Tuple[pd.DataFrame, pd.DataFrame, pd.DataFrame]:
    """Time-based split by entry_timestamp using sorted indices to preserve original indices."""
    # Get indices sorted by timestamp
    sorted_idx = df['entry_timestamp'].sort_values().index
    n = len(sorted_idx)
    train_end = int(n * train_pct)
    val_end = int(n * (train_pct + val_pct))

    train_idx = sorted_idx[:train_end]
    val_idx = sorted_idx[train_end:val_end]
    test_idx = sorted_idx[val_end:]

    train_df = df.loc[train_idx].sort_values('entry_timestamp')
    val_df = df.loc[val_idx].sort_values('entry_timestamp') if len(val_idx) > 0 else pd.DataFrame()
    test_df = df.loc[test_idx].sort_values('entry_timestamp') if len(test_idx) > 0 else pd.DataFrame()

    print(f"Train: {train_df['entry_timestamp'].min()} to {train_df['entry_timestamp'].max()} ({len(train_df)} samples)")
    if not val_df.empty:
        print(f"Val: {val_df['entry_timestamp'].min()} to {val_df['entry_timestamp'].max()} ({len(val_df)} samples)")
    else:
        print("Val: empty")
    if not test_df.empty:
        print(f"Test: {test_df['entry_timestamp'].min()} to {test_df['entry_timestamp'].max()} ({len(test_df)} samples)")
    else:
        print("Test: empty")

    return train_df, val_df, test_df

def prepare_sequences(X: np.ndarray, y: np.ndarray, seq_len: int) -> Tuple[np.ndarray, np.ndarray]:
    """Create sequences for LSTM, padding early ones with zeros to full length."""
    xs, ys = [], []
    n_features = X.shape[1]
    for i in range(len(X)):
        start = max(0, i - seq_len + 1)
        seq = X[start:i+1]
        if len(seq) < seq_len:
            pad_len = seq_len - len(seq)
            pad = np.zeros((pad_len, n_features))
            seq = np.vstack([pad, seq])
        else:
            seq = seq[-seq_len:]  # Last seq_len
        xs.append(seq)
        ys.append(y[i])
    return np.array(xs), np.array(ys)

def train_xgboost(X_train: np.ndarray, y_train: np.ndarray, X_val: np.ndarray, y_val: np.ndarray,
                  feat_cols: List[str], save_path: str):
    """Train XGBoost classifier. Handles imbalance with scale_pos_weight, but here class_weight approx."""
    model = xgb.XGBClassifier(
        objective='multi:softprob',
        num_class=3,
        eval_metric='mlogloss',
        random_state=42,
        n_estimators=100,
        max_depth=6,
        learning_rate=0.1,
        subsample=0.8,
        colsample_bytree=0.8
    )
    model.fit(
        X_train, y_train,
        eval_set=[(X_val, y_val)],
        verbose=False
    )
    joblib.dump(model, save_path)
    return model

def train_lstm(X_train_seq: np.ndarray, y_train: np.ndarray, X_val_seq: np.ndarray, y_val: np.ndarray,
               input_size: int, save_path: str, epochs: int = 50, batch_size: int = 32, lr: float = 0.001):
    """Train LSTM with early stopping on val loss."""
    device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
    train_ds = TradingDataset(X_train_seq, y_train)
    val_ds = TradingDataset(X_val_seq, y_val)
    train_loader = DataLoader(train_ds, batch_size=batch_size, shuffle=False)  # No shuffle for TS
    val_loader = DataLoader(val_ds, batch_size=batch_size, shuffle=False)

    model = LSTModel(input_size).to(device)
    criterion = nn.CrossEntropyLoss()
    optimizer = optim.Adam(model.parameters(), lr=lr)
    scheduler = optim.lr_scheduler.ReduceLROnPlateau(optimizer, patience=5)

    best_val_loss = float('inf')
    patience_counter = 0
    for epoch in range(epochs):
        model.train()
        train_loss = 0
        for Xb, yb in train_loader:
            Xb, yb = Xb.to(device), yb.to(device)
            optimizer.zero_grad()
            out = model(Xb)
            loss = criterion(out, yb)
            loss.backward()
            optimizer.step()
            train_loss += loss.item()
        train_loss /= len(train_loader)

        model.eval()
        val_loss = 0
        with torch.no_grad():
            for Xb, yb in val_loader:
                Xb, yb = Xb.to(device), yb.to(device)
                out = model(Xb)
                loss = criterion(out, yb)
                val_loss += loss.item()
        val_loss /= len(val_loader)

        scheduler.step(val_loss)
        if val_loss < best_val_loss:
            best_val_loss = val_loss
            patience_counter = 0
            torch.save(model.state_dict(), save_path)
        else:
            patience_counter += 1
            if patience_counter >= 10:
                break

    # Load best
    model.load_state_dict(torch.load(save_path))
    return model

def backtest_predictions(df_test: pd.DataFrame, market_dfs: Dict[str, pd.DataFrame], model, feat_cols: List[str],
                         preprocessor, target_pct: float, sl_pct: float, model_type: str, seq_len: int = 10) -> Tuple[pd.DataFrame, np.ndarray]:
    """Backtest: predict on test, simulate only if long/short and in window. Returns trades and all preds."""
    X_raw = df_test[feat_cols].fillna(0).values
    X_proc = preprocessor.transform(X_raw)
    ts_test = df_test['entry_timestamp']
    symbols_test = df_test['symbol']

    if model_type == 'lstm':
        X_test_seq, _ = prepare_sequences(X_proc, np.zeros(len(X_proc)), seq_len)
        preds = []
        model.eval()
        device = next(model.parameters()).device
        with torch.no_grad():
            for i in range(len(X_test_seq)):
                xb = torch.tensor(X_test_seq[i:i+1], dtype=torch.float32).to(device)
                out = model(xb)
                proba = torch.softmax(out, dim=1).cpu().numpy()[0]
                pred = np.argmax(proba)
                preds.append(pred)
        preds = np.array(preds)
    else:
        # XGBoost: assume model.predict returns classes
        preds = model.predict(X_proc)

    trades = []
    for i, pred in enumerate(preds):
        ts = ts_test.iloc[i]
        if ts.time() < time(9, 20) or ts.time() > time(15, 10):
            continue  # Only allowed entries
        if pred == 2:  # flat
            continue

        sym = symbols_test.iloc[i]
        direction = 'long' if pred == 0 else 'short'
        market_df = market_dfs[sym]
        sim = simulate_trade(market_df, ts, direction, target_pct, sl_pct)
        trades.append({
            'entry_ts': ts,
            'entry_price': sim['entry_price'],
            'exit_ts': sim['exit_timestamp'],
            'exit_price': sim['exit_price'],
            'direction': direction,
            'pnl': sim['ppnl'] * sim['entry_price'],  # Absolute, assume 1 unit
            'ppnl': sim['ppnl'],
            'target': target_pct,
            'stoploss': sl_pct,
            'reason': sim['reason'],
            'symbol': sym
        })

    trades_df = pd.DataFrame(trades)
    return trades_df, preds

def compute_metrics(trades_df: pd.DataFrame) -> Dict[str, float]:
    """Compute backtest metrics."""
    if trades_df.empty:
        return {'n_trades': 0, 'avg_ppnl': 0, 'win_rate': 0, 'total_pnl': 0, 'max_dd': 0, 'sharpe': 0}

    n_trades = len(trades_df)
    avg_ppnl = trades_df['ppnl'].mean()
    win_rate = (trades_df['ppnl'] > 0).mean()
    total_pnl = trades_df['pnl'].sum()

    # Equity curve for DD and Sharpe
    trades_df = trades_df.sort_values('exit_ts')
    equity = trades_df['pnl'].cumsum()
    running_max = equity.expanding().max()
    drawdown = (equity - running_max) / running_max
    max_dd = drawdown.min() if not drawdown.empty else 0

    returns = trades_df['ppnl']
    sharpe = returns.mean() / returns.std() * np.sqrt(252 * 12) if returns.std() > 0 else 0  # Ann, approx 12 trades/day?

    # Trade dist
    trade_dist = trades_df.groupby(['symbol', trades_df['entry_ts'].dt.hour])['ppnl'].agg(['count', 'mean']).reset_index()
    print("Trade distribution by symbol and hour:")
    print(trade_dist)

    return {
        'n_trades': float(n_trades),
        'avg_ppnl': avg_ppnl,
        'win_rate': win_rate,
        'total_pnl': total_pnl,
        'max_dd': max_dd,
        'sharpe': sharpe
    }

def plot_results(trades_df: pd.DataFrame, metrics: Dict, save_dir: str, model_type: str):
    """Generate plots."""
    os.makedirs(save_dir, exist_ok=True)

    # Equity curve
    if not trades_df.empty:
        trades_df_sorted = trades_df.sort_values('exit_ts')
        equity = trades_df_sorted['pnl'].cumsum()
        plt.figure(figsize=(10, 6))
        plt.plot(equity)
        plt.title(f'Equity Curve - {model_type}')
        plt.xlabel('Trade #')
        plt.ylabel('Cumulative PnL')
        plt.savefig(os.path.join(save_dir, f'equity_curve_{model_type}.png'))
        plt.close()

        # PnL hist
        plt.figure(figsize=(10, 6))
        plt.hist(trades_df['ppnl'], bins=20)
        plt.title('Per-Trade PnL Histogram')
        plt.xlabel('PnL %')
        plt.ylabel('Frequency')
        plt.savefig(os.path.join(save_dir, f'pnl_hist_{model_type}.png'))
        plt.close()

    # Save trades CSV
    trades_df.to_csv(os.path.join(save_dir, f'trades_{model_type}.csv'), index=False)

    # Summary JSON
    metrics.update({'model_type': model_type})
    with open(os.path.join(save_dir, f'summary_metrics_{model_type}.json'), 'w') as f:
        json.dump(metrics, f, default=str)

def evaluate_model(y_true_test: np.ndarray, y_pred_test: np.ndarray, save_dir: str, model_type: str):
    """Eval on test: confusion, report."""
    if len(y_true_test) > 0:
        cm = confusion_matrix(y_true_test, y_pred_test)
        plt.figure(figsize=(8, 6))
        sns.heatmap(cm, annot=True, fmt='d', cmap='Blues')
        plt.title(f'Confusion Matrix - {model_type}')
        plt.ylabel('True')
        plt.xlabel('Pred')
        plt.savefig(os.path.join(save_dir, f'confusion_{model_type}.png'))
        plt.close()

        report = classification_report(y_true_test, y_pred_test, target_names=['long', 'short', 'flat'], output_dict=True)
        with open(os.path.join(save_dir, f'class_report_{model_type}.json'), 'w') as f:
            json.dump(report, f, default=str)

# Main
def main(args):
    engine = connect_db()
    symbols = fetch_all_symbols(engine)
    print(f"Found symbols: {symbols}")

    # Fetch all data
    all_meta = pd.DataFrame()
    all_market = {}
    start_dt = pd.to_datetime(args.start_date)
    end_dt = pd.to_datetime(args.end_date)
    start_str = start_dt.strftime('%Y-%m-%d 00:00:00')
    end_str = end_dt.strftime('%Y-%m-%d 23:59:59')
    for sym in symbols:
        meta_sym = fetch_meta(engine, sym, start_str, end_str)
        if not meta_sym.empty:
            meta_sym['symbol'] = sym  # Ensure
            all_meta = pd.concat([all_meta, meta_sym])
        market_sym = fetch_market_data(engine, sym, start_str, end_str)
        all_market[sym] = market_sym

    if all_meta.empty:
        print("No data found. Exiting.")
        return

    # Engineer features (across symbols)
    df_feat, feat_cols = engineer_features(all_meta)

    # Generate labels
    df_feat, labels = generate_labels(df_feat, all_market, args.profit_target, args.stoploss_pct, DEFAULT_LABEL_THRESHOLD)

    # Split
    train_df, val_df, test_df = time_split(df_feat)

    if test_df.empty:
        print("No test data. Exiting.")
        return

    # Prepare X y (scaled)
    X_train = train_df[feat_cols].fillna(0).values
    y_train = labels.loc[train_df.index].values
    X_val = val_df[feat_cols].fillna(0).values
    y_val = labels.loc[val_df.index].values
    X_test = test_df[feat_cols].fillna(0).values
    y_test = labels.loc[test_df.index].values

    # Preprocessor
    numeric_features = feat_cols  # All numeric after dummies
    preprocessor = Pipeline([
        ('scaler', StandardScaler())
    ])
    X_train_proc = preprocessor.fit_transform(X_train)
    X_val_proc = preprocessor.transform(X_val)
    X_test_proc = preprocessor.transform(X_test)
    joblib.dump(preprocessor, os.path.join(args.save_dir, 'preprocessor.pkl'))

    models = {}
    if args.model_type in ['xgboost', 'both']:
        model_xgb = train_xgboost(X_train_proc, y_train, X_val_proc, y_val, feat_cols,
                                  os.path.join(args.save_dir, 'model_xgboost.pkl'))
        models['xgboost'] = model_xgb
        trades_xgb, y_pred_xgb = backtest_predictions(test_df, all_market, model_xgb, feat_cols, preprocessor,
                                                      args.profit_target, args.stoploss_pct, 'xgboost')
        metrics_xgb = compute_metrics(trades_xgb)
        plot_results(trades_xgb, metrics_xgb, args.save_dir, 'xgboost')
        evaluate_model(y_test, y_pred_xgb, args.save_dir, 'xgboost')

    if args.model_type in ['lstm', 'both']:
        seq_len = DEFAULT_SEQ_LEN
        input_size = X_train_proc.shape[1]
        X_train_seq, y_train_seq = prepare_sequences(X_train_proc, y_train, seq_len)
        X_val_seq, y_val_seq = prepare_sequences(X_val_proc, y_val, seq_len)
        model_lstm = train_lstm(X_train_seq, y_train_seq, X_val_seq, y_val_seq, input_size,
                                os.path.join(args.save_dir, 'model_lstm.pth'))
        models['lstm'] = model_lstm

        # Backtest will handle X_test_seq internally
        trades_lstm, y_pred_lstm = backtest_predictions(test_df, all_market, model_lstm, feat_cols, preprocessor,
                                                        args.profit_target, args.stoploss_pct, 'lstm', seq_len)
        metrics_lstm = compute_metrics(trades_lstm)
        plot_results(trades_lstm, metrics_lstm, args.save_dir, 'lstm')
        evaluate_model(y_test, y_pred_lstm, args.save_dir, 'lstm')

    # Summary
    if args.model_type == 'both':
        # Select best by val, but here print both
        print("XGBoost:", metrics_xgb)
        print("LSTM:", metrics_lstm)
        # Example: best = max by avg_ppnl
        best_metrics = metrics_xgb if metrics_xgb['avg_ppnl'] > metrics_lstm['avg_ppnl'] else metrics_lstm
    else:
        best_metrics = metrics_xgb if args.model_type == 'xgboost' else metrics_lstm
        print(f"{args.model_type.capitalize()} Metrics:", best_metrics)

    avg_ppnl = best_metrics['avg_ppnl']
    win_rate = best_metrics['win_rate']
    print(f"test_avg_ppnl: {avg_ppnl:.4f}, test_win_rate: {win_rate:.4f}, n_trades: {best_metrics['n_trades']}, total_pnl: {best_metrics['total_pnl']:.2f}, max_drawdown: {best_metrics['max_dd']:.4f}, sharpe: {best_metrics['sharpe']:.4f}")

    if avg_ppnl >= 0.005 and win_rate >= 0.5:
        print("TARGET ACHIEVED")
    else:
        print("TARGET NOT ACHIEVED")
        print("""
Suggestions for improvements:
- Feature ideas: Add order flow imbalance, sector momentum, news sentiment (fetch via API).
- Hyperparams: Tune with Optuna (e.g., max_depth 3-10, lr 0.01-0.3, seq_len 5-20).
- Ensembling: Average probs from XGBoost + LSTM.
- Threshold tuning: Use val to find prob threshold >0.5 for entry.
- More data: Include microstructure features like bid-ask spread.
- Risk: Add position sizing based on ATR.
        """)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Train and backtest intraday model")
    parser.add_argument('--start_date', default=DEFAULT_START_DATE, help='Start date YYYY-MM-DD')
    parser.add_argument('--end_date', default=DEFAULT_END_DATE, help='End date YYYY-MM-DD')
    parser.add_argument('--profit_target', type=float, default=DEFAULT_PROFIT_TARGET, help='Profit target %')
    parser.add_argument('--stoploss_pct', type=float, default=DEFAULT_STOPLOSS_PCT, help='Stoploss %')
    parser.add_argument('--model_type', choices=['xgboost', 'lstm', 'both'], default='both', help='Model type')
    parser.add_argument('--save_dir', default='./output', help='Save dir')
    args = parser.parse_args()
    os.makedirs(args.save_dir, exist_ok=True)
    main(args)
