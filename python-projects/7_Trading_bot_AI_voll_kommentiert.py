# =========================
# IMPORTS (Bibliotheken laden)
# =========================

import yfinance as yf        # holt Börsendaten von Yahoo Finance
import pandas as pd          # Datenanalyse (Tabellen, CSV, etc.)
import csv                   # Schreiben von CSV-Dateien
import os                    # Dateisystem (Ordner prüfen/erstellen)
from datetime import datetime # Zeitstempel für Journal


# =========================
# KONFIGURATION
# =========================

SYMBOL = "MSTR"  # welche Aktie wir analysieren

JOURNAL_FILE = "data/logs/5_trading_bot_journal.csv"
# Speicherort für deine Trading-Historie


# =========================
# DATA LAYER
# =========================

def load_data(symbol: str):
    # lädt 6 Monate Tagesdaten für Aktie

    data = yf.download(symbol, period="6mo", interval="1d")

    # falls Yahoo MultiIndex liefert → vereinfachen
    if isinstance(data.columns, pd.MultiIndex):
        data.columns = data.columns.get_level_values(0)

    return data


# =========================
# FEATURE ENGINEERING
# =========================

def add_indicators(data):
    # berechnet technische Indikatoren

    data["MA20"] = data["Close"].rolling(window=20).mean()
    # Durchschnitt der letzten 20 Tage

    data["MA50"] = data["Close"].rolling(window=50).mean()
    # Durchschnitt der letzten 50 Tage

    return data


# =========================
# STRATEGY ENGINE (DEIN "DENKEN")
# =========================

def generate_signal(latest):

    ma20 = float(latest["MA20"])   # aktueller MA20 Wert
    ma50 = float(latest["MA50"])   # aktueller MA50 Wert
    close = float(latest["Close"]) # aktueller Kurs

    # Trend-Logik
    if ma20 > ma50:
        trend = "UPTREND 📈"
    elif ma20 < ma50:
        trend = "DOWNTREND 📉"
    else:
        trend = "UNCLEAR ⚪"

    # Handelsentscheidung
    if ma20 > ma50 and close > ma20:
        signal = "BUY"
    elif ma20 < ma50:
        signal = "NO TRADE"
    else:
        signal = "HOLD"

    # mehrere Werte zurückgeben
    return signal, trend, ma20, ma50, close


# =========================
# RETURN BERECHNUNG
# =========================

def calculate_return(data):

    # Veränderung letzter 2 Tage (einfaches Modell)
    return (data["Close"].iloc[-1] - data["Close"].iloc[-2]) / data["Close"].iloc[-2]


# =========================
# JOURNAL INITIALISIEREN
# =========================

def init_journal():

    # Ordner erstellen falls nicht existiert
    os.makedirs("data/logs", exist_ok=True)

    # Datei nur erstellen wenn sie noch nicht existiert
    if not os.path.exists(JOURNAL_FILE):

        with open(JOURNAL_FILE, mode="w", newline="") as file:
            writer = csv.writer(file)

            # Header (Spaltennamen)
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
# SPEICHERN INS JOURNAL
# =========================

def save_to_journal(symbol, close, ma20, ma50, signal, future_return):

    with open(JOURNAL_FILE, mode="a", newline="") as file:
        writer = csv.writer(file)

        # neue Zeile anhängen
        writer.writerow([
            datetime.now(),   # aktueller Zeitpunkt
            symbol,
            close,
            ma20,
            ma50,
            signal,
            future_return
        ])


# =========================
# ANALYSE / "AI INSIGHT"
# =========================

def analyze_journal():

    # CSV laden
    df = pd.read_csv(JOURNAL_FILE)

    print("\nSignal Verteilung:")
    print(df["signal"].value_counts())

    print("\nSignal Performance (Ø Return):")
    print(df.groupby("signal")["future_return"].mean())

    print("\nAI Insight (erste Statistik-Logik):")

    # NaN Werte entfernen (wichtig!)
    df_clean = df.dropna(subset=["future_return"])

    buy = df_clean[df_clean["signal"] == "BUY"]
    no_trade = df_clean[df_clean["signal"] == "NO TRADE"]

    print("BUY avg return:",
          buy["future_return"].mean() if len(buy) > 0 else "N/A")

    print("NO TRADE avg return:",
          no_trade["future_return"].mean() if len(no_trade) > 0 else "N/A")

#ML
def prepare_ml_data(data):
    df = data.copy()

    # Ziel: steigt der Preis morgen?
    df["target"] = (df["Close"].shift(-1) > df["Close"]).astype(int)

    # Features
    df["MA20"] = df["Close"].rolling(20).mean()
    df["MA50"] = df["Close"].rolling(50).mean()

    df = df.dropna()

    return df



from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier

def train_model(df):

    features = ["MA20", "MA50", "Close"]
    X = df[features]
    y = df["target"]

    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, shuffle=False
    )

    model = RandomForestClassifier()
    model.fit(X_train, y_train)

    accuracy = model.score(X_test, y_test)

    print("Model Accuracy:", accuracy)

    return model




def generate_ml_signal(model, latest):

    features = [[
        latest["MA20"],
        latest["MA50"],
        latest["Close"]
    ]]

    prediction = model.predict(features)[0]

    if prediction == 1:
        return "BUY"
    else:
        return "NO TRADE"






# =========================
# MAIN (ORCHESTRATOR)
# =========================


def main():

    # 1. Daten holen
    data = load_data(SYMBOL)

    # 2. Indikatoren berechnen
    data = add_indicators(data)

    # 3. letzte Zeile holen (aktueller Zustand)
    latest = data.iloc[-1]

    # 4. Signal berechnen
    signal, trend, ma20, ma50, close = generate_signal(latest)

    print("Trend:", trend)
    print("Signal:", signal)

    # 5. Return berechnen
    future_return = calculate_return(data)

    # 6. Journal vorbereiten
    init_journal()

    # 7. speichern
    save_to_journal(SYMBOL, close, ma20, ma50, signal, future_return)

    # 8. Analyse
    analyze_journal()



# =========================
# START PROGRAMM
# =========================

if __name__ == "__main__":
    main()