import yfinance as yf
import pandas as pd
import csv
import os
from datetime import datetime

# ======================
# CONFIG
# ======================
symbol = "MSTR"

os.makedirs("data/logs", exist_ok=True)
journal_file = "data/logs/5_trading_bot_journal.csv"

# ======================
# DATA
# ======================
data = yf.download(symbol, period="6mo", interval="1d")

if isinstance(data.columns, pd.MultiIndex):
    data.columns = data.columns.get_level_values(0)

# ======================
# INDICATORS
# ======================
data["MA20"] = data["Close"].rolling(window=20).mean()
data["MA50"] = data["Close"].rolling(window=50).mean()

latest = data.iloc[-1]

ma20 = float(latest["MA20"])
ma50 = float(latest["MA50"])
close = float(latest["Close"])

# ======================
# TREND
# ======================
if ma20 > ma50:
    trend = "UPTREND 📈"
elif ma20 < ma50:
    trend = "DOWNTREND 📉"
else:
    trend = "UNCLEAR ⚪"

print("Trend:", trend)

# ======================
# SIGNAL
# ======================
if ma20 > ma50 and close > ma20:
    signal = "BUY"
elif ma20 < ma50:
    signal = "NO TRADE"
else:
    signal = "HOLD"

print("Signal:", signal)

# ======================
# FUTURE RETURN (sauber)
# ======================
future_return = (data["Close"].iloc[-1] - data["Close"].iloc[-2]) / data["Close"].iloc[-2]

# ======================
# JOURNAL SETUP
# ======================
os.makedirs("data/logs", exist_ok=True)

file_exists = os.path.isfile(journal_file)

with open(journal_file, mode="a", newline="") as file:
    writer = csv.writer(file)

    if not file_exists:
        writer.writerow([
            "timestamp",
            "symbol",
            "close",
            "ma20",
            "ma50",
            "signal",
            "future_return"
        ])

    writer.writerow([
        datetime.now(),
        symbol,
        close,
        ma20,
        ma50,
        signal,
        future_return
    ])

# ======================
# ANALYSE
# ======================
df = pd.read_csv(journal_file)

print("\nSignal Verteilung:")
print(df["signal"].value_counts())

print("\nSignal Performance:")
print(df.groupby("signal")["future_return"].mean())

# ======================
# AI INSIGHT (erste Stufe)
# ======================
print("\nAI Insight:")

df_clean = df.dropna(subset=["future_return"])

buy = df_clean[df_clean["signal"] == "BUY"]
no_trade = df_clean[df_clean["signal"] == "NO TRADE"]

print("BUY avg return:", buy["future_return"].mean() if len(buy) > 0 else "N/A")
print("NO TRADE avg return:", no_trade["future_return"].mean() if len(no_trade) > 0 else "N/A")