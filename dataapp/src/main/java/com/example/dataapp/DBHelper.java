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

    Context context;
    private static final String TAG = "DBHelper";
    private static final String TABLE_NAME = "Action_Table";
    private static final String COL1 = "User_ID";
    private static final String COL2 = "Orientation";
    private static final String COL3 = "Activity";
    private static final String COL4 = "Timestamp";
    private static final String COL5 = "Chest_Accel_X";
    private static final String COL6 = "Chest_Accel_Y";
    private static final String COL7 = "Chest_Accel_Z";
    RequestQueue queue;
   // private static final String COL8 = "Helmet_Accel_X";
   // private static final String COL9 = "Helmet_Accel_Y";
   // private static final String COL10 = "Helmet_Accel_Z";
    private static ExecutorService threadpool;

    public DBHelper(Context context){
        this.context = context;
        queue =  Volley.newRequestQueue(context);
        threadpool = Executors.newFixedThreadPool(3);



    }



    public void addData( final ArrayList<String> items)
    {
        System.out.println(queue.getSequenceNumber());
        class runTask implements Runnable {
            private int counter;

            @Override
            public void run() {

            }
        }

                StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbwyUTIr6RMxjfIzBdJ9_b9ep7Kzx7ZexpK5bzThA-2CTZjA8r0/exec",
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
                //contentValues.put(COL8,items.get(6));
                //contentValues.put(COL9,items.get(7));
                //contentValues.put(COL10,items.get(8));

                return contentValues;
            }
        };

        int socketTimeOut = 50000;

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);



        queue.add(stringRequest);





    }

    /**
  public void addData( final ArrayList<String> items)
   {
       dbAdder db = new dbAdder(context,items);
       threadpool.submit(db);
   }*/

   public void awaitEnd()
   {

   }

    private static class dbAdder implements Callable {

        private ArrayList<String> items;
        private Context context;

        public dbAdder(Context context, ArrayList<String> items)
        {
            this.items = items;
            System.out.println("copying: " + items.size());
            this.context = context;
        }
        @Override
        public String call() throws Exception {
            System.out.println("calling");
            RequestQueue rq = Volley.newRequestQueue(context);
            String url = "https://script.google.com/macros/s/AKfycbwyUTIr6RMxjfIzBdJ9_b9ep7Kzx7ZexpK5bzThA-2CTZjA8r0/exec";
            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest request = new StringRequest(Request.Method.POST, url, future,future){
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
                    //contentValues.put(COL8,items.get(6));
                    //contentValues.put(COL9,items.get(7));
                    //contentValues.put(COL10,items.get(8));

                    return contentValues;
                }
            };
            rq.add(request);
            String result = "TEmp";
            try {
                result = future.get(); // this line will block
            } catch (Exception e) {
            }
            System.out.println(result);
            return result;
        }
    }











}
