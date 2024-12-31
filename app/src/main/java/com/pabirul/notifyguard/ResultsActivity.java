package com.pabirul.notifyguard;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Initialize the AdwareScanner
        AdwareScanner adwareScanner = new AdwareScanner(this);

        // Get a list of adware apps
        List<String> adwareApps = adwareScanner.scanInstalledApps();

        // Initialize the app list for the RecyclerView
        List<ResultAppInfo> appList = new ArrayList<>();
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        // Iterate through installed apps
        for (ApplicationInfo app : installedApps) {
            String appName = packageManager.getApplicationLabel(app).toString();
            String packageName = app.packageName;
            Drawable icon = packageManager.getApplicationIcon(app);

            // Check if the app is flagged as potential adware
            boolean isAdware = adwareApps.contains(appName);

            if (isAdware) { // Only add flagged apps to the list
                appList.add(new ResultAppInfo(appName, packageName, icon, true));
            }
        }

        // Set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.resultsrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ResultsAdapter adapter = new ResultsAdapter(appList); // Pass only flagged apps
        recyclerView.setAdapter(adapter);
    }
}
