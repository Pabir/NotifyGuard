package com.pabirul.notifyguard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockedUrlsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private BlockedUrlsAdapter adapter;
    private List<String> blockedUrlsList;
    private ListView listView;
    private Button unblockAllButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_urls);

        sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);

        blockedUrlsList = new ArrayList<>(sharedPreferences.getStringSet("blockedUrls", new HashSet<>()));
        adapter = new BlockedUrlsAdapter(this, blockedUrlsList);

        listView = findViewById(R.id.blockedUrlsListView);
        listView.setAdapter(adapter);

        unblockAllButton = findViewById(R.id.unblockAllButton);
        unblockAllButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("blockedUrls", new HashSet<>());
            editor.apply();

            blockedUrlsList.clear();
            adapter.notifyDataSetChanged();
        });
    }

    public void unblockUrl(String url) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> blockedUrls = sharedPreferences.getStringSet("blockedUrls", new HashSet<>());
        blockedUrls.remove(url);
        editor.putStringSet("blockedUrls", blockedUrls);
        editor.apply();

        blockedUrlsList.remove(url);
        adapter.notifyDataSetChanged();
    }
}
