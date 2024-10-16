package com.pabirul.notifyguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    public static final String ACTION_NOTIFICATION_PREFS_CHANGED = "com.pabirul.notifyguard.ACTION_NOTIFICATION_PREFS_CHANGED";

    private Context context;
    private AppInfo[] appList;
    private SharedPreferences sharedPreferences;
    private BroadcastReceiver prefsChangedReceiver;

    public AppListAdapter(Context context, AppInfo[] appList) {
        this.context = context;
        this.appList = appList;
        sharedPreferences = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfo appInfo = appList[position];
        holder.appName.setText(appInfo.getAppName());
        holder.appIcon.setImageDrawable(appInfo.getAppIcon());
        Bitmap largeIcon = appInfo.getLargeIcon();
        if (largeIcon != null) {
            holder.largeIcon.setImageBitmap(largeIcon);
            holder.largeIcon.setVisibility(View.VISIBLE);
        } else {
            holder.largeIcon.setVisibility(View.GONE);
        }
        holder.largeIcon.setOnClickListener(v -> {
            FullScreenImageDialog dialog = FullScreenImageDialog.newInstance(appInfo.getLargeIcon());
            dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "FullScreenImageDialog");
        });
        holder.notificationTitle.setText(context.getString(R.string.notification_title_text) + appInfo.getNotificationTitle());
        holder.notificationText.setText(context.getString(R.string.notification_text) + appInfo.getNotificationText());
        holder.notificationChannelId.setText(context.getString(R.string.notification_channel_id) + appInfo.getNotificationChannelId());

        String notifyGuardPackageName = "com.pabirul.notifyguard";
        boolean isNotifyGuardApp = appInfo.getPackageName().equals(notifyGuardPackageName);

        if (isNotifyGuardApp) {
            holder.blockButton.setVisibility(View.GONE);
            holder.blockUrlButton.setVisibility(View.GONE);
        } else {
            boolean isBlocked = sharedPreferences.getBoolean(appInfo.getPackageName(), false);
            boolean isBrowserApp = isBrowserApp(appInfo.getPackageName());
            Set<String> extractedUrls = extractUrls(appInfo.getNotificationTitle() + " " + appInfo.getNotificationText() + " " + appInfo.getNotificationChannelId());

            holder.blockButton.setVisibility(isBlocked ? View.GONE : View.VISIBLE);
            holder.blockButton.setOnClickListener(v -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(appInfo.getPackageName(), true);
                editor.apply();

                Intent intent = new Intent(ACTION_NOTIFICATION_PREFS_CHANGED);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            });

            if (isBlocked || !isBrowserApp || extractedUrls.isEmpty()) {
                holder.blockUrlButton.setVisibility(View.GONE);
            } else {
                holder.blockUrlButton.setVisibility(View.VISIBLE);
                holder.blockUrlButton.setOnClickListener(v -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Set<String> blockedUrls = sharedPreferences.getStringSet("blockedUrls", new HashSet<>());
                    blockedUrls.addAll(extractedUrls);
                    editor.putStringSet("blockedUrls", blockedUrls);
                    editor.apply();

                    Intent intent = new Intent(ACTION_NOTIFICATION_PREFS_CHANGED);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                });
            }

            holder.appIcon.setVisibility(isBlocked ? View.GONE : View.VISIBLE);
            holder.appName.setVisibility(isBlocked ? View.GONE : View.VISIBLE);
            holder.notificationTitle.setVisibility(isBlocked ? View.GONE : View.VISIBLE);
            holder.notificationText.setVisibility(isBlocked ? View.GONE : View.VISIBLE);
            holder.notificationChannelId.setVisibility(isBlocked ? View.GONE : View.VISIBLE);
            holder.largeIcon.setVisibility(isBlocked ? View.GONE : View.VISIBLE);
        }
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

    @Override
    public int getItemCount() {
        return appList.length;
    }

    public void setAppList(AppInfo[] appList) {
        this.appList = appList;
        notifyDataSetChanged();
    }

    private void refreshAppList() {
        // Refresh logic to reload appList from SharedPreferences or other data sources
        notifyDataSetChanged();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        // Unregister the broadcast receiver when the adapter is detached
        LocalBroadcastManager.getInstance(context).unregisterReceiver(prefsChangedReceiver);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView appIcon;
        final ImageView largeIcon;
        final TextView appName;
        final TextView notificationTitle;
        final TextView notificationText;
        final TextView notificationChannelId;
        final Button blockButton;
        final Button blockUrlButton;

        ViewHolder(View view) {
            super(view);
            appIcon = view.findViewById(R.id.appIcon);
            largeIcon = view.findViewById(R.id.largeIcon);
            appName = view.findViewById(R.id.appName);
            notificationTitle = view.findViewById(R.id.notificationTitle);
            notificationText = view.findViewById(R.id.notificationText);
            notificationChannelId = view.findViewById(R.id.notificationChannelId);
            blockButton = view.findViewById(R.id.blockButton);
            blockUrlButton = view.findViewById(R.id.blockUrlButton);
        }
    }

    private boolean isBrowserApp(String packageName) {
        return packageName.equals("com.android.chrome") ||
                packageName.equals("org.mozilla.firefox") ||
                packageName.equals("com.opera.browser") ||
                packageName.equals("com.microsoft.emmx") ||
                packageName.equals("com.brave.browser") ||
                packageName.equals("com.nearme.browser") ||
                packageName.equals("com.duckduckgo.mobile.android");
    }
}
