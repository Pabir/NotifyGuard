package com.pabirul.notifyguard;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.pabirul.notifyguard.AppInfo;
import com.pabirul.notifyguard.AppListAdapter;
import com.pabirul.notifyguard.DrawableUtils;
import com.pabirul.notifyguard.PackageManagerWrapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationListener extends NotificationListenerService {

    private static ListenerCallback listenerCallback;
    private static final Set<String> appPackageNames = Collections.synchronizedSet(new HashSet<>());
    private SharedPreferences sharedPreferences;
    private BroadcastReceiver prefsChangedReceiver;
    private static AppInfo[] appList;
    private PackageManagerWrapper packageManagerWrapper;
    private Map<String, StatusBarNotification> packageToNotificationMap = new HashMap<>();
    private Set<String> blockedUrls = new HashSet<>();

    public interface ListenerCallback {
        void onAppListUpdated(AppInfo[] appList);
    }

    public static void setListenerCallback(ListenerCallback callback) {
        listenerCallback = callback;
    }

    public static AppInfo[] getAppList() {
        return appList;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        packageManagerWrapper = new PackageManagerWrapper(this);

        // Load blocked URLs from SharedPreferences
        blockedUrls = new HashSet<>(sharedPreferences.getStringSet("blockedUrls", new HashSet<>()));

        prefsChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppListAdapter.ACTION_NOTIFICATION_PREFS_CHANGED)) {
                    updateAppList();
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(prefsChangedReceiver,
                new IntentFilter(AppListAdapter.ACTION_NOTIFICATION_PREFS_CHANGED));
    }




    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(prefsChangedReceiver);
        super.onDestroy();
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        StatusBarNotification[] activeNotifications = getActiveNotifications();
        for (StatusBarNotification sbn : activeNotifications) {
            processNotification(sbn);
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        processNotification(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        synchronized (appPackageNames) {
            appPackageNames.remove(packageName);
            packageToNotificationMap.remove(packageName);
        }
        updateAppList();
    }

    private void processNotification(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        Notification notification = sbn.getNotification();
        String notificationTitle = notification.extras.getString(Notification.EXTRA_TITLE, "");
        String notificationText = notification.extras.getString(Notification.EXTRA_TEXT, "");
        String channelId = notification.getChannelId();

        Set<String> blockedUrls = sharedPreferences.getStringSet("blockedUrls", new HashSet<>());
        Set<String> blockedChannels = sharedPreferences.getStringSet("blockedChannels", new HashSet<>());

        Log.d("NotificationProcessing", "Package: " + packageName);
        Log.d("NotificationProcessing", "Title: " + notificationTitle);
        Log.d("NotificationProcessing", "Text: " + notificationText);
        Log.d("NotificationProcessing", "ChannelId: " + channelId);
        Log.d("NotificationProcessing", "Blocked URLs: " + blockedUrls);
        Log.d("NotificationProcessing", "Blocked Channels: " + blockedChannels);

        if (containsBlockedUrl(notificationTitle, blockedUrls) || containsBlockedUrl(notificationText, blockedUrls) || containsBlockedUrl(channelId, blockedUrls) || blockedChannels.contains(channelId)) {
            Log.d("NotificationProcessing", "Notification blocked: " + sbn.getKey());
            cancelNotification(sbn.getKey());
        } else {
            if (!isNotificationBlocked(packageName, notification)) {
                synchronized (appPackageNames) {
                    appPackageNames.add(packageName);
                    packageToNotificationMap.put(packageName, sbn);
                }
                updateAppList();
            }
        }
    }


    private boolean containsBlockedUrl(String text, Set<String> blockedUrls) {
        if (text == null || text.isEmpty()) return false;

        for (String blockedUrl : blockedUrls) {
            if (text.contains(blockedUrl)) {
                return true;
            }
        }
        return false;
    }


    private boolean isNotificationBlocked(String packageName, Notification notification) {
        if (sharedPreferences.getBoolean(packageName, false)) {
            return true;
        }

        if (isBrowserApp(packageName)) {
            String notificationTitle = notification.extras.getString(Notification.EXTRA_TITLE);
            String notificationText = notification.extras.getString(Notification.EXTRA_TEXT);

            String title = notificationTitle != null ? notificationTitle.toString() : "";
            String text = notificationText != null ? notificationText.toString() : "";

            Set<String> extractedUrls = new HashSet<>();
            extractedUrls.addAll(extractUrls(title));
            extractedUrls.addAll(extractUrls(text));

            for (String blockedUrl : blockedUrls) {
                if (title.contains(blockedUrl) || text.contains(blockedUrl)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateAppList() {
        synchronized (appPackageNames) {
            appList = new AppInfo[appPackageNames.size()];
            int i = 0;
            for (String packageName : appPackageNames) {
                String appName = packageManagerWrapper.getApplicationLabel(packageName);
                Drawable appIcon = packageManagerWrapper.getApplicationIcon(packageName);
                StatusBarNotification sbn = packageToNotificationMap.get(packageName);
                String notificationTitle = packageManagerWrapper.getNotificationTitle(sbn);
                String notificationText = packageManagerWrapper.getNotificationText(sbn);
                String notificationChannelId = sbn.getNotification().getChannelId();
                Bitmap largeIcon = null;
                if (sbn.getNotification().getLargeIcon() != null) {
                    Drawable largeIconDrawable = sbn.getNotification().getLargeIcon().loadDrawable(this);
                    largeIcon = DrawableUtils.drawableToBitmap(largeIconDrawable);
                }
                appList[i++] = new AppInfo(packageName, appName, appIcon, largeIcon, notificationTitle, notificationText, notificationChannelId);
            }
        }
        if (listenerCallback != null) {
            listenerCallback.onAppListUpdated(appList);
        }
    }

    private boolean isBrowserApp(String packageName) {
        return packageName.equals("com.android.chrome") || packageName.equals("org.mozilla.firefox") || packageName.equals("com.opera.browser");
    }

    private Set<String> extractUrls(String text) {
        Set<String> urls = new HashSet<>();
        String urlPattern = "(https?://|www\\.|[a-zA-Z0-9-]+\\.[a-zA-Z]{2,})([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?";
        Pattern pattern = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String url = matcher.group();
            urls.add(url);
        }
        return urls;
    }

    private void saveBlockedUrls() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("blockedUrls", blockedUrls);
        editor.apply();
    }

}
