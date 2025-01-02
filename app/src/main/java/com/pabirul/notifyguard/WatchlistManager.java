package com.pabirul.notifyguard;

import java.util.ArrayList;
import java.util.List;

public class WatchlistManager {

    private static final List<WatchlistAppInfo> watchlist = new ArrayList<>();

    public static boolean addToWatchlist(WatchlistAppInfo app) {
        // Check if app is already in the watchlist
        for (WatchlistAppInfo existingApp : watchlist) {
            if (existingApp.getPackageName().equals(app.getPackageName())) {
                return false; // Already in the watchlist
            }
        }
        watchlist.add(app);
        return true;
    }

    public static List<WatchlistAppInfo> getWatchlist() {
        return new ArrayList<>(watchlist); // Return a copy to prevent modification
    }
    
    // Method to remove an app from the watchlist
    public static void removeFromWatchlist(WatchlistAppInfo appInfo) {
        watchlist.remove(appInfo);
    }
}
