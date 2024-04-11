package com.example.domg.simonhmi;

import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Build;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Button;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.widget.Toast;
import android.view.ViewGroup;

import java.util.List;
import java.util.Observer;
import java.util.Observable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    final String name = MainActivity.class.getSimpleName();

    Button b;
    static MediaPlayer mp;
    static MediaPlayer mpzonk;


    @Override
    protected void onCreate(Bundle aktuellenZustandSpeichern)
    {
        super.onCreate(aktuellenZustandSpeichern);
               setContentView(R.layout.activity_main);
    }

/*
    @Override
    public void onResume (){
        super.onResume();
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.r2d2);
        b =(Button)findViewById(R.id.buttonBlue);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mp.start();
               // startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });

    }
*/
    @Override
    protected void onStart() {
        super.onStart();
        final MediaPlayer mpzonk = MediaPlayer.create(this, R.raw.zonk);

        ScoreFragment scoreFragment = (ScoreFragment) getSupportFragmentManager().findFragmentById(R.id.scoreleiste_fragment);
        hauptFragment boardFragment = (hauptFragment) getSupportFragmentManager().findFragmentById(R.id.brett_fragment);
        boardFragment.getBeobachterScore().addObserver(scoreFragment);

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.knightrider);
        mp.start();
       // b =(Button)findViewById(R.id.action_begin);
       /*
        b.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view) {
                mp.stop();
                // startActivity(new Intent(MainActivity.this, MainActivity.class));

            }
        }); */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu hauptmenue) {

        getMenuInflater().inflate(R.menu.main, hauptmenue);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem gegenstand)
    {
        switch (gegenstand.getItemId())
        {
            case R.id.start_spiel:
                try {
                    ((hauptFragment) getSupportFragmentManager().findFragmentById(R.id.brett_fragment)).start();
                } catch (Exception e) {
                    Log.e(name, "Fehler start", e);
                }
                break;
            case R.id.hilfe:
                final AlertDialog.Builder ersteller = new AlertDialog.Builder(this);
                ersteller.setTitle(R.string.help_title);
                ersteller.setMessage(R.string.hilfeText);
                ersteller.setPositiveButton(android.R.string.ok, null);
                ersteller.show();
                break;
            case R.id.konfigurieren:
                Intent absicht = new Intent(this, ActivityEinstellungen.class);
                startActivity(absicht);
                break;
        }
        return super.onOptionsItemSelected(gegenstand);
    }

    public static class ActivityEinstellungen extends PreferenceActivity
    {
        final String TAG = ActivityEinstellungen.class.getSimpleName();
        @Override
        protected void onCreate(Bundle aktuellenStandSpeichern)
        {
            super.onCreate(aktuellenStandSpeichern);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            {
                PreHoneycombZeigeVorzuege();
            }
            else
            {
                FragmentStyleZeigeVorzuege(aktuellenStandSpeichern);
            }
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        private void FragmentStyleZeigeVorzuege(Bundle zustandStatusSpeichern)
        {
            if (zustandStatusSpeichern == null)
            {
                FragmentTransaction uerbertrag = getFragmentManager()
                        .beginTransaction();
                android.app.Fragment vorzug = new MeinFragmentBevorzugt();
                uerbertrag.replace(android.R.id.content, vorzug);
                uerbertrag.commit();
            }
        }

        @SuppressWarnings("deprecation")
        private void PreHoneycombZeigeVorzuege()
        {
            addPreferencesFromResource(R.xml.preferences);
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public static class MeinFragmentBevorzugt extends PreferenceFragment
        {
            final String name4 = MeinFragmentBevorzugt.class.getSimpleName();

            @Override
            public void onAttach(Activity aktivitaet)
            {
                super.onAttach(aktivitaet);
            }

            @Override
            public View onCreateView(LayoutInflater vergroesserer, ViewGroup behaelter,
                                     Bundle aktuellenZustandSpeichern)
            {
                this.addPreferencesFromResource(R.xml.preferences);
                return super.onCreateView(vergroesserer, behaelter, aktuellenZustandSpeichern);
            }
        }
    }

    public static class hauptFragment extends Fragment implements View.OnClickListener, Simon.hauptHost
    {
        final int DAUER_ABSPIELEN_IN_MS = 600;
        final int DAUER_PAUSE_IN_MS = 200;
        private final Handler spielBenutzer = new Handler();
        private String name2 = hauptFragment.class.getSimpleName();
        private List<View> farbenSimon;
        private Simon simon;

        @Override
        public void onCreate(Bundle aktuellenZustandSpeichern)
        {
            super.onCreate(aktuellenZustandSpeichern);
            setRetainInstance(true);
        }

        @Override
        public View onCreateView(LayoutInflater vergroesserer, ViewGroup behaelter,
                                 Bundle aktuellenZustandSpeichern)
        {
            super.onCreateView(vergroesserer, behaelter, aktuellenZustandSpeichern);
            // Vergrössert das Format für das Fragment
            SharedPreferences vorzuege = PreferenceManager.getDefaultSharedPreferences(getActivity());
            boolean brettGross = vorzuege.getBoolean(getString(R.string.schluesselGrossesBrett), false);
            return brettGross ? vergroesserer.inflate(R.layout.brett_3x2, behaelter, false) : vergroesserer.inflate(R.layout.brett_2x2, behaelter, false);
        }

        @Override
        public void onActivityCreated(Bundle aktuellenZustandSpeichern)
        {
            super.onActivityCreated(aktuellenZustandSpeichern);
            if (!belege(getView()))
            {
                Log.e(name2, "Simon nicht verfuegbar");
                Toast.makeText(getActivity(), getActivity().getString(R.string.initialisierungsfehler), Toast.LENGTH_SHORT).show();
            }
        }

        //reagiert auf Farbtasten drücken
        public void onClick(View view)
        {
            //mp.stop();
            byte farbeIndex = (byte) farbenSimon.indexOf(view);
            try {
                simon.farbeGedrueckt(farbeIndex);
            } catch (Exception e) {
                Log.e(name2, "Simon nicht verfügbar", e);
            }

        }

        void start()
        {
            spielBenutzer.removeCallbacksAndMessages(null);
            try {
                simon.anfang();
            } catch (Exception e) {
                Log.e(name2, "Simon nicht verfügbar", e);
            }
        }

        public void nachrichtSpiel(byte CODE_NACHRICHT)
        {
            String[] nachrichtSpielbrett = getActivity().getResources().getStringArray(R.array.brettNachrichten);
            Toast.makeText(getActivity(), nachrichtSpielbrett[CODE_NACHRICHT], Toast.LENGTH_SHORT).show();

            mpzonk.start();
        }

        public void spielZuende()
        {
            spielBenutzer.removeCallbacksAndMessages(null);
        }

        Simon.BeobachterScore getBeobachterScore()
        {
            return simon.getBeobachterScore();
        }

        public boolean belege(View viewBegin)
        {
            try {

                farbenSimon = getKeinLayoutKinder(viewBegin);
            } catch (Exception e) {
                Log.e(name2, "Fehler beim erstellen von Farbarray", e);
                return false;
            }
            for (View kachelSimon : farbenSimon) {
                kachelSimon.setOnClickListener(this);
            }
            try {
                if (simon == null) this.simon = new Simon((byte) farbenSimon.size(), this);
            } catch (Exception e) {
                Log.e(name2, "Fehler beim initialisieren von simon");
                return false;
            }
            return true;
        }

        public boolean abspiel(List<Byte> farblisteSpielen)
        {
            int delayMultiplier = 1;
            if (farblisteSpielen == null) return false;

            for (final Byte indexFarbe : farblisteSpielen)
            {
                spielBenutzer.postDelayed(new Runnable() {
                    public void run() {
                        einAbspiel(indexFarbe);
                    }
                }, delayMultiplier * (DAUER_ABSPIELEN_IN_MS + DAUER_PAUSE_IN_MS));
                delayMultiplier++;
            }
            return true;
        }

        public void einAbspiel(byte indexFarbe)
        {
            final View viewAbspiel = farbenSimon.get(indexFarbe);
            viewAbspiel.setPressed(true);
            viewAbspiel.postDelayed(new Runnable() {
                public void run() {
                    viewAbspiel.setPressed(false);
                }
            }, DAUER_ABSPIELEN_IN_MS);
        }

        private List<View> getKeinLayoutKinder(View viewKeinLayout)
        {
            List<View> aufgerufen = new ArrayList<View>();
            List<View> unaufgerufen = new ArrayList<View>();
            unaufgerufen.add(viewKeinLayout);

            while (!unaufgerufen.isEmpty())
            {
                View kind = unaufgerufen.remove(0);
                if (!(kind instanceof ViewGroup))
                {
                    aufgerufen.add(kind);
                    continue;
                }
                ViewGroup gruppe = (ViewGroup) kind;
                final int zahlKinder = gruppe.getChildCount();
                for (int i = 0; i < zahlKinder; i++) unaufgerufen.add(gruppe.getChildAt(i));
            }
            return aufgerufen;
        }
    }

    public static class ScoreFragment extends Fragment implements Observer
    {
        private String name3 = ScoreFragment.class.getSimpleName();

        public void onCreate(Bundle aktuellerZustand) {
            super.onCreate(aktuellerZustand);
        }

        @Override
        public View onCreateView(LayoutInflater vergroesserer, ViewGroup behaelter,
                                 Bundle aktuellenZustandSpeichern)
        {
            return vergroesserer.inflate(R.layout.score_fragment, behaelter, false);
        }

        public void update(Observable beobachter, Object objekt)
        {
            if (beobachter instanceof Simon.BeobachterScore)
            {
                byte anfrageScore = ((Simon.BeobachterScore) beobachter).getAnfrage();
                switch (anfrageScore)
                {
                    case Simon.BeobachterScore.SCORE_RUECKSETZEN:
                        zuruecksetzenScore();
                        break;
                    case Simon.BeobachterScore.RUNDE_ERHOEHEN:
                        rundeErhoehen();
                        break;
                    case Simon.BeobachterScore.SCORE_ERHOEHEN:
                        scoreErhoehen();
                        break;
                    default: //nichts
                }
            }
        }

        private void rundeErhoehen()
        {
            try {
                TextView textviewRunde = (TextView) getView().findViewById(R.id.rundeid);
                int runde = Integer.parseInt(textviewRunde.getText().toString());
                runde++;
                textviewRunde.setText(Integer.toString(runde));
            } catch (Exception e) {
                Log.w(name3, "Fehler bei Runde", e);
            }
        }

        private void scoreErhoehen()
        {
            try {
                TextView zeigeScore = (TextView) getView().findViewById(R.id.scoreid);
                if (zeigeScore == null) Log.w(name3, "zeigeScore=null");
                int punktestand = Integer.parseInt(zeigeScore.getText().toString());
                punktestand++;
                zeigeScore.setText(Integer.toString(punktestand));
            } catch (Exception e) {
                Log.w(name3, "Fehler beim Score", e);
            }
        }

        private void zuruecksetzenScore()
        {
            try {
                TextView textviewPunktestand = (TextView) getView().findViewById(R.id.scoreid);
                textviewPunktestand.setText(Integer.toString(0));
                TextView textviewRunde = (TextView) getView().findViewById(R.id.rundeid);
                textviewRunde.setText(Integer.toString(0));
            } catch (Exception e) {
                Log.w(name3, "Fehler beim reset von Runde oder Score", e);
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder beenden = new AlertDialog.Builder(MainActivity.this);
        beenden.setTitle("Simon HMI");
        beenden.setMessage("Möchten Sie Simon schließen?");
        beenden.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);

                MainActivity.this.finish();
            }
        });
        beenden.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        beenden.show();
    }

}