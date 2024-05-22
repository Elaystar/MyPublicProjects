package com.nachname.vorname.fes2dg;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.DataOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;


public class MainActivity extends Activity implements View.OnClickListener, SensorEventListener {



    TextView timestamp;
    TextView y_value;
    TextView counter;
    TextView tagespkte;
    TextView perID;
    TextView FeedbackMessage;
    Button save_data,mit_popup;
    ImageView feedbackLogo;

    private LineChart lineChart;
    private Sensor defaultSensor;
    Long timestampLong = System.currentTimeMillis()/1000;

    private static int score=28800; //1day*60min*60seonds = startscore
    private static String combinedScore ="\nScore"+score;

    private static  LineData data = new LineData();
    private static String personalId="keine Personal ID";
    private static String combinedPersonalID="Nicht eingeloggt\n";

    private static String save_values="";
    private static String uhrzeit;
    private static String datumUhrzeit;
    private static float sumYvalues=0;
    private static int count=0;
    private static int x=0;
    private static float y;
    private String stringXwert;
    private String stringYwert;

    private static String save_correctedValues;
    private static String feedbackMessage;
    private static String feedback;

    private static float yCorrected;
    private static float middleValueY;
    private static float motorStartPeriod=200;
    private static float yBad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mit_popup = (Button) findViewById(R.id.mit_popup);
        mit_popup.setOnClickListener(this);

        timestamp = (TextView) findViewById(R.id.timestamp);
        y_value = (TextView) findViewById(R.id.y_value);
        counter = (TextView) findViewById(R.id.counter);
        save_data = (Button) findViewById(R.id.save_data);
        FeedbackMessage = (TextView) findViewById(R.id.FeedbackMessage);
        feedbackLogo = (ImageView) findViewById(R.id.feedbackLogo);

        save_data.setOnClickListener(this);

        tagespkte = (TextView) findViewById(R.id.txtTagespunkte);

        perID = (TextView) findViewById(R.id.txtPersonalID);
        perID.setText(combinedPersonalID);

        lineChart =(LineChart) findViewById(R.id.lineChart);

