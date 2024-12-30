package com.pabirul.notifyguard;

import android.graphics.drawable.Drawable;

public class ResultAppInfo {
    private final String name;
    private final String packageName;
    private final Drawable icon;
    private boolean isAdware;

    public ResultAppInfo(String name, String packageName, Drawable icon, boolean isAdware) {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
        this.isAdware = isAdware;
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
    
    public boolean isAdware() {
        return isAdware;
    }
}
