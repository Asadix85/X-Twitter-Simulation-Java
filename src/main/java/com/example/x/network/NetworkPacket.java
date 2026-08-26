package com.example.x.network;

import java.io.Serializable;
import java.time.LocalDateTime;

public class NetworkPacket implements Serializable {

    private static final long serialVersionUID = 1L;

    private RequestType type;
    private Object data;
    private String senderId;
    private String receiverId;
    private LocalDateTime timestamp;
    private String sessionId;

    public enum RequestType {
        LOGIN,
        LOGOUT,
        REGISTER,

        SEND_MESSAGE,
        SEND_MESSAGE_TO_GROUP,
        GET_HISTORY,
        GET_CONVERSATION,
        MARK_AS_READ,
        MARK_ALL_AS_READ,
        DELETE_MESSAGE,
        DELETE_MESSAGE_FOR_EVERYONE,
        TYPING_INDICATOR,
        TYPING_STOP,
        TYPING_START,

        GET_USERS,
        GET_USER_BY_ID,
        GET_USER_BY_USERNAME,
        GET_ONLINE_USERS,
        GET_FOLLOWERS,
        GET_FOLLOWING,
        FOLLOW_USER,
        UNFOLLOW_USER,
        UPDATE_USER_STATUS,

        CREATE_POST,
        GET_POSTS,
        GET_POST_BY_ID,
        DELETE_POST,
        LIKE_POST,
        UNLIKE_POST,
        GET_LIKERS,
        ADD_REPLY,
        DELETE_REPLY,
        GET_REPLIES,
        GET_POSTS_BY_AUTHOR,

        GET_HASHTAGS,
        GET_HASHTAG_BY_TITLE,
        GET_POPULAR_HASHTAGS,
        FOLLOW_HASHTAG,
        UNFOLLOW_HASHTAG,

        CREATE_REPORT,
        GET_REPORTS,
        UPDATE_REPORT_STATUS,

        NOTIFICATION,

        SYNC_HISTORY,
        GET_NOTIFICATIONS,
        DISCONNECT,
        PING,
        PONG,
        ERROR
    }


    public NetworkPacket() {
        this.timestamp = LocalDateTime.now();
    }

    public NetworkPacket(RequestType type, Object data) {
        this();
        this.type = type;
        this.data = data;
    }

    public NetworkPacket(RequestType type, Object data, String senderId, String receiverId) {
        this(type, data);
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public boolean isType(RequestType type) {
        return this.type == type;
    }

    public boolean isError() {
        return this.type == RequestType.ERROR;
    }

    public <T> T getDataAs(Class<T> clazz) {
        if (data == null) return null;
        if (clazz.isInstance(data)) {
            return clazz.cast(data);
        }
        return null;
    }

    public String getStringData() {
        return data != null ? data.toString() : null;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "NetworkPacket{" +
                "type=" + type +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", timestamp=" + timestamp +
                ", data=" + (data != null ? data.getClass().getSimpleName() : "null") +
                '}';
    }
}