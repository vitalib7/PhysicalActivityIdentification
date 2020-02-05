package com.example.dataapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    Button btn;
    Spinner spinner;
    Spinner foldSpinner;
    DBHelper dbHelper;
    EditText num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up spinner to select action
        spinner = (Spinner)findViewById(R.id.spinner);
        dbHelper = new DBHelper(this);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.options));

        num = (EditText) findViewById(R.id.editText2);

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        //Set up spinner to select phone orientation
        foldSpinner = (Spinner)findViewById(R.id.spinner2);

        ArrayAdapter<String> foldAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.folded));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foldSpinner.setAdapter(foldAdapter);

        btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Button:","Button Pressed");
                openRecording( spinner.getSelectedItem().toString(),foldSpinner.getSelectedItem().toString(),Integer.parseInt(num.getText().toString()));
            }
        });
    }

    private void openRecording(String action,String orientation,int time)
    {
        Intent intent = new Intent(this, Recording.class);
        intent.putExtra("Action",action);
        intent.putExtra("Orientation",orientation);
        intent.putExtra("Time",time);
        startActivity(intent);
    }

}
