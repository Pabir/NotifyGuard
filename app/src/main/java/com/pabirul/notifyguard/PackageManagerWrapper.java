package com.pabirul.notifyguard;

import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.service.notification.StatusBarNotification;

public class PackageManagerWrapper {
    private PackageManager packageManager;

    public PackageManagerWrapper(Context context) {
        this.packageManager = context.getPackageManager();
    }

    public String getApplicationLabel(String packageName) {
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageName;
    }


    public Drawable getApplicationIcon(String packageName) {
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            return packageManager.getApplicationIcon(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getNotificationTitle(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        String title = notification.extras.getString(Notification.EXTRA_TITLE);
        if (title == null) {
            title = (String) notification.tickerText;
        }
        return title;
    }

    public String getNotificationText(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        String text = notification.extras.getString(Notification.EXTRA_TEXT);
        return text != null ? text : "";
    }
}