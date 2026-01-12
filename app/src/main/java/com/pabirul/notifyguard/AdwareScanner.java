package com.pabirul.notifyguard;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdwareScanner {

    // Define the interface
    public interface ScanListener {
        void onScanComplete(ArrayList<String> adwareApps);
        void onScanProgressUpdate(int progress, int total);
    }

    private final Context context;

    // List of known ad metadata keys to search for
    private static final List<String> adMetadataKeys = Arrays.asList(
            "com.google.android.gms.ads.APPLICATION_ID",
            "com.facebook.sdk.ApplicationId",
            "com.mopub.sdk.ApplicationId",
            "com.chartboost.sdk.AppId",
            "com.applovin.sdk.AppLovinSdkKey",
            "com.startapp.sdk.adsbase.ApplicationId"
    );

    public AdwareScanner(Context context) {
        this.context = context;
    }

    // Scan all installed apps and flag potential adware (synchronous version)
    public ArrayList<String> scanInstalledApps() {
        ArrayList<String> adwareApps = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo app : installedApps) {
            if (app.metaData != null && containsAdMetadata(app.metaData)) {
                adwareApps.add((String) packageManager.getApplicationLabel(app));
            }
        }

        return adwareApps;
    }

    // Async scanning method
    public void scanInstalledAppsAsync(ScanListener listener) {
        new ScanTask(listener).execute();
    }

    // Check if an app's metadata contains known ad-related keys
    private boolean containsAdMetadata(Bundle metaData) {
        if (metaData == null) return false;
        
        for (String key : adMetadataKeys) {
            if (metaData.containsKey(key)) {
                return true; // Ad metadata found
            }
        }
        return false;
    }

    // AsyncTask for scanning
    private class ScanTask extends AsyncTask<Void, Integer, ArrayList<String>> {
        private final ScanListener listener;
        private PackageManager packageManager;
        private List<ApplicationInfo> installedApps;
        
        public ScanTask(ScanListener listener) {
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            packageManager = context.getPackageManager();
            installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            ArrayList<String> adwareApps = new ArrayList<>();
            
            for (int i = 0; i < installedApps.size(); i++) {
                ApplicationInfo app = installedApps.get(i);
                
                if (app.metaData != null && containsAdMetadata(app.metaData)) {
                    String appName = (String) packageManager.getApplicationLabel(app);
                    adwareApps.add(appName);
                }
                
                // Update progress
                publishProgress(i + 1, installedApps.size());
                
                // Small delay to show progress (optional)
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            return adwareApps;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (listener != null && values.length >= 2) {
                listener.onScanProgressUpdate(values[0], values[1]);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> adwareApps) {
            super.onPostExecute(adwareApps);
            if (listener != null) {
                listener.onScanComplete(adwareApps);
            }
        }
    }
}