package com.pabirul.notifyguard;

import android.content.Context;
import java.util.List;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.ArrayAdapter;


public class UrlAdapter extends ArrayAdapter<String> {

    public UrlAdapter(Context context, List<String> urls) {
        super(context, android.R.layout.simple_list_item_1, urls);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Customize the view if necessary
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView urlTextView = convertView.findViewById(android.R.id.text1);
        urlTextView.setText(getItem(position));

        return convertView;
    }
}
