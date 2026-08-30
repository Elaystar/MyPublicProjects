import yfinance as yf
import pandas as pd
import csv
import os
from datetime import datetime


# =========================
# CONFIG
# =========================
SYMBOL = "MSTR"
JOURNAL_FILE = "data/logs/5_trading_bot_journal.csv"


# =========================
# DATA LOADING
# =========================
def load_data(symbol: str) -> pd.DataFrame:
    data = yf.download(symbol, period="6mo", interval="1d")

    if isinstance(data.columns, pd.MultiIndex):
        data.columns = data.columns.get_level_values(0)

    return data


# =========================
# INDICATORS
# =========================
def add_indicators(data: pd.DataFrame) -> pd.DataFrame:
    data["MA20"] = data["Close"].rolling(window=20).mean()
    data["MA50"] = data["Close"].rolling(window=50).mean()
    return data


# =========================
# SIGNAL ENGINE
# =========================
def generate_signal(latest_row) -> tuple:
    ma20 = float(latest_row["MA20"])
    ma50 = float(latest_row["MA50"])
    close = float(latest_row["Close"])

    if ma20 > ma50 and close > ma20:
        signal = "BUY"
    elif ma20 < ma50:
        signal = "NO TRADE"
    else:
        signal = "HOLD"

    if ma20 > ma50:
        trend = "UPTREND 📈"
    elif ma20 < ma50:
        trend = "DOWNTREND 📉"
    else:
        trend = "UNCLEAR ⚪"

    return signal, trend, ma20, ma50, close


# =========================
# FUTURE RETURN
# =========================
def calculate_return(data: pd.DataFrame) -> float:
    return (data["Close"].iloc[-1] - data["Close"].iloc[-2]) / data["Close"].iloc[-2]


# =========================
# JOURNAL INIT
# =========================
def init_journal():
    os.makedirs("data/logs", exist_ok=True)

    if not os.path.exists(JOURNAL_FILE):
        with open(JOURNAL_FILE, mode="w", newline="") as file:
            writer = csv.writer(file)
            writer.writerow([
                "timestamp",
                "symbol",
                "close",
                "ma20",
                "ma50",
                "signal",
                "future_return"
            ])


# =========================
# SAVE JOURNAL
# =========================
def save_to_journal(symbol, close, ma20, ma50, signal, future_return):
    with open(JOURNAL_FILE, mode="a", newline="") as file:
        writer = csv.writer(file)
        writer.writerow([
            datetime.now(),
            symbol,
            close,
            ma20,
            ma50,
            signal,
            future_return
        ])


# =========================
# ANALYSIS / AI INSIGHT
# =========================
def analyze_journal():
    df = pd.read_csv(JOURNAL_FILE)

    print("\nSignal Verteilung:")
    print(df["signal"].value_counts())

    print("\nSignal Performance:")
    print(df.groupby("signal")["future_return"].mean())

    print("\nAI Insight:")

    df_clean = df.dropna(subset=["future_return"])

    buy = df_clean[df_clean["signal"] == "BUY"]
    no_trade = df_clean[df_clean["signal"] == "NO TRADE"]

    print("BUY avg return:", buy["future_return"].mean() if len(buy) > 0 else "N/A")
    print("NO TRADE avg return:", no_trade["future_return"].mean() if len(no_trade) > 0 else "N/A")


# =========================
# MAIN
# =========================
def main():
    data = load_data(SYMBOL)
    data = add_indicators(data)

    latest = data.iloc[-1]

    signal, trend, ma20, ma50, close = generate_signal(latest)

    print("Trend:", trend)
    print("Signal:", signal)

    future_return = calculate_return(data)

    init_journal()
    save_to_journal(SYMBOL, close, ma20, ma50, signal, future_return)

    analyze_journal()


# =========================
# RUN
# =========================
if __name__ == "__main__":
    main()