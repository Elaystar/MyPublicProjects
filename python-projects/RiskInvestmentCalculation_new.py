#User Story: Nutzer will ausgehend von seinem gesamten Investmentkapital bestimmtes Maximalrisiko (2%) berechnen, bezogen auf
#den Anteil, den er investieren möchte. dieses % Gesamtrisiko bezieht man anschließend auf ein anteiligen Investmentbetrag (z.B. 30% wegen DCA DollarCostAverage) vom
# gesamten Investmentkapital, indem man den anteiligen Investmentbetrag durch den aktuellen Aktienpreis für ein Stück teilt, dadurch die Anzahl der möglichen zu
# erwerbenden Aktien erhält und letztendlich den Prozentwert des Maximalrisikos durch diese Anzahl der Aktien teilt.
#Nutzen: Dadurch erhält man den zu setzenden Stop loss Abstand (der nun größer ist, als das Maximalrisiko, z.B. >2%) zum aktuellen Aktienwert.
#Ohne die Ermittlung eines "Stop loss"-Abstands, also ohne Risikokalkulation für ein Investieren sollte man keinesfalls vorher investieren.
#Erst sollte Stop loss beschlossen sein, danach darf erst investiert werden, ohne stop loss dann noch niedriger zu setzen.

import math
import pandas as pd
import yfinance as yf
import logging
import os

# ---------------------------
# LOGGING SETUP
# ---------------------------
LOG_PATH = "data/logs"
os.makedirs(LOG_PATH, exist_ok=True)

logging.basicConfig(
    filename=f"{LOG_PATH}/RiskInvestmentCalculation.log",
    level=logging.INFO, #hier kann man auch DEBUG stellen, aber zu viel aktuell wei yf dann auch angezeigt wird im .log
    format="%(asctime)s - %(levelname)s - %(message)s"
)


# ---------------------------
# 1. Aktienpreis holen
# ---------------------------
def hole_aktienpreis(ticker, retries=3):
    for attempt in range(retries):
        try:
            logging.info(f"Lade Daten für {ticker} (Attempt {attempt+1})")

            daten = yf.download(
                ticker,
                period="1d",
                interval="5m",
                progress=False,
                threads=False
            )

            if daten.empty:
                raise ValueError("Leere Daten")

            close_data = daten["Close"].iloc[-1]

            preis = float(close_data.values[0]) if hasattr(close_data, "values") else float(close_data)

            logging.info(f"Preis geladen: {preis}")
            return preis

        except Exception as e:
            logging.warning(f"Attempt {attempt+1} fehlgeschlagen: {e}")
            time.sleep(1.5)

    logging.error("Alle Versuche Aktienpreis fehlgeschlagen")
    return None


# ---------------------------
# 2. USD -> EUR Kurs holen
# ---------------------------
def hole_wechselkurs(retries=3):
    for attempt in range(retries):
        try:
            logging.info(f"Lade Wechselkurs (Attempt {attempt+1})")

            daten = yf.download(
                "EURUSD=X",
                period="1d",
                interval="1m",
                progress=False,
                threads=False
            )

            if daten.empty:
                raise ValueError("Leere FX Daten")

            close_data = daten["Close"].iloc[-1]

            kurs = float(close_data.values[0]) if hasattr(close_data, "values") else float(close_data)

            logging.info(f"FX Kurs geladen: {kurs}")
            return kurs

        except Exception as e:
            logging.warning(f"FX Attempt {attempt+1} fehlgeschlagen: {e}")
            time.sleep(1.5)

    logging.error("Alle FX Versuche fehlgeschlagen")
    return None

# ---------------------------
# 3. Daten laden
# ---------------------------
def lade_daten(dateiname):
    laden = input("Möchtest du vorhandene Daten laden? (y/n): ")

    if laden.lower() == "y":
        try:
            df = pd.read_csv(dateiname)
            print("\nBisherige Trades:")
            print(df)
        except FileNotFoundError:
            print("Keine gespeicherten Daten gefunden.")


