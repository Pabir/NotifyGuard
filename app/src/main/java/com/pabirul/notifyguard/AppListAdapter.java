package com.pabirul.notifyguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.List;
import java.util.ArrayList;
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
import androidx.appcompat.app.AlertDialog;


public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    public static final String ACTION_NOTIFICATION_PREFS_CHANGED = "com.pabirul.notifyguard.ACTION_NOTIFICATION_PREFS_CHANGED";
    private Set<String> blockedUrls;

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
           holder.urlsTextView.setVisibility(View.GONE);
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

    if (isBrowserApp) {
        holder.blockUrlButton.setVisibility(View.VISIBLE);
               holder.urlsTextView.setVisibility(View.VISIBLE);
        holder.blockUrlButton.setOnClickListener(v -> {
    // Show confirmation dialog before blocking all URLs
    new AlertDialog.Builder(context)
        .setTitle("Block All URLs")
        .setMessage("Are you sure you want to block all extracted URLs?")
        .setPositiveButton("Block All", (dialog, which) -> {
            // Retrieve the current blocked URLs from SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Set<String> blockedUrls = new HashSet<>(sharedPreferences.getStringSet("blockedUrls", new HashSet<>()));

            // Add all extracted URLs to the blocked URLs set
            blockedUrls.addAll(extractedUrls);

            // Save the updated set back to SharedPreferences
            editor.putStringSet("blockedUrls", blockedUrls);
            editor.apply();

            // Send broadcast to update other components (if needed)
            Intent intent = new Intent(ACTION_NOTIFICATION_PREFS_CHANGED);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            // Notify the user
            Toast.makeText(context, "All URLs blocked successfully.", Toast.LENGTH_SHORT).show();

            // Log the blocked URLs for debugging
            Log.d("BlockedURLs", "Blocked URLs: " + blockedUrls);

            // Refresh the UI if needed
            notifyDataSetChanged();
        })
        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
        .show();
});


        // Set up the ListView with the extracted URLs
        // If URLs are available, show them in the TextView
    if (extractedUrls != null && !extractedUrls.isEmpty()) {
        // Combine URLs into a single string
        StringBuilder urlsBuilder = new StringBuilder();
        for (String url : extractedUrls) {
            urlsBuilder.append(url).append("\n"); // Add each URL in a new line
        }

        // Set the combined URLs in the TextView
                   
        // Build clickable spans for each URL
SpannableString spannableUrls = new SpannableString(urlsBuilder.toString());
String[] urlsArray = urlsBuilder.toString().split("\n");

int startIndex = 0;
for (String url : urlsArray) {
    int endIndex = startIndex + url.length();

    // Create a clickable span for the URL
    ClickableSpan clickableSpan = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            // Show confirmation popup
            showConfirmationPopup(context, url);
        }
    };

    // Set the clickable span to the URL
    spannableUrls.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    // Update startIndex for the next URL
    startIndex = endIndex + 1; // +1 for the newline character
}

// Set the SpannableString to the TextView
holder.urlsTextView.setText(spannableUrls);
holder.urlsTextView.setMovementMethod(LinkMovementMethod.getInstance()); // Enable link clicks

        // Make the TextView visible
    } else {
        holder.urlsTextView.setVisibility(View.GONE); // Hide the TextView if no URLs
    }
        // Set the visibility of other elements based on the blocked status
        holder.appIcon.setVisibility(isBlocked ? View.GONE : View.VISIBLE);
        holder.appName.setVisibility(isBlocked ? View.GONE : View.VISIBLE);
        holder.notificationTitle.setVisibility(isBlocked ? View.GONE : View.VISIBLE);
        holder.notificationText.setVisibility(isBlocked ? View.GONE : View.VISIBLE);
        holder.notificationChannelId.setVisibility(isBlocked ? View.GONE : View.VISIBLE);
        holder.largeIcon.setVisibility(isBlocked ? View.GONE : View.VISIBLE);
               
    } else if (isBlocked || !isBrowserApp || extractedUrls.isEmpty()) {
        holder.blockUrlButton.setVisibility(View.GONE);
       holder.urlsTextView.setVisibility(View.GONE); // Hide ListView if no URLs
    } else {
    // Handle other conditions if necessary
}


    // Set the visibility for app details if blocked
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
      ListView notificationUrlsListView;
        final ImageView appIcon;
        final ImageView largeIcon;
        final TextView appName;
        final TextView notificationTitle;
        final TextView notificationText;
        final TextView notificationChannelId;
        final Button blockButton;
        final Button blockUrlButton;
        final TextView urlsTextView;

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
           urlsTextView = view.findViewById(R.id.urlsTextView);
        }
    }
    
   private void showConfirmationPopup(Context context, String url) {
    new AlertDialog.Builder(context)
        .setTitle("Block Notifications")
        .setMessage("Are you sure you want to block notifications from this URL?\n\n" + url)
        .setPositiveButton("Block", (dialog, which) -> {
            // Add URL to the blocked list and save it
            addToBlockedUrls(url);

            // Notify the user
            Toast.makeText(context, "URL blocked: " + url, Toast.LENGTH_SHORT).show();
        })
        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
        .show();
}

   private void addToBlockedUrls(String url) {
    // Add the URL to the blockedUrls set
       SharedPreferences.Editor editor = sharedPreferences.edit();
       Set<String> blockedUrls = sharedPreferences.getStringSet("blockedUrls", new HashSet<>());
    blockedUrls.add(url);

    // Save the updated blockedUrls to SharedPreferences
    
    editor.putStringSet("blockedUrls", blockedUrls);
    editor.apply();
        Intent intent = new Intent(ACTION_NOTIFICATION_PREFS_CHANGED);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    Log.d("BlockURL", "Blocked URL: " + url);
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
