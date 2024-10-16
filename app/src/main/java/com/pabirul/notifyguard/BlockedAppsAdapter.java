package com.pabirul.notifyguard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Set;

public class BlockedAppsAdapter extends RecyclerView.Adapter<BlockedAppsAdapter.ViewHolder> {

    private final Context context;
    private final List<AppInfo> blockedAppsList;
    private final SharedPreferences sharedPreferences;

    public BlockedAppsAdapter(Context context, List<AppInfo> blockedAppsList) {
        this.context = context;
        this.blockedAppsList = blockedAppsList;
        this.sharedPreferences = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_blocked_app, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfo appInfo = blockedAppsList.get(position);
        holder.appName.setText(appInfo.getAppName());
        holder.appIcon.setImageDrawable(appInfo.getAppIcon());

        // Display blocked URLs
        StringBuilder blockedUrlsText = new StringBuilder();
        for (String url : appInfo.getBlockedUrls()) {
            blockedUrlsText.append(url).append("\n");
        }
        holder.blockedUrls.setText(blockedUrlsText.toString());

        holder.unblockButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(appInfo.getPackageName(), false);
            editor.apply();

            int index = holder.getAdapterPosition();
            blockedAppsList.remove(index);
            notifyItemRemoved(index);

            // Notify AppListAdapter about the change
            Intent intent = new Intent(AppListAdapter.ACTION_NOTIFICATION_PREFS_CHANGED);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });
    }

    @Override
    public int getItemCount() {
        return blockedAppsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView appIcon;
        final TextView appName;
        final Button unblockButton;
        final TextView blockedUrls;

        public ViewHolder(View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIconBlocked);
            appName = itemView.findViewById(R.id.appNameBlocked);
            unblockButton = itemView.findViewById(R.id.unblockButton);
            blockedUrls = itemView.findViewById(R.id.blockedUrls);
        }
    }
}