# ---------------------------
# 4. Eingaben
# ---------------------------
def eingabe():
    max_risiko = input("Maximales Risiko in % (Standard 2): ")
    max_risiko = float(max_risiko) if max_risiko else 2.0

    if max_risiko <= 0:
        max_risiko = 2.0

    gesamt_kapital = float(input("Gesamtkapital (€): "))

    aktienname = input("Ticker (z.B. AAPL, TSLA, NVDA, MSTR): ")

    aktie_holen = input("Aktienpreis automatisch holen? (y/n): ")

    if aktie_holen.lower() == "y":
        aktienwert_usd = hole_aktienpreis(aktienname)

        if aktienwert_usd is None:
            print("Fallback: manueller Preis nötig.")
            aktienwert = float(input("Aktienpreis (€): "))
        else:
            wechselkurs = hole_wechselkurs()

            if wechselkurs is None:
                print("Kein Wechselkurs → USD wird verwendet!")
                aktienwert = aktienwert_usd
            else:
                aktienwert = aktienwert_usd / wechselkurs

            print(f"Aktueller Preis: {aktienwert:.2f} €")

    else:
        aktienwert = float(input("Aktienpreis (€): "))

    invest_kapital = float(input("Investitionsbetrag (€): "))

    return {
        "max_risiko": max_risiko,
        "gesamt_kapital": gesamt_kapital,
        "aktienname": aktienname,
        "aktienwert": aktienwert,
        "invest_kapital": invest_kapital
    }


# ---------------------------
# 5. Berechnungen
# ---------------------------
def berechnungen(daten):
    max_risiko = daten["max_risiko"]
    gesamt_kapital = daten["gesamt_kapital"]
    aktienwert = daten["aktienwert"]
    invest_kapital = daten["invest_kapital"]

    gesamt_risiko_wert = gesamt_kapital * max_risiko / 100

    anz_aktien = invest_kapital / aktienwert
    anz_aktien_abgerundet = math.floor(anz_aktien)

    if anz_aktien_abgerundet == 0:
        print("Zu wenig Kapital für eine Aktie.")
        exit()

    stop_loss_abstand = gesamt_risiko_wert / anz_aktien_abgerundet
    stop_wert = aktienwert - stop_loss_abstand
    stop_abstand_satz = (stop_wert - aktienwert) / aktienwert * 100

    return {
        "gesamt_risiko_wert": gesamt_risiko_wert,
        "anz_aktien": anz_aktien_abgerundet,
        "stop_loss_abstand": stop_loss_abstand,
        "stop_wert": stop_wert,
        "stop_abstand_satz": round(stop_abstand_satz, 2)
    }


# ---------------------------
# 6. Ergebnis
# ---------------------------
def ergebnis_anzeigen(eingaben, ergebnisse):
    data = pd.Series({
        "Aktie": eingaben["aktienname"],
        "Aktienpreis (€)": eingaben["aktienwert"],
        "Invest (€)": eingaben["invest_kapital"],
        "Anzahl Aktien": ergebnisse["anz_aktien"],
        "Max Risiko %": eingaben["max_risiko"],
        "Risiko (€)": ergebnisse["gesamt_risiko_wert"],
        "Stop Loss (€)": ergebnisse["stop_wert"],
        "Stop Abstand %": ergebnisse["stop_abstand_satz"]
    })

    print("\n--- Ergebnis ---")
    print(data)

    return data


# ---------------------------
# 7. CSV speichern
# ---------------------------
def speichern_csv(data, dateiname):
    df_neu = data.to_frame().T

    try:
        df_alt = pd.read_csv(dateiname)
        df_gesamt = pd.concat([df_alt, df_neu], ignore_index=True)
    except FileNotFoundError:
        df_gesamt = df_neu

    df_gesamt.to_csv(dateiname, index=False)
    print("Daten wurden gespeichert.")


# ---------------------------
# 8. MAIN
# ---------------------------
def main():
    dateiname = "data/RiskInvestmentCalculation.csv"

    lade_daten(dateiname)

    eingaben = eingabe()
    ergebnisse = berechnungen(eingaben)

    data = ergebnis_anzeigen(eingaben, ergebnisse)

    speichern_csv(data, dateiname)


if __name__ == "__main__":
    main()