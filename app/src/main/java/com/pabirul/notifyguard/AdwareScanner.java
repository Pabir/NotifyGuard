package com.pabirul.notifyguard;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdwareScanner {

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

    // Scan all installed apps and flag potential adware
    public List<String> scanInstalledApps() {
        List<String> adwareApps = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo app : installedApps) {
            if (app.metaData != null && containsAdMetadata(app.metaData)) {
                adwareApps.add((String) packageManager.getApplicationLabel(app));
            }
        }

        return adwareApps;
    }

    // Check if an app's metadata contains known ad-related keys
    private boolean containsAdMetadata(Bundle metaData) {
        for (String key : adMetadataKeys) {
            if (metaData.containsKey(key)) {
                return true; // Ad metadata found
            }
        }
        return false;
    }
}
