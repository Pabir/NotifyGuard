package com.pabirul.notifyguard;

import android.graphics.drawable.Drawable;
import java.io.Serializable;

public class WatchlistAppInfo{
    private final String name;
    private final String packageName;
    private final Drawable icon;

    public WatchlistAppInfo(String name, String packageName, Drawable icon) {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return icon;
    }
}
