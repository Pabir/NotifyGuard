package com.pabirul.notifyguard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class BlockedUrlsAdapter extends BaseAdapter {

    private Context context;
    private List<String> blockedUrlsList;

    public BlockedUrlsAdapter(Context context, List<String> blockedUrlsList) {
        this.context = context;
        this.blockedUrlsList = blockedUrlsList;
    }

    @Override
    public int getCount() {
        return blockedUrlsList.size();
    }

    @Override
    public Object getItem(int position) {
        return blockedUrlsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_blocked_url, parent, false);
        }

        String url = blockedUrlsList.get(position);

        TextView urlTextView = convertView.findViewById(R.id.urlTextView);
        Button unblockButton = convertView.findViewById(R.id.unblockUrlButton);

        urlTextView.setText(url);
        unblockButton.setOnClickListener(v -> {
            if (context instanceof BlockedUrlsActivity) {
                ((BlockedUrlsActivity) context).unblockUrl(url);
            }
        });

        return convertView;
    }
}
