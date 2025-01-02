package com.pabirul.notifyguard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {

    private final List<WatchlistAppInfo> watchlist;

    public WatchlistAdapter(List<WatchlistAppInfo> watchlist) {
        this.watchlist = watchlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_watchlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WatchlistAppInfo appInfo = watchlist.get(position);

        // Set app details
        holder.appIconImageView.setImageDrawable(appInfo.getIcon());
        holder.appNameTextView.setText(appInfo.getName());
        holder.appPackageTextView.setText(appInfo.getPackageName());
        
         

        // Handle remove button click
        holder.removeFromWatchlistButton.setOnClickListener(v -> {
            watchlist.remove(position); // Remove the item from the list
            WatchlistManager.removeFromWatchlist(appInfo); // Remove from WatchlistManager
            notifyItemRemoved(position); // Notify RecyclerView of the removal
            Toast.makeText(v.getContext(), appInfo.getName() + " removed from watchlist", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return watchlist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView appIconImageView;
        final TextView appNameTextView;
        final TextView appPackageTextView;
        final Button removeFromWatchlistButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIconImageView = itemView.findViewById(R.id.appIconImageView);
            appNameTextView = itemView.findViewById(R.id.appNameTextView);
            appPackageTextView = itemView.findViewById(R.id.packageNameTextView);
            removeFromWatchlistButton = itemView.findViewById(R.id.removeFromWatchlistButton);
        }
    }
}
