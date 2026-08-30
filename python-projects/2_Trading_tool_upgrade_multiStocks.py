import pandas as pd
import yfinance as yf

# Liste von Aktien
stocks = ["AAPL", "MSFT", "TSLA", "MSTR", "NVDA"]

def calculate_rsi(data, period=14):
    delta = data['Close'].diff()

    gain = (delta.where(delta > 0, 0)).rolling(window=period).mean()
    loss = (-delta.where(delta < 0, 0)).rolling(window=period).mean()

    rs = gain / loss
    rsi = 100 - (100 / (1 + rs))

    return rsi


for stock in stocks:
    data = yf.download(stock, period="1mo", interval="1d")

    data['RSI'] = calculate_rsi(data)

    latest_rsi = data['RSI'].iloc[-1]

    if latest_rsi < 30:
        signal = "BUY"
    elif latest_rsi > 70:
        signal = "SELL"
    else:
        signal = "HOLD"

    print(f"{stock} → RSI: {latest_rsi:.2f} → {signal}")

    all_data = {}
    for stock in stocks:
        data = yf.download(stock, period="1mo", interval="1d")
        all_data[stock] = data

print(all_data)