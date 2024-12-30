package com.pabirul.notifyguard;


import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        listView = findViewById(R.id.listView);

        // Retrieve the scan results passed from MainActivity
        List<String> adwareApps = getIntent().getStringArrayListExtra("adwareApps");

        if (adwareApps != null && !adwareApps.isEmpty()) {
            // Display the flagged apps in the ListView
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, adwareApps);
            listView.setAdapter(adapter);
        } else {
            // Show a toast if no adware is detected
            Toast.makeText(this, "No Adware Detected!", Toast.LENGTH_LONG).show();
        }
    }
}
