#!/usr/bin/env python3
"""
LSTM Stock Price Prediction (AXISBANK)
- Loads OHLCV data from MySQL
- Resamples from 5-min to 15-min candles
- Trains LSTM to predict closing price
- Evaluates MSE & RMSE
- Visualizes gradients and activations using see-rnn
"""

import pandas as pd
import numpy as np
import mysql.connector
from datetime import datetime
from sklearn.preprocessing import MinMaxScaler
from sklearn.metrics import mean_squared_error
from keras.models import Sequential
from keras.layers import LSTM, Dense
import plotly.graph_objects as go
import tensorflow as tf
import warnings
warnings.filterwarnings("ignore", category=DeprecationWarning)

# ==========================================================
# 1️⃣ Load & Resample Data
# ==========================================================
def load_symbol_data(symbol='AXISBANK', duration='5minute'):
    """
    Load OHLCV data from MySQL for a given symbol/duration,
    resample from 5-min to 15-min candles.
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
            open, high, low, close, volume
        FROM market_data
        WHERE symbol = %s
          AND duration = %s
          AND is_correct = 1
        ORDER BY start_timestamp ASC;
    """

    df = pd.read_sql(query, conn, params=(symbol, duration))
    conn.close()

    if df.empty:
        raise ValueError(f"No data found for symbol={symbol}, duration={duration}")

    df['date'] = pd.to_datetime(df['date'])
    df.set_index('date', inplace=True)
    df.sort_index(inplace=True)

    # ✅ Resample from 5-min to 15-min
    df_15 = pd.DataFrame()
    df_15['open'] = df['open'].resample('15min').first()
    df_15['high'] = df['high'].resample('15min').max()
    df_15['low'] = df['low'].resample('15min').min()
    df_15['close'] = df['close'].resample('15min').last()
    df_15['volume'] = df['volume'].resample('15min').sum()
    df_15.dropna(inplace=True)

    print(f"✅ Loaded and resampled {len(df)}→{len(df_15)} rows for {symbol}")
    return df_15


# ==========================================================
# 2️⃣ Prepare Data for LSTM
# ==========================================================
def prepare_data(df, train_split=20000):
    new_df = df[['close']]
    scaler = MinMaxScaler(feature_range=(0, 1))
    final_dataset = new_df.values

    train_data = final_dataset[:train_split]
    valid_data = final_dataset[train_split:]

    scaled_data = scaler.fit_transform(final_dataset)

    x_train, y_train = [], []
    for i in range(60, len(train_data)):
        x_train.append(scaled_data[i-60:i, 0])
        y_train.append(scaled_data[i, 0])

    x_train, y_train = np.array(x_train), np.array(y_train)
    x_train = np.reshape(x_train, (x_train.shape[0], x_train.shape[1], 1))

    print(f"✅ Training samples: {x_train.shape[0]}, Validation samples: {len(valid_data)}")
    return x_train, y_train, valid_data, new_df, scaler


# ==========================================================
# 3️⃣ Build & Train LSTM Model
# ==========================================================
def build_lstm_model(input_shape):
    model = Sequential([
        LSTM(units=50, return_sequences=True, input_shape=input_shape),
        LSTM(units=50),
        Dense(1)
    ])
    model.compile(loss='mean_squared_error', optimizer='adam')
    model.summary()
    return model


# ==========================================================
# 4️⃣ Visualization Helpers
# ==========================================================
def plot_predictions(train_df, valid_df):
    fig = go.Figure()
    fig.add_trace(go.Scatter(x=train_df.index, y=train_df['Close'], mode='lines', name='Train Data'))
    fig.add_trace(go.Scatter(x=valid_df.index, y=valid_df['Close'], mode='lines', name='Actual Close'))
    fig.add_trace(go.Scatter(x=valid_df.index, y=valid_df['Predictions'], mode='lines', name='Predicted Close'))
    fig.update_layout(title="LSTM Close Price Prediction", height=600, width=1000)
    fig.show()

def get_gradients(model, x_batch, y_batch):
    """Return gradients of loss wrt model trainable weights."""
    with tf.GradientTape() as tape:
        preds = model(x_batch, training=True)
        loss = tf.keras.losses.mean_squared_error(y_batch, preds)
    grads = tape.gradient(loss, model.trainable_variables)
    return grads

def get_outputs(model, x_batch, layer_names=None):
    """
    Return outputs (activations) from each layer of the model.
    If `layer_names` is provided, only those layers' outputs are returned.
    """
    # Collect layers to extract activations from
    if layer_names is None:
        layers = [layer for layer in model.layers if 'input' not in layer.name]
    else:
        layers = [layer for layer in model.layers if layer.name in layer_names]

    # Build a new model that outputs all intermediate activations
    activation_model = tf.keras.models.Model(
        inputs=model.input,
        outputs=[layer.output for layer in layers]
    )

    # Get activations
    activations = activation_model.predict(x_batch)

    # Return a dictionary mapping layer name → activations
    return {layer.name: act for layer, act in zip(layers, activations)}

# ==========================================================
# 5️⃣ Main Execution
# ==========================================================
if __name__ == "__main__":
    df = load_symbol_data('AXISBANK', '5minute')

    x_train, y_train, valid_data, new_df, scaler = prepare_data(df)
    model = build_lstm_model((x_train.shape[1], 1))

    model.fit(x_train, y_train, epochs=1, batch_size=1, verbose=2)

    # Predict
    inputs = new_df[len(new_df) - len(valid_data) - 60:].values
    inputs = inputs.reshape(-1, 1)
    inputs = scaler.transform(inputs)

    X_test = []
    for i in range(60, inputs.shape[0]):
        X_test.append(inputs[i-60:i, 0])
    X_test = np.array(X_test)
    X_test = np.reshape(X_test, (X_test.shape[0], X_test.shape[1], 1))

    predicted_price = model.predict(X_test)
    predicted_price = scaler.inverse_transform(predicted_price)

    # Create DataFrames
    train_df = pd.DataFrame({'Close': new_df[:20000].values.flatten()}, index=new_df[:20000].index)
    valid_df = pd.DataFrame({'Close': new_df[20000:].values.flatten()}, index=new_df[20000:].index)
    valid_df['Predictions'] = predicted_price

    # Metrics
    mse = mean_squared_error(valid_df['Close'], valid_df['Predictions'])
    rmse = np.sqrt(mse)
    print(f"\n📊 Mean Squared Error (MSE): {mse:.3f}")
    print(f"📈 Root Mean Squared Error (RMSE): {rmse:.3f}")

    # Plot results
    plot_predictions(train_df, valid_df)

    # ======================================================
    # 🔍 SEE-RNN VISUALIZATIONS
    # ======================================================
    print("\n🔍 Generating gradient and activation visualizations...")

    grads1 = get_gradients(model, layer_idx=1, x=x_train, y=y_train)
    grads2 = get_gradients(model, layer_idx=2, x=x_train, y=y_train)
    outputs = get_outputs(model, layer_idx=1, x=x_train)

    # Feature visualizations
    features_1D(grads1[:500], n_rows=2)
    features_2D(grads2[:500], n_rows=2)
    features_1D(outputs[:500], n_rows=2)

    # Weight heatmaps
    # rnn_histogram(model, 'lstm')
    # rnn_heatmap(model, 'lstm')

    print("\n✅ All visualizations complete.")
