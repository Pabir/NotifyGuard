package com.pabirul.notifyguard;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.util.Set;

public class AppInfo {
    private String packageName;
    private String appName;
    private Drawable appIcon;
    private Bitmap largeIcon;
    private String notificationTitle;
    private String notificationText;
    private String notificationChannelId;
    private Set<String> blockedUrls;
    private boolean isExpanded = false;
    public AppInfo(String packageName, String appName, Drawable appIcon, Bitmap largeIcon,
                   String notificationTitle, String notificationText, String notificationChannelId) {
        this.packageName = packageName;
        this.appName = appName;
        this.appIcon = appIcon;
        this.largeIcon = largeIcon;
        this.notificationTitle = notificationTitle;
        this.notificationText = notificationText;
        this.notificationChannelId = notificationChannelId;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAppName() {
        return appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public Bitmap getLargeIcon() {
        return largeIcon;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public String getNotificationChannelId() {
        return notificationChannelId;
    }

    public Set<String> getBlockedUrls() {
        return blockedUrls;
    }

    public void setBlockedUrls(Set<String> blockedUrls) {
        this.blockedUrls = blockedUrls;
    }
    
   // Getter and Setter for isExpanded
    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
