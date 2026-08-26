import pandas as pd
import yfinance as yf

import os
os.makedirs("data", exist_ok=True)

# Liste der Aktien
stocks = ["AAPL", "MSFT", "NVDA", "MSTR", "AMD"]


# Funktion für RSI
def calculate_rsi(data, period=14):
    delta = data['Close'].diff()
    gain = (delta.where(delta > 0, 0)).rolling(window=period).mean()
    loss = (-delta.where(delta < 0, 0)).rolling(window=period).mean()
    rs = gain / loss
    rsi = 100 - (100 / (1 + rs))
    return rsi


# Dictionary für alle Aktien
all_data = {}

# Schleife über alle Aktien
for stock in stocks:
    data = yf.download(stock, period="1mo", interval="1d")
    data['RSI'] = calculate_rsi(data)
    all_data[stock] = data  # Speichern in Dictionary

# Ausgabe Beispiel: letzter RSI und Signal pro Aktie
for stock, data in all_data.items():
    latest_rsi = data['RSI'].iloc[-1]
    #In Dateien speichern
    data.to_csv(f"data/{stock}_data.csv")

    if latest_rsi < 30:
        signal = "BUY"
    elif latest_rsi > 70:
        signal = "SELL"
    else:
        signal = "HOLD"

    print(f"{stock} → RSI: {latest_rsi:.2f} → {signal}")

# In Datei speichern .csv Format
   # for stock, data in all_data.items():
       # data.to_csv(f"{stock}_data.csv")