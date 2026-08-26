# =========================
# IMPORTS (Bibliotheken laden)
# =========================

import yfinance as yf              # Holt Börsendaten von Yahoo Finance
import pandas as pd                # Datenverarbeitung (Tabellen, DataFrames)
import csv                         # Schreiben von CSV-Dateien
import os                          # Arbeiten mit Ordnern / Dateien
from datetime import datetime      # Zeitstempel für Logging

from sklearn.model_selection import train_test_split   # Aufteilen in Training/Test
from sklearn.ensemble import RandomForestClassifier    # ML-Modell (Entscheidungsbäume)


# =========================
# KONFIGURATION
# =========================

SYMBOL = "MSTR"  # Aktie, die analysiert wird
JOURNAL_FILE = "data/logs/8_trading_bot_journal.csv"  # Speicherort für Logs


# =========================
# DATA LAYER
# =========================

def load_data(symbol: str):
    # Lädt 6 Monate historische Tagesdaten

    data = yf.download(symbol, period="6mo", interval="1d")

    # Falls Daten MultiIndex haben → vereinfachen
    if isinstance(data.columns, pd.MultiIndex):
        data.columns = data.columns.get_level_values(0)

    return data


# =========================
# FEATURE ENGINEERING
# =========================

def add_indicators(data):
    # Klassische technische Indikatoren

    data["MA20"] = data["Close"].rolling(window=20).mean()  # 20-Tage Durchschnitt
    data["MA50"] = data["Close"].rolling(window=50).mean()  # 50-Tage Durchschnitt

    return data


def add_advanced_features(data):
    # Erweiterte Features für ML

    data["Return"] = data["Close"].pct_change()
    # Prozentuale Veränderung zum Vortag

    data["Volatility"] = data["Return"].rolling(10).std()
    # Schwankungsbreite (Risiko)

    data["Momentum"] = data["Close"] - data["Close"].shift(5)
    # Bewegung über 5 Tage

    return data


# =========================
# RULE-BASED STRATEGIE
# =========================

def generate_signal(latest):
    # Holt aktuelle Werte

    ma20 = float(latest["MA20"])
    ma50 = float(latest["MA50"])
    close = float(latest["Close"])

    # Trend bestimmen
    if ma20 > ma50:
        trend = "UPTREND 📈"
    elif ma20 < ma50:
        trend = "DOWNTREND 📉"
    else:
        trend = "UNCLEAR ⚪"

    # Regelbasierte Entscheidung
    if ma20 > ma50 and close > ma20:
        signal = "BUY"
    elif ma20 < ma50:
        signal = "NO TRADE"
    else:
        signal = "HOLD"

    return signal, trend, ma20, ma50, close


# =========================
# ML DATA PREP
# =========================

def prepare_ml_data(data):

    df = data.copy()  # Kopie erstellen (Original nicht verändern)

    # Zielvariable:
    # 1 = Preis steigt morgen, 0 = nicht
    df["target"] = (df["Close"].shift(-1) > df["Close"]).astype(int)

    # WICHTIG: Alle Features müssen vorhanden sein!
    df = df.dropna()  # Entfernt Zeilen mit fehlenden Werten

    return df


# =========================
# ML MODELL TRAINING
# =========================

def train_model(df):

    # Features für das Modell
    features = ["MA20", "MA50", "Close", "Return", "Volatility", "Momentum"]

    X = df[features]   # Eingabedaten
    y = df["target"]   # Zielvariable

    # Aufteilung in Training und Test
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, shuffle=False
    )

    model = RandomForestClassifier()  # Modell erstellen
    model.fit(X_train, y_train)       # Modell trainieren

    accuracy = model.score(X_test, y_test)  # Bewertung
    print("Model Accuracy:", accuracy)

    return model


# =========================
# ML SIGNAL
# =========================

def generate_ml_signal(model, latest):

    # Wichtig: gleiche Struktur wie Training!
    df = pd.DataFrame([{
        "MA20": latest["MA20"],
        "MA50": latest["MA50"],
        "Close": latest["Close"],
        "Return": latest["Return"],
        "Volatility": latest["Volatility"],
        "Momentum": latest["Momentum"]
    }])

    prediction = model.predict(df)[0]

    return "BUY" if prediction == 1 else "NO TRADE"


# =========================
# RETURN BERECHNUNG
# =========================

def calculate_return(data):

    return (data["Close"].iloc[-1] - data["Close"].iloc[-2]) / data["Close"].iloc[-2]


# =========================
# JOURNAL
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
# ANALYSE
# =========================

def analyze_journal():

    df = pd.read_csv(JOURNAL_FILE)

    print("\nSignal Verteilung:")
    print(df["signal"].value_counts())

    print("\nSignal Performance:")
    print(df.groupby("signal")["future_return"].mean())


# =========================
# MAIN
# =========================

def main():

    # 1. Daten laden
    data = load_data(SYMBOL)

    # 2. Features berechnen (RICHTIGE REIHENFOLGE!)
    data = add_indicators(data)
    data = add_advanced_features(data)

    # 3. Aktuellen Zustand holen
    latest = data.iloc[-1]

    # 4. Regelbasierte Strategie
    signal, trend, ma20, ma50, close = generate_signal(latest)

    print("Trend:", trend)
    print("Rule Signal:", signal)

    # =========================
    # ML TEIL
    # =========================

    df_ml = prepare_ml_data(data)

    model = train_model(df_ml)

    ml_signal = generate_ml_signal(model, latest)

    print("ML Signal:", ml_signal)

    # =========================

    # 5. Return berechnen
    future_return = calculate_return(data)

    # 6. Journal speichern
    init_journal()
    save_to_journal(SYMBOL, close, ma20, ma50, signal, future_return)

    # 7. Analyse anzeigen
    analyze_journal()


# =========================
# START
# =========================

if __name__ == "__main__":
    main()