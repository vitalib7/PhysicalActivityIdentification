package com.example.dataapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String TABLE_NAME = "Action_Table";
    private static final String COL1 = "User_ID";
    private static final String COL2 = "Orientation";
    private static final String COL3 = "Activity";
    private static final String COL4 = "Timestamp";
    private static final String COL5 = "Chest_Accel_X";
    private static final String COL6 = "Chest_Accel_Y";
    private static final String COL7 = "Chest_Accel_Z";
   // private static final String COL8 = "Helmet_Accel_X";
   // private static final String COL9 = "Helmet_Accel_Y";
   // private static final String COL10 = "Helmet_Accel_Z";

    public DBHelper(Context context)
    {
        super(context, TABLE_NAME, null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" (\n"
                + "    User_ID integer PRIMARY KEY AUTOINCREMENT,\n"
                + "    Orientation text NOT NULL,\n"
                + "    Activity text NOT NULL,\n"
                + "    Timestamp text NOT NULL,\n"
                + "    Chest_Accel_X real NOT NULL,\n"
                + "    Chest_Accel_Y real NOT NULL,\n"
                + "    Chest_Accel_Z real NOT NULL\n"
              //  + "    Helmet_Accel_X real NOT NULL,\n"
               // + "    Helmet_Accel_Y real NOT NULL,\n"
               // + "    Helmet_Accel_Z real NOT NULL\n"
                + ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME );
        onCreate(db);
    }

    public boolean addData(ArrayList<String> items)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2,items.get(0));
        contentValues.put(COL3,items.get(1));
        contentValues.put(COL4,items.get(2));
        contentValues.put(COL5,Float.valueOf(items.get(3)));
        contentValues.put(COL6,Float.valueOf(items.get(4)));
        contentValues.put(COL7,Float.valueOf(items.get(5)));
        //contentValues.put(COL8,items.get(6));
        //contentValues.put(COL9,items.get(7));
        //contentValues.put(COL10,items.get(8));
        Log.d(TAG,"addData: Adding " + items.toString() + " to " + TABLE_NAME);
        long result = db.insert(TABLE_NAME,null,contentValues);
        if( result == -1)
            return false;
        else
            return true;
    }
}
