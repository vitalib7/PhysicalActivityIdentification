package com.example.dataapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.security.spec.ECField;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Recording extends AppCompatActivity implements SensorEventListener {

    private TextView timer;
    private Integer cur;
    private String action;
    private String orientation;
    private Integer record_length;
    private HandlerThread mSensorThread;
    private Handler mSensorHandler;
    private SensorManager sensorManager;
    private Sensor accel;
    private List<String> data =
            Collections.synchronizedList(new ArrayList<String>());
    private DBHelper dbHelper;
    private Date date;
    private String id;
    private String speed;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(this);
        setContentView(R.layout.activity_recording);
        Asynch runner = new Asynch(this);
        runner.execute();
        timer = (TextView) findViewById(R.id.textView3);
        cur = 3;
        timer.setText(cur.toString());
        action = getIntent().getStringExtra("Action");
        orientation = getIntent().getStringExtra("Orientation");
        record_length = getIntent().getIntExtra("Time",15);
        speed = getIntent().getStringExtra("Speed");
        Log.d("Recording",action + " " + orientation + record_length);
        StartTimer();

    }

    /**
     * Convert sensor speed to correct unit
     * @return
     */
    private int getSpeed()
    {
        if(speed.contains("Slow"))
            return sensorManager.SENSOR_DELAY_NORMAL;
        else if(speed.contains("Fast"))
            return sensorManager.SENSOR_DELAY_FASTEST;
        else
            return sensorManager.SENSOR_DELAY_UI;
    }

    /**
     * Starts timer in thread separate from sensor
     */
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
                    //After 3 seconds, run the sensor
                    synchronized (data) {
                        runSensor();
                    }
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
        synchronized (data) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                Log.d("Sensor", "sensor changed");
                //time = new Timestamp(System.currentTimeMillis());
                date = new Date();
                data.add(Long.toString(date.getTime()));
                data.add(String.valueOf(event.values[0]));
                data.add(String.valueOf(event.values[1]));
                data.add(String.valueOf(event.values[2]));
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Starts the sensor on new handler thread
     */
    private void runSensor()
    {

        synchronized (data) {
            sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
            accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorThread = new HandlerThread("Sensor thread", Thread.MAX_PRIORITY);
            mSensorThread.start();
            mSensorHandler = new Handler(mSensorThread.getLooper()); //Blocks until looper is prepared, which is fairly quick
            sensorManager.registerListener(this, accel, getSpeed(), mSensorHandler);
            //after 15 seconds, turn off the sensors
            mSensorHandler.postDelayed(new Runnable() {
                public void run() {
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
                    Log.d("data:", data.toString());
                }
            }, record_length * 1000);
        }
    }

    /**
     * Prompt user to add to database
     */
    private void userReview()
    {
        synchronized (data) {
            Log.d("Cur ID", id + "");
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(Recording.this);
            mBuilder.setIcon(android.R.drawable.sym_def_app_icon);
            mBuilder.setTitle("Save Data?");
            mBuilder.setMessage("Would you like to save this data to the database? " + "\nSize: " + data.size() / 4);
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
    }

    /**
     * Add data to google sheets db
     */
    private void addToDatabase()
    {
        synchronized (data) {
            int newId = 0;
            try {
                 newId = Integer.parseInt(id) + 1; //get previous userID and add 1
            }
            catch(NumberFormatException e)
            {
                newId = 0; //If error, then db is empty and userID is 0
            }
            Log.d("Added: ", Integer.toString(newId));


            for (int i = 1; i < data.size() / 4; i++) {
                //Add the corresponding values to the db row
                ArrayList<String> temp = new ArrayList<>();
                temp.add(String.valueOf(newId));
                temp.add(orientation);
                temp.add(action);
                temp.add(speed);
                temp.add(data.get((i - 1) * 4));
                temp.add(data.get(1 + ((i - 1) * 4)));
                temp.add(data.get(2 + ((i - 1) * 4)));
                temp.add(data.get(3 + ((i - 1) * 4)));


                dbHelper.addData(temp);

                Log.d("Added: ", Integer.toString(i));

            }


        }




    }

    /**
     * Get previous userID in background
     */
    private class Asynch extends AsyncTask<Void,Void,String> {
        Context context;
        String result;

        Asynch(Context context)
        {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestQueue rq = Volley.newRequestQueue(context);
            String url = "https://script.google.com/macros/s/AKfycbwyUTIr6RMxjfIzBdJ9_b9ep7Kzx7ZexpK5bzThA-2CTZjA8r0/exec?action=getId";
            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest request = new StringRequest(Request.Method.GET, url, future, future);
            rq.add(request);
            String result = "TEmp";
            try {
                result = future.get(); // this line will block
            } catch (Exception e) {
            }
            id = result;
            this.result = id;
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            id = this.result;
        }
    }




    }

