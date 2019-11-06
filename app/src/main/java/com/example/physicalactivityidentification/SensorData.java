package com.example.physicalactivityidentification;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorData implements SensorEventListener {

    Activity activity;
    private SensorManager sensorManager;
    Sensor accel;

    public SensorData(Activity activity)
    {
        this.activity = activity;
        Log.d("Now", "Initializing sensors");

        sensorManager = (SensorManager) this.activity.getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(SensorData.this,accel,sensorManager.SENSOR_DELAY_NORMAL);
        Log.d("Now","Accel has been initialized");
    }

    public int getAcceleration()
    {
        return 0;
    }

    public String getAction()
    {
        return "";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("Main:", "onSensorChanged: X:" + event.values[0] + " Y: " + event.values[1] +
                " Z: " + event.values[2]);
    }
}
