package com.aap.medicore.Models;

import java.time.LocalDateTime;

public class InboxMessage {
    public String id;
    public String username;
    public String title;
    public String content;
    public long sentTime;
    public long openTime;
    public boolean isOpened;

    public InboxMessage() {
    }

    public InboxMessage(String title, String content) {
        this.title = title;
        this.content = content;
    }


    public InboxMessage(String id, String title, String content, long sentTime, long openTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.sentTime = sentTime;
        this.openTime = openTime;
    }
}
