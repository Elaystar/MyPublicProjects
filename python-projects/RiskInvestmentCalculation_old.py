#User Story: Nutzer will ausgehend von seinem gesamten Investmentkapital bestimmtes Maximalrisiko (2%) berechnen, bezogen auf
#den Anteil, den er investieren möchte. dieses % Gesamtrisiko bezieht man anschließend auf ein anteiligen Investmentbetrag (z.B. 30% wegen DCA DollarCostAverage) vom gesamten Investmentkapital, indem
#man den anteiligen Investmentbetrag durch den aktuellen Aktienpreis für ein Stück teilt, dadurch die Anzahl der möglichen zu erwerbenden Aktien erhält und letztendlich
# den Prozentwert des Maximalrisikos durch diese Anzahl der Aktien teilt.
#Nutzen: Dadurch erhält man den zu setzenden Stop loss Abstand (der nun größer ist, als das Maximalrisiko, z.B. >2%) zum aktuellen Aktienwert.
#Ohne die Ermittlung eines "Stop loss"-Abstands, also ohne Risikokalkulation für ein Investieren sollte man keinesfalls vorher investieren.
#Erst sollte Stop loss beschlossen sein, danach darf erst investiert werden, ohne stop loss dann noch niedriger zu setzen.

import math
import pandas as pd

##wichtig, alle rounds rauslöschen, da sonst berechnungen ungenau werden. dafür f-string verwenden, siehe unten bei: stop_wert

#def vorige_daten_daden():
dateiname = "trading_daten.csv"

laden = input("Möchtest du vorhandene Daten laden? (y/n): ")

if laden.lower() == "y":
    try:
        df = pd.read_csv(dateiname)
        print("\nBisherige Trades:")
        print(df)
    except FileNotFoundError:
        print("Keine gespeicherten Daten gefunden.")

def eingabe():
    max_risiko = input("Wie hoch in % soll das maximale Risiko auf das Gesamte zur verfügung stehende Investmentkapital sein? (Standard 2%)")
    print(f"Maximalrisiko in % ist auf  {max_risiko}:.2f gesetzt")
    return max_risiko

def maximalrisiko(max_risiko):
    if max_risiko <= 0:
    max_risiko = 2
    print("Maximalrisiko wurde standardmäßig auf 2% gesetzt.")
    return max_risiko

maximalrisiko(eingabe())

def gesamtrisikowert(maximalrisiko()):
    gesamt_kapital = input("Wie hoch ist das Gesamtkapital:")
    #gesamt_kapital = round(float(gesamt_kapital),2)

    #Berechnung Maximalrisiko-Wert
    gesamt_risiko_wert= gesamt_kapital * maximalrisiko() /100
    print("Gesamtrisiko Betrag ist: ",gesamt_risiko_wert)

#Berechnung von anzahl der Aktien bei bestimmten Anteil an Investmentwert

aktienname = input("Wie heißt die Aktie?")
aktienwert = input("Wie ist der aktuelle Aktienpreis bei dem investiert werden soll?")

invest_kapital = input("Für wie viel Kapital soll investiert werden?")
#invest_kapital = round(float(invest_kapital),2)
print("Es wird also für ", invest_kapital, " Euro investiert.")

anz_aktien = invest_kapital / aktienwert

#Abrunden geht auch bei positiven zahlen mit int(anz_aktien)
anz_aktien_abgerundet = math.floor(anz_aktien)

if anz_aktien_abgerundet == 0:
    print("Zu wenig Kapital für auch nur eine Aktie.")
    exit()

print("Bei einem Maximalrisiko von ", max_risiko, "% können ", anz_aktien_abgerundet, " Stück der ", aktienname, "Aktie gekauft werden.")

#Berechnung Stop loss Abstand
stop_loss_abstand = gesamt_risiko_wert /anz_aktien_abgerundet
print("Der Stop-loss Abstand ", stop_loss_abstand, " Euro zu dem Aktienpreis ",aktienwert, " Euro setzen.")

stop_wert = aktienwert -stop_loss_abstand
print(f"Stop-loss beim Aktienwert: {stop_wert:.2f} Euro setzen!")
stop_abstand_satz = (stop_wert - aktienwert) /aktienwert *100
#stop_abstand_satz_gerundet
print("Das sind ", round(stop_abstand_satz, 2), "% Abstand zum Aktienpreis.")

#Speichern in panda Series
#def eingabe_sichern():
data = pd.Series({
    "Aktie": aktienname,
    "Aktienpreis": aktienwert,
    "Investiertes Kapital": invest_kapital,
    "Anzahl Aktien": anz_aktien_abgerundet,
    "Max Risiko %": max_risiko,
    "Gesamtrisiko €": gesamt_risiko_wert,
    "Stop Loss €": stop_wert,
    "Stop Abstand %": stop_abstand_satz
})

print("\n--- Ergebnis ---")
print(data)

df_anzeige = data.to_frame(name="Wert")
print(df_anzeige)

#Speichern in .csv Datei
def save_csv():
    dateiname = "trading_daten.csv"

    # Series → DataFrame umwandeln (1 Zeile)
    df_neu = data.to_frame().T

    try:
        df_alt = pd.read_csv(dateiname)
        df_gesamt = pd.concat([df_alt, df_neu], ignore_index=True)
    except FileNotFoundError:
        df_gesamt = df_neu

    df_gesamt.to_csv(dateiname, index=False)

    print("Daten wurden gespeichert.")
