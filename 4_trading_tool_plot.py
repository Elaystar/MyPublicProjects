import pandas as pd
import yfinance as yf
import matplotlib.pyplot as plt

# Aktie laden
data = yf.download("MSTR", period="1mo", interval="1d")

# RSI berechnen
def calculate_rsi(data, period=14):
    delta = data['Close'].diff()
    gain = (delta.where(delta > 0, 0)).rolling(window=period).mean()
    loss = (-delta.where(delta < 0, 0)).rolling(window=period).mean()
    rs = gain / loss
    rsi = 100 - (100 / (1 + rs))
    return rsi

data['RSI'] = calculate_rsi(data)

# Plot
fig, ax1 = plt.subplots(figsize=(10,5))

# Preislinie
ax1.plot(data.index, data['Close'], color='blue', label='Close Price')
ax1.set_ylabel('Preis', color='blue')
ax1.tick_params(axis='y', labelcolor='blue')

# RSI auf zweiter Achse
ax2 = ax1.twinx()
ax2.plot(data.index, data['RSI'], color='red', label='RSI')
ax2.axhline(70, color='grey', linestyle='--')
ax2.axhline(30, color='grey', linestyle='--')
ax2.set_ylabel('RSI', color='red')
ax2.tick_params(axis='y', labelcolor='red')

# Titel & Legende
plt.title('MSTR: Preis und RSI (1 Monat)')
fig.tight_layout()
plt.show()