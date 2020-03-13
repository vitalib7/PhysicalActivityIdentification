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
    Spinner speedSpinner;
    EditText num;
    EditText id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up spinner to select action
        spinner = (Spinner)findViewById(R.id.spinner);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.options));

        num = (EditText) findViewById(R.id.editText2);
        id = (EditText) findViewById(R.id.editText);

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        //Set up spinner to select phone orientation
        foldSpinner = (Spinner)findViewById(R.id.spinner2);





        //Set up spinner to select speed
        speedSpinner = (Spinner)findViewById(R.id.spinner3);

        ArrayAdapter<String> speedAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Speed));

        speedSpinner.setAdapter(speedAdapter);

        ArrayAdapter<String> foldAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.folded));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foldSpinner.setAdapter(foldAdapter);

        btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Button:","Button Pressed");
                openRecording( spinner.getSelectedItem().toString(),foldSpinner.getSelectedItem().toString(),
                        speedSpinner.getSelectedItem().toString(),Integer.parseInt(num.getText().toString()),id.getText().toString());
            }
        });
    }

    /**
     * Open the recording screen
     * @param action
     * @param orientation
     * @param speed
     * @param time
     */
    private void openRecording(String action,String orientation, String speed, int time, String id)
    {
        Intent intent = new Intent(this, Recording.class);
        intent.putExtra("Action",action);
        intent.putExtra("Orientation",orientation);
        intent.putExtra("Time",time);
        intent.putExtra("Speed", convertString(speed));
        intent.putExtra("ID", id);
        startActivity(intent);
    }

    /**
     * Convert speed to simpler string to send to new activity
     * @param speed
     * @return
     */
    private String convertString(String speed)
    {
        if(speed.contains("Slowest") )
            return "Slow";
        else if(speed.contains("Fastest"))
            return "Fast";
        else
            return "Average";
    }

}
