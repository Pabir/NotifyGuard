package com.pabirul.notifyguard;

public class NotificationInfo {
    private String title;
    private String text;
    private String channelId;

    public NotificationInfo(String title, String text, String channelId) {
        this.title = title;
        this.text = text;
        this.channelId = channelId;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getChannelId() {
        return channelId;
    }
}
