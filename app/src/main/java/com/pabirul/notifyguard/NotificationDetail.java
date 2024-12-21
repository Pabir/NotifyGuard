package com.pabirul.notifyguard;

import java.util.Set;

public class NotificationDetail {
    private final String title;
    private final String text;
    private final Set<String> urls;

    public NotificationDetail(String title, String text, Set<String> urls) {
        this.title = title;
        this.text = text;
        this.urls = urls;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public Set<String> getUrls() {
        return urls;
    }
}
