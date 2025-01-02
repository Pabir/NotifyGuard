package com.pabirul.notifyguard;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {

    private final List<ResultAppInfo> appList;
    private final Context context;

    public ResultsAdapter(Context context, List<ResultAppInfo> appList) {
        this.context = context;
        this.appList = appList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_app, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResultAppInfo appInfo = appList.get(position);

        // Set the app icon, name, and package name
        holder.appIconImageView.setImageDrawable(appInfo.getIcon());
        holder.appNameTextView.setText(appInfo.getName());
        holder.appDetailsTextView.setText(appInfo.getPackageName());

        // Highlight if the app is flagged as adware
        if (appInfo.isAdware()) {
            holder.warningTextView.setVisibility(View.VISIBLE);
            holder.warningTextView.setText("Potential Adware Detected!");
            holder.warningTextView.setTextColor(Color.RED);
        } else {
            holder.warningTextView.setVisibility(View.GONE);
        }

        // Set up the "Add to Watchlist" button
        holder.addwatchlistButton.setOnClickListener(v -> {
            WatchlistAppInfo watchlistApp = new WatchlistAppInfo(
                    appInfo.getName(),
                    appInfo.getPackageName(),
                    appInfo.getIcon()
            );
               // Remove the app from the current list
            appList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, appList.size());

            boolean added = WatchlistManager.addToWatchlist(watchlistApp);
            if (added) {
                Toast.makeText(context, appInfo.getName() + " added to Watchlist", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, appInfo.getName() + " is already in Watchlist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView appIconImageView;
        final TextView appNameTextView;
        final TextView appDetailsTextView;
        final TextView warningTextView;
        final TextView addwatchlistButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIconImageView = itemView.findViewById(R.id.appIconImageView);
            appNameTextView = itemView.findViewById(R.id.appNameTextView);
            appDetailsTextView = itemView.findViewById(R.id.appDetailsTextView);
            warningTextView = itemView.findViewById(R.id.warningTextView);
            addwatchlistButton = itemView.findViewById(R.id.addwatchlistButton);
        }
    }
}
