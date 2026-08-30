import pandas as pd
import yfinance as yf

#Daten laden (Mstr Aktie)
data = yf.download("MSTR", period="1mo", interval="1d")
data['Delta'] = data['Close'].diff()
print(data)
#RSI berechnen

def calculate_rsi(data, period=14):
    delta = data['Close'].diff()

    gain =(delta.where(delta > 0, 0)).rolling(window=period).mean()
    loss = (-delta.where(delta < 0, 0)).rolling(window=period).mean()

    rs = gain /loss
    rsi = 100 - (100 / (1 + rs))
    return rsi

#RSI GAIN LOSS in data tabelle einfügen
data['RSI'] = calculate_rsi(data)
data['Gain'] = data['Delta'].where(data['Delta'] > 0, 0)
data['Loss'] = -data['Delta'].where(data['Delta'] < 0, 0)

print(data)
#Letzten Wert nehmen
latest_rsi = data['RSI'].iloc[-1]

#Signal erzeugen
if  latest_rsi < 30:
    signal = "BUY (<30)"
elif latest_rsi >70:
    signal = "SELL (>70)"
else:
    signal = "HOLD (RSI between 30-70)"

print(f"RSI: {latest_rsi:.2f}")
print(f"Signal: {signal}")

