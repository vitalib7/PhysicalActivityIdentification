package com.example.physicalactivityidentification;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

public class SensorData implements SensorEventListener {

    private Activity activity;
    private SensorManager sensorManager;
    private Sensor accel;
    TextView xValue, yValue, zValue, action;

    public SensorData(Activity activity)
    {
        this.activity = activity;
        Log.d("Now", "Initializing sensors");
        xValue = (TextView)this.activity.findViewById(R.id.xValue);
        yValue = (TextView)this.activity.findViewById(R.id.yValue);
        zValue = (TextView)this.activity.findViewById(R.id.zValue);
        action = (TextView)this.activity.findViewById(R.id.action);
        sensorManager = (SensorManager) this.activity.getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(SensorData.this,accel,sensorManager.SENSOR_DELAY_NORMAL);
        Log.d("Now","Accel has been initialized");
    }


    private void setAction(SensorEvent event)
    {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if(x < 2.5 && x > -2 && y < 6 && y > -3 && z<11 && z > 6)
            action.setText("Not moving");
        else
            action.setText("Moving");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("Main:", "onSensorChanged: X:" + event.values[0] + " Y: " + event.values[1] +
                " Z: " + event.values[2]);

        xValue.setText("xValue: " + event.values[0]);
        yValue.setText("yValue: " + event.values[1]);
        zValue.setText("zValue: " + event.values[2]);
        setAction(event);
    }

}
