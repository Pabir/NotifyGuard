package com.pabirul.notifyguard;

import java.util.List;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.view.LayoutInflater;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import android.graphics.Color;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {

    private final List<ResultAppInfo> appList;

    public ResultsAdapter(List<ResultAppInfo> appList) {
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

        // Set the app icon
        holder.appIconImageView.setImageDrawable(appInfo.getIcon());

        // Set the app name
        holder.appNameTextView.setText(appInfo.getName());

        // Set additional details (e.g., package name)
        holder.appDetailsTextView.setText(appInfo.getPackageName());
        
        // Highlight if the app is adware
        if (appInfo.isAdware()) {
           holder.warningTextView.setVisibility(View.VISIBLE);
            holder.warningTextView.setText("Potential Adware Detected!");
            holder.warningTextView.setTextColor(Color.RED);
        } else {
            holder.warningTextView.setVisibility(View.GONE);
        }
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIconImageView = itemView.findViewById(R.id.appIconImageView);
            appNameTextView = itemView.findViewById(R.id.appNameTextView);
            appDetailsTextView = itemView.findViewById(R.id.appDetailsTextView);
            warningTextView = itemView.findViewById(R.id.warningTextView);
        }
    }
}