        SensorManager sm = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        defaultSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, defaultSensor, SensorManager.SENSOR_DELAY_UI);


        if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            Toast.makeText(this, "Externe SD Karte nicht vorhanden", Toast.LENGTH_LONG).show();
        }
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(this, "SD Karte vorhanden", Toast.LENGTH_LONG).show();
        }

        
        //customize Chart
        lineChart.setDescription("Daten werden in Realtime geladen");

        lineChart.setNoDataTextDescription("Keine Daten im Moment");

        lineChart.setHighlightEnabled(false);

        lineChart.setTouchEnabled(true);

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);

        lineChart.setPinchZoom(false);
         //lineChart.setScaleMinima(1f,1f);
        //lineChart.centerViewPort(x,y);

        // lineChart.setVisibleYRange(20, YAxis.AxisDependency.LEFT);
        lineChart.setBackgroundColor(Color.WHITE);


        data.setValueTextColor(Color.BLUE);

        //add data to linechart
        lineChart.setData(data);

        Legend legende = lineChart.getLegend();

        legende.setForm(Legend.LegendForm.LINE);
        legende.setTextColor(Color.BLUE);

        XAxis xl = lineChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(2);
        xl.setDrawAxisLine(true);
        //  xl.setEnabled(true);

        YAxis yl = lineChart.getAxisLeft();
        yl.setTextColor(Color.BLACK);
        //yl.setAxisMaxValue(30);
        //yl.setAxisMinValue(-30);
        yl.setDrawGridLines(true);
        //yl.setSpaceBottom(50);
        yl.setStartAtZero(false);

        YAxis yl2 = lineChart.getAxisRight();
        yl2.setEnabled(false);


    }//oncreate ende



    @Override
    protected void onResume() {



        super.onResume();
        //now simulate real time data addition

        new Thread(new Runnable() {

            @Override
            public void run() {

                while (true){

                    runOnUiThread(new Runnable() {

                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {
                            // chart is notified to update in addEntry method
                            addEntry(y);
                        }
                    });

                    //pause between adds
                    try {
                        Thread.sleep(100); // 7 changes per second in middle =1000/7= 142 milliseconds wait for change maximum. here 100ms =10changes per second
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


@TargetApi(Build.VERSION_CODES.N)
@RequiresApi(api = Build.VERSION_CODES.N)
public static String getCurrentTimeStamp(boolean datum){
    try {
//yyyy-MM-dd
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd-HH-mm-ss");
        String currentDateTime = dateFormat.format(new Date()); // Find todays date

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH-mm-ss");
        String currentTime = timeFormat.format(new Date()); // Find todays date

if(datum==true) {
    return currentDateTime;
}else{
    return currentTime;
}
    } catch (Exception e) {
        e.printStackTrace();

        return null;
    }
}

    //add entry to linechart
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addEntry(float acceleration){ 

        //objekt mit daten für Linechart
        LineData data = lineChart.getData();

        if (data != null){
            LineDataSet set = data.getDataSetByIndex(0);

            if(set== null){
                set=createSet();
                data.addDataSet(set);
            }
            //add a new x value
            x=data.getXValCount();
            stringXwert=""+x;
           // data.addXValue(xwert);


            //String timestampString = timestampLong.toString();
            //data.addXValue(timestampString);

            //oder zeit über fktion getCurrentTimeStamp
            data.addXValue(getCurrentTimeStamp(false));
            uhrzeit= getCurrentTimeStamp(false);
            timestamp.setText(uhrzeit+" Local Time");
            counter.setText(stringXwert+"th value");
            data.addEntry(new Entry(acceleration, set.getEntryCount()),0);
        }

        //notify chart data has changed
        lineChart.notifyDataSetChanged();
        data.notifyDataChanged();

        //limit number of visible entries
        lineChart.setVisibleXRange(20);
        //lineChart.setVerticalScrollBarEnabled(true);
        //lineChart.setVerticalScrollbarPosition(0);


         /*This will restrain the view on the x-axis and always show exactly 10 values.
        Then you can set where your view should aim at by calling
        moveViewToX(float xValue): Moves the left side (edge) of the current viewport to the specified x-value.
         */
        lineChart.setScaleMinima(x / 40f, 1f);
        lineChart.moveViewToX(data.getXValCount()-39);


        //lineChart.moveViewToX(x);
       // save_values=save_values+"<CounterValue: "+stringXwert+"; Timestamp: "+uhrzeit+"#; Y-Value (acceleration): "+stringYwert+"*>\n";
    }


    //method to create set
    private LineDataSet createSet(){

        LineDataSet set= new LineDataSet(null, "FES Beschleunigung/Zeit");

        set.setDrawCubic(true);
        set.setCubicIntensity(0.2f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.RED);
        set.setLineWidth(1f);
        set.setCircleSize(1f);
       // set.setFillAlpha(65);
        set.setFillColor(Color.GREEN);
        set.setHighLightColor(Color.rgb(0,0,255));
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(7f);

        return set;
    }

    @Override
    public void onClick(View v) {


        if (v == mit_popup) {

            // Create Object of Dialog class
            final Dialog login = new Dialog(this);
            // Set GUI of login screen
            login.setContentView(R.layout.login_dialog);
            login.setTitle("Login to FES");

            // Init button of login GUI
            Button btnLogin = (Button) login.findViewById(R.id.btnLogin);
            Button btnCancel = (Button) login.findViewById(R.id.btnCancel);
            final EditText txtUsername = (EditText)login.findViewById(R.id.txtUsername);
            final EditText txtPassword = (EditText)login.findViewById(R.id.txtPassword);


            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(txtUsername.getText().toString().trim().length() > 0 && txtPassword.getText().toString().trim().length() > 0)
                    {
                        //personalId=txtUsername.getText().toString().trim().length();
                        // Validate Your login credential here than display message
                        Toast.makeText(MainActivity.this,
                                "Login erfolgreich", Toast.LENGTH_LONG).show();

                        personalId=txtUsername.getText().toString();
                        combinedPersonalID="Personal ID: "+personalId+"\n";
                        perID.setText(combinedPersonalID);
                        // Redirect to dashboard / home screen.
                        login.dismiss();
                    }

                    else
                    {
                        Toast.makeText(MainActivity.this,
                                "Bitte Personal-ID und Passwort eingeben", Toast.LENGTH_LONG).show();

                    }
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login.dismiss();
                }
            });

            // Make dialog box visible.
            login.show();
        }


        if(v.equals(save_data)){

            saveDataValues();
            Toast.makeText(this, "Daten gespeichert in Datei:\n FES_data_Datum-Uhrzeit.csv", Toast.LENGTH_LONG).show();


           
            new AlertDialog.Builder(this)
                    .setMessage("Sind Sie sicher?")
                    .setCancelable(false)
                    .setPositiveButton("JA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("NEIN",null)
                    .show();
            //finish();
            //System.exit(0);

            /* try {
                saveObj();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }*/
        }
        }



        @RequiresApi(api = Build.VERSION_CODES.N)
        private void saveDataValues(){// throws IOException {
 //<uses-sdk android:minSdkVersion="8"/>

            File inDocuments =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File externalSDCard= Environment.getExternalStorageDirectory();
            File inFES =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            String combinedScore ="Score"+score;
            try {
        FileOutputStream fos;
        DataOutputStream dos;
        File f = this.getFilesDir();
        String s = f.getCanonicalPath();

            datumUhrzeit= getCurrentTimeStamp(true);
        File file = new File(getExternalFilesDir(null), "FES_data_"+datumUhrzeit+".csv");

        file.createNewFile();
        fos = new FileOutputStream(file);
        dos = new DataOutputStream(fos);

                dos.write("RecordStartOfSaveSession\n".getBytes());
                dos.write(combinedPersonalID.getBytes());
                dos.write(combinedScore.getBytes());
                dos.write(save_values.getBytes());
                dos.write("\nRecordEndOfSaveSession\n".getBytes());
                fos.close();
                dos.close();

    }catch(Exception e){
        Toast.makeText(this, "Daten wurden nicht in Datei gespeichert!", Toast.LENGTH_LONG).show();
        Log.e("Exception", "File write failed: " + e.toString());
    }


/*
           File folderFES = new File(inFES, "FES");
            folderFES.mkdir();

          File file= new File(folderFES,"/FES_data.dat");
            //file.createNewFile();
            //OutputStream os;

                os = new FileOutputStream(file);
                os.write(save_values.getBytes());
                os.flush();
                os.close();
                Toast.makeText(this, "Saved to file", Toast.LENGTH_LONG).show();

            // dos.writeChars("endoffile");
                //File file= new File(test + "/fesdata.txt");


              // file.mkdir();

            String state = Environment.getExternalStorageState();
            if(state.equals(Environment.MEDIA_MOUNTED)){

              File inDocuments =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                   File externalSDCard= Environment.getExternalStorageDirectory();

            }

                String filename="FES_data.dat";
                String teststring="hallo";
                FileOutputStream outputStream;

            File file = new File(getFilesDir(), filename);
            BufferedWriter writer;
                try {
                    writer = new BufferedWriter(new FileWriter(file));
                    writer.write("hallo");
                    writer.write(save_values);
                    writer.write("_end");
                    writer.flush();
                    writer.close();

                    outputStream= openFileOutput(filename, Context.MODE_PRIVATE);
                    //save_values
                    outputStream.write(teststring.getBytes());
                    outputStream.close();

                OutputStreamWriter outStrWr = new OutputStreamWriter(fos);
                    outStrWr.write("hallo2");
                outStrWr.write(save_values);
                outStrWr.close();
                fos.close();
*/
        }



    private void saveObj() throws IOException, ClassNotFoundException {

        final String FILE = "userdata";
        Context context = null;
        FileOutputStream fos = context.openFileOutput(FILE, context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(data);
        os.close();
        fos.close();
        /*
        FileOutputStream fos = openFileOutput(&quot;sdcard/sdtext.txt&quot;, MODE_WORLD_WRITEABLE);
// Reading it back..
FileInputStream fis = openFileInput(&ldquo;sdcard/sdtext.txt&rdquo;);
         */
    }

   
    private void loadObj() throws IOException, ClassNotFoundException {
       // Auslesen kann man die Datei mittels:
        final String FILE = "userdata";
        Context context = null;
        FileInputStream fis = context.openFileInput(FILE);
        ObjectInputStream is = new ObjectInputStream(fis);
        LineChart data = (LineChart) is.readObject();
        is.close();
        fis.close();
    }


    
    @Override
    public void onSensorChanged(SensorEvent event) {

      //  addEntry(event.values[2]);
        y=event.values[2];
        stringYwert=Float.toString(y);
        y_value.setText(stringYwert+" m/s\u00B2");
        //save_values=save_values+"<EntryCounter X: /"+stringXwert+"/c>; <Timestamp: /"+uhrzeit+"/#>; <Y (acceleration): /"+stringYwert+"/*>;\n";
        save_values=save_values+"\n"+stringXwert+","+uhrzeit+","+stringYwert+","+feedback+"\n";
count++;
        if(count<motorStartPeriod){
            sumYvalues=(sumYvalues+y);
            Log.d("summwert", String.valueOf(sumYvalues));
        }

        middleValueY =sumYvalues/motorStartPeriod;
        Log.d("mittelwert", String.valueOf(middleValueY));

        yCorrected=y-middleValueY;
        save_correctedValues=save_correctedValues+"\n"+stringXwert+","+uhrzeit+","+yCorrected+"\n";

        if (yCorrected<=1 && yCorrected>=-1)
        {
            feedback="GOOD";
            feedbackMessage="Dankeschön für das sparsame Fahren.";
            Drawable myGreen =getResources().getDrawable(R.drawable.green_neu);
            feedbackLogo.setImageDrawable(myGreen);
            //score++;
            tagespkte.setText("Tagespunkte: "+score);
        }
        if (yCorrected>1 && yCorrected<=2.5 || yCorrected<-1 && yCorrected>=-2.5)
        {
            feedback="OK";
            feedbackMessage="Sie fahren ganz normal. Alles ist gut.";
           // feedbackLogo.setImageResource(R.drawable.yellow);
           Drawable myYellow =getResources().getDrawable(R.drawable.yellow_neu);
           feedbackLogo.setImageDrawable(myYellow);
            tagespkte.setText("Tagespunkte: "+score);
            score--;
        }
        if (yCorrected>2.5 || yCorrected<-2.5)        {
            feedback="BAD";
            feedbackMessage="Bitte vorsichtiger und sparsamer fahren!";
            //feedbackLogo.setImageResource(R.drawable.red);
            Drawable myRed =getResources().getDrawable(R.drawable.red_neu);
            feedbackLogo.setImageDrawable(myRed);
            yBad=yCorrected;
            score=score-10;
            tagespkte.setText("Tagespunkte: "+score);

        }

        FeedbackMessage.setText(feedbackMessage);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
      /*  SensorManager sm = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        defaultSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, defaultSensor, SensorManager.SENSOR_DELAY_NORMAL);
        */
    }
}
