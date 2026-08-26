package com.example.x.model.hashtag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Hashtag implements Serializable {
    private String hashtagId;
    private String title;
    private List<String> postIds;

    public Hashtag(String title) {
        this.title = title;
        this.hashtagId = UUID.randomUUID().toString();
        this.postIds = new ArrayList<>();
    }

    public Hashtag(int numericId, String title) {
        this.hashtagId = String.valueOf(numericId);
        this.title = title;
        this.postIds = new ArrayList<>();
    }

    public String getHashtagId() {
        return hashtagId;
    }

    public void setHashtagId(String hashtagId) {
        this.hashtagId = hashtagId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getPostIds() {
        return postIds;
    }

    public void setPostIds(List<String> postIds) {
        this.postIds = postIds;
    }

    public void addPost(String postId) {
        if (!postIds.contains(postId))
            postIds.add(postId);
    }

    public void removePost(String postId) {
        postIds.remove(postId);
    }

    public int getUsageCount() {
        return postIds.size();
    }

    @Override
    public String toString() {
        return title;
    }
}
