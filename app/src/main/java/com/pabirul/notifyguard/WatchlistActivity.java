package com.pabirul.notifyguard;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WatchlistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        // Get the watchlist from the WatchlistManager
        List<WatchlistAppInfo> watchlist = WatchlistManager.getWatchlist();

        // Set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.watchlistRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Use a WatchlistAdapter to display watchlist items
        WatchlistAdapter adapter = new WatchlistAdapter(watchlist);
        recyclerView.setAdapter(adapter);
    }
}
