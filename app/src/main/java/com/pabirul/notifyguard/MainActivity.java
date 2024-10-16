package com.pabirul.notifyguard;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements NotificationListener.ListenerCallback {

    private static final int REQUEST_POST_NOTIFICATIONS = 1;
    private RecyclerView recyclerView;
    private AppListAdapter appListAdapter;
    private Button btnBlockedApps, btnBlockedUrls;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);

        // Create notification channel
        createNotificationChannel();

        // Check for POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS}, REQUEST_POST_NOTIFICATIONS);
            } else {
                sendNotification();
            }
        } else {
            sendNotification();
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (!isNotificationServiceEnabled()) {
            Toast.makeText(this, "Please enable notification access", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        } else {
            NotificationListener.setListenerCallback(this);
            startService(new Intent(this, NotificationListener.class));
        }

        btnBlockedApps = findViewById(R.id.btnBlockedApps);
        btnBlockedApps.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BlockedAppsActivity.class)));
        btnBlockedUrls = findViewById(R.id.btnBlockedUrls);
        btnBlockedUrls.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BlockedUrlsActivity.class);
            startActivity(intent);
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateAppList();
            }
        }, new IntentFilter(AppListAdapter.ACTION_NOTIFICATION_PREFS_CHANGED));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "your_channel_id";
            String channelName = "Your Channel Name";
            String channelDescription = "Your Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void sendNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE); // Add FLAG_IMMUTABLE here

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "your_channel_id")
                .setSmallIcon(R.drawable.ic_launcher_background) // Replace with your app's icon
                .setContentTitle("Notification Title")
                .setContentText("This is a notification from NotifyGuard")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }


    private boolean isNotificationServiceEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onAppListUpdated(AppInfo[] appList) {
        runOnUiThread(() -> {
            if (appListAdapter == null) {
                appListAdapter = new AppListAdapter(MainActivity.this, appList);
                recyclerView.setAdapter(appListAdapter);
            } else {
                appListAdapter.setAppList(appList);
                appListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void updateAppList() {
        // Call the service to update the app list
        if (NotificationListener.getAppList() != null) {
            onAppListUpdated(NotificationListener.getAppList());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                sendNotification();
            } else {
                Toast.makeText(this, "Permission denied to post notifications", Toast.LENGTH_SHORT).show();
            }
        }
    }
}