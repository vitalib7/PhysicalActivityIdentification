package com.example.physicalactivityidentification;

import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Poller {

    private static Poller instance;

    public static Poller getInstance()
    {
        if(instance == null)
            instance = new Poller();
        return instance;
    }

    public Poller()
    {

    }
}
