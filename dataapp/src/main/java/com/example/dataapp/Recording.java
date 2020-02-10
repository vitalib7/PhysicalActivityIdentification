package com.example.dataapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class Recording extends AppCompatActivity implements SensorEventListener {

    TextView timer;
    Integer cur;
    String action;
    String orientation;
    Integer record_length;
    private HandlerThread mSensorThread;
    private Handler mSensorHandler;
    private SensorManager sensorManager;
    private Sensor accel;
    private ArrayList<String> data;
    private Timestamp time;
    DBHelper dbHelper;
    private Date date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(this);
        data = new ArrayList<String>();
        setContentView(R.layout.activity_recording);
        timer = (TextView) findViewById(R.id.textView3);

        cur = 3;
        timer.setText(cur.toString());
        action = getIntent().getStringExtra("Action");
        orientation = getIntent().getStringExtra("Orientation");
        record_length = getIntent().getIntExtra("Time",15);
        Log.d("Recording",action + " " + orientation + record_length);

        StartTimer();



    }

    private void StartTimer()
    {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (cur) {

                        while (cur > 1) {
                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cur -= 1;
                                    if (cur == 0)
                                        timer.setText("Recording Data ");
                                    else
                                        timer.setText(cur.toString());
                                }
                            });
                        }
                    }
                    runSensor();
                    while (cur<record_length) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cur +=1;
                                timer.setText("Recording Data\n" + cur.toString());

                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("Sensor","sensor changed");
        //time = new Timestamp(System.currentTimeMillis());
        date = new Date();
        data.add(Long.toString(date.getTime()));
        data.add(String.valueOf(event.values[0]));
        data.add(String.valueOf(event.values[1]));
        data.add(String.valueOf(event.values[2]));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void runSensor()
    {
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorThread = new HandlerThread("Sensor thread", Thread.MAX_PRIORITY);
        mSensorThread.start();
        mSensorHandler = new Handler(mSensorThread.getLooper()); //Blocks until looper is prepared, which is fairly quick
        sensorManager.registerListener(this,accel,sensorManager.SENSOR_DELAY_NORMAL,mSensorHandler);
        //after 15 seconds, turn off the sensors
        mSensorHandler.postDelayed(new Runnable () {
            public void run () {
                sensorManager.unregisterListener(Recording.this);
                mSensorThread.quitSafely();
                //Need to call this on main thread because this thread dies
                Handler mHandler = new Handler(getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        userReview();
                    }
                });
                Log.d("data:",data.toString());
            }
        }, record_length * 1000);
    }

    private void userReview()
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Recording.this);
        mBuilder.setIcon(android.R.drawable.sym_def_app_icon);
        mBuilder.setTitle("Save Data?");
        mBuilder.setMessage("Would you like to save this data to the database?");
        mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addToDatabase();
                dialog.dismiss();
                Intent intent = new Intent(Recording.this, MainActivity.class);
                startActivity(intent);
            }
        });
        mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Recording.this, MainActivity.class);
                startActivity(intent);
            }
        });
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void addToDatabase()
    {
        boolean success = true;

        for(int i = 1; i < data.size()/4;i++)
        {
            //Add the corresponding values to the db row
            ArrayList<String> temp = new ArrayList<>();
            temp.add(orientation);
            temp.add(action);
            temp.add(data.get((i-1) * 4));
            temp.add(data.get(1+ ((i-1) * 4)));
            temp.add(data.get(2+ ((i-1) * 4)));
            temp.add(data.get(3+ ((i-1) * 4)));


            boolean insert = dbHelper.addData(temp);
            try{
                Thread.sleep(200);
            }
            catch(InterruptedException e)
            {

            }
            Log.d("Added: ",Integer.toString(i));
            if(!insert)
                success = false;
        }
        if(success)
            Toast.makeText(Recording.this,"Successfully Inserted data",Toast.LENGTH_SHORT);
        else
            Toast.makeText(Recording.this,"Error Inserting Data",Toast.LENGTH_SHORT);

    }
}
