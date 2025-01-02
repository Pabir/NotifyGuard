package com.pabirul.notifyguard;

import android.content.pm.ApplicationInfo;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;

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
        
        // Fetch the watchlist
        List<WatchlistAppInfo> watchlist = WatchlistManager.getWatchlist();

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
    
        // Check if the app is already in the watchlist
        boolean isInWatchlist = false;
        for (WatchlistAppInfo watchlistApp : watchlist) {
            if (watchlistApp.getPackageName().equals(packageName)) {
                isInWatchlist = true;
                break;
            }
        }

        // Only add flagged apps that are not in the watchlist
        if (isAdware && !isInWatchlist) {
            appList.add(new ResultAppInfo(appName, packageName, icon, true));
        }
    }
    
        // Set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.resultsrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ResultsAdapter adapter = new ResultsAdapter(this, appList);
        recyclerView.setAdapter(adapter);

        // Navigate to WatchlistActivity using WatchlistManager
        Button watchlistButton = findViewById(R.id.watchlistButton);
        watchlistButton.setOnClickListener(v -> {
            // Directly launch WatchlistActivity
            startActivity(new Intent(ResultsActivity.this, WatchlistActivity.class));
        });
    }
}
