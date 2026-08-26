package com.example.x.model.notification;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;
    private String senderId;
    private NotificationType type;
    private String content;
    private String targetId;
    private LocalDateTime timestamp;
    private boolean isRead;

    public enum NotificationType {
        LIKE,
        FOLLOW,
        MESSAGE,
        REPLY,
        MENTION,
        REPORT
    }

    public Notification() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    public Notification(String userId, String senderId, NotificationType type, String content, String targetId) {
        this();
        this.userId = userId;
        this.senderId = senderId;
        this.type = type;
        this.content = content;
        this.targetId = targetId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}