package com.example.domg.simonhmi;

import java.util.Observable;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import android.util.Log;

class Simon {

    private final byte NACHRICHT_RUNDE_GEWONNEN = 0;
    private final byte NACHRICHT_VERLOREN = 1;
    private final java.util.Random zufall = new java.util.Random();
    private final String name_kurz = Simon.class.getSimpleName();
    private hauptHost mainHost;
    private List<Byte> reihenfolgeAbspiel;
    private BeobachterScore beobachterScore = new BeobachterScore();
    private byte[] farbbuttonsIndex;
    private Iterator spiellistenZaehler;
    private boolean spielAn = false;

    protected Simon(final byte[] farbbuttonsIndex, final hauptHost mainHost)
    {
        super();
        if (farbbuttonsIndex.length == 0) throw new IllegalArgumentException("farbbuttonsIndex.length 1+");
        this.farbbuttonsIndex = farbbuttonsIndex;
        this.mainHost = mainHost;
    }

    protected Simon(final byte zaehlerButtons, final hauptHost mainHost)
    {
        super();
        if (zaehlerButtons == 0) throw new IllegalArgumentException("zaehlerButtons 1+");
        byte[] farbeIndex = new byte[zaehlerButtons];
        for (byte i = 0; i < zaehlerButtons; i++)
        {
            farbeIndex[i] = i;
        }
        this.farbbuttonsIndex = farbeIndex;
        this.mainHost = mainHost;
    }

    protected void anfang()
    {
        /* Spiel startet und beobachtet*/
        spielAn = true;
        beobachterScore.anfrageSenden(BeobachterScore.SCORE_RUECKSETZEN);
        reihenfolgeAbspiel = new ArrayList<>();
        spiellistenZaehler = reihenfolgeAbspiel.iterator();
        zufallZusatzNotieren();
        mainHost.abspiel(reihenfolgeAbspiel);
    }

    public void farbeGedrueckt(final byte farbeIndex) {
        if (!spielAn) return; //Simon igoriert button eingabe
        if (farbeGedruecktPruef(farbeIndex)) {
            beobachterScore.anfrageSenden(BeobachterScore.SCORE_ERHOEHEN); //erhöht Punktestand
            if (!naechste()) {
                //keine buttons mehr zu drücken
                beobachterScore.anfrageSenden(BeobachterScore.RUNDE_ERHOEHEN);
                try {
                    mainHost.nachrichtSpiel(NACHRICHT_RUNDE_GEWONNEN);

                } catch (Exception e) {
                    Log.w(name_kurz, "Fehler Nachricht senden win");
                }
                zufallZusatzNotieren();
                mainHost.abspiel(reihenfolgeAbspiel);
            }
        } else
            {
            //falscher button gedrückt
            try {
                mainHost.nachrichtSpiel(NACHRICHT_VERLOREN);
            } catch (Exception e) {
                Log.w(name_kurz, "Fehler Nachricht senden lose");
            }
            finally {
                mainHost.spielZuende();
                spielAn = false;
            }
        }
    }

    BeobachterScore getBeobachterScore()
    {
        return beobachterScore;
    }


    private boolean naechste() {
        //Return true wenn keine farben mehr, ansonsten false
        try {
            return spiellistenZaehler.hasNext();
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void zufallsfarbeHinzufuegen() {

        int IndexFarbeAusgabe = zufall.nextInt(farbbuttonsIndex.length);
        Log.v(name_kurz, "IndexFarbeAusgabe: " + IndexFarbeAusgabe);
        reihenfolgeAbspiel.add(farbbuttonsIndex[IndexFarbeAusgabe]);
    }

    @SuppressWarnings("unused")
    protected synchronized void zufallZusatzNotieren(final int zahlZumHinzufuegen)
    {
        // hinzufügen Zufallsfarben
        for (int i = 0; i < zahlZumHinzufuegen; i++)
        {
            zufallsfarbeHinzufuegen();
        }
        spiellistenZaehler = reihenfolgeAbspiel.iterator();
    }

    protected synchronized void zufallZusatzNotieren()
    {
        zufallsfarbeHinzufuegen();
        spiellistenZaehler = reihenfolgeAbspiel.iterator();
    }


    protected void setReihenfolgeAbspiel(List<Byte> meineAbspielReihenfolge)
    {
        this.reihenfolgeAbspiel = meineAbspielReihenfolge;
    }

    boolean farbeGedruecktPruef(final byte farbeIndex)
    {
        return spiellistenZaehler.hasNext() && farbeIndex == (Byte) spiellistenZaehler.next();
    }

    static interface hauptHost
    {
        public void spielZuende();

        public boolean abspiel(List<Byte> farblisteSpielen);


        public void einAbspiel(byte indexFarbe);

        public void nachrichtSpiel(byte CODE_NACHRICHT);
    }

    public class BeobachterScore extends Observable
    {
        private byte codeAnfrage;
        final static byte RUNDE_ERHOEHEN = 2;
        final static byte SCORE_ERHOEHEN = 4;
        final static byte SCORE_RUECKSETZEN = 1;
        byte getAnfrage() {
            return codeAnfrage;
        }

        void anfrageSenden(final byte CODE_ANFRAGE)
        {
            if (CODE_ANFRAGE == SCORE_RUECKSETZEN || CODE_ANFRAGE == RUNDE_ERHOEHEN || CODE_ANFRAGE == SCORE_ERHOEHEN)
            {
                this.codeAnfrage = CODE_ANFRAGE;
                setChanged();
                notifyObservers();
            }
        }
    }
}