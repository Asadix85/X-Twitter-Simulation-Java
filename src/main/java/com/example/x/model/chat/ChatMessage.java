package com.example.x.model.chat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String messageId;
    private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime sendTime;
    private MessageStatus status;
    private boolean isDeletedForEveryone;

    public enum MessageStatus {
        SENT,
        DELIVERED,
        READ
    }

    public ChatMessage() {
        this.messageId = UUID.randomUUID().toString();
        this.sendTime = LocalDateTime.now();
        this.status = MessageStatus.SENT;
        this.isDeletedForEveryone = false;
    }

    public ChatMessage(String senderId, String receiverId, String content) {
        this();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
    }

    public ChatMessage(String senderId, String receiverId, String content, MessageStatus status) {
        this(senderId, receiverId, content);
        this.status = status;
    }

    public boolean isSent() {
        return status == MessageStatus.SENT;
    }

    public boolean isDelivered() {
        return status == MessageStatus.DELIVERED;
    }

    public boolean isRead() {
        return status == MessageStatus.READ;
    }

    public void markAsDelivered() {
        this.status = MessageStatus.DELIVERED;
    }

    public void markAsRead() {
        this.status = MessageStatus.READ;
    }

    public boolean isDeleted() {
        return isDeletedForEveryone;
    }

    public void deleteForEveryone() {
        this.isDeletedForEveryone = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public boolean isDeletedForEveryone() {
        return isDeletedForEveryone;
    }

    public void setDeletedForEveryone(boolean deletedForEveryone) {
        isDeletedForEveryone = deletedForEveryone;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", messageId='" + messageId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", content='" + content + '\'' +
                ", sendTime=" + sendTime +
                ", status=" + status +
                ", isDeletedForEveryone=" + isDeletedForEveryone +
                '}';
    }

    public String getDisplayText() {
        return content;
    }

    public String getTimeDisplay() {
        return sendTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getDateDisplay() {
        return sendTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getChatDisplay() {
        return "[" + getTimeDisplay() + "] " + content;
    }
}