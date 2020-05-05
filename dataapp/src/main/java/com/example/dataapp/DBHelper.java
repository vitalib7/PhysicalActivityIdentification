package com.example.dataapp;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class DBHelper{

    private Context context;
    private static final String TAG = "DBHelper";
    private static final String TABLE_NAME = "Action_Table";
    private static final String COL1 = "Trial";
    private static final String COL2 = "Orientation";
    private static final String COL3 = "Activity";
    private static final String COL4 = "Speed";
    private static final String COL5 = "Timestamp";
    private static final String COL6 = "Chest_Accel_X";
    private static final String COL7 = "Chest_Accel_Y";
    private static final String COL8 = "Chest_Accel_Z";
    private static final String COL9 = "User_ID";
    private RequestQueue queue;
    private String url = "https://script.google.com/macros/s/AKfycbwyUTIr6RMxjfIzBdJ9_b9ep7Kzx7ZexpK5bzThA-2CTZjA8r0/exec";
   // private static final String COL8 = "Helmet_Accel_X";
   // private static final String COL9 = "Helmet_Accel_Y";
   // private static final String COL10 = "Helmet_Accel_Z";

    public DBHelper(Context context){
        this.context = context;
        queue =  Volley.newRequestQueue(context);



    }


    /**
     * Send the data to the database
     * @param items
     */
    public void addData( final ArrayList<String> items)
    {

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> contentValues = new HashMap<>();

                //here we pass params
                contentValues.put("action","addItem");
                contentValues.put(COL1,items.get(0));
                contentValues.put(COL2,items.get(1));
                contentValues.put(COL3,items.get(2));
                contentValues.put(COL4,items.get(3));
                contentValues.put(COL5,items.get(4));
                contentValues.put(COL6,items.get(5));
                contentValues.put(COL7,items.get(6));
                contentValues.put(COL8,items.get(7));
                contentValues.put(COL9,items.get(8));
                //contentValues.put(COL8,items.get(9));
                //contentValues.put(COL9,items.get(10));
                //contentValues.put(COL10,items.get(11));

                return contentValues;
            }
        };
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        queue.add(stringRequest);

    }


}
