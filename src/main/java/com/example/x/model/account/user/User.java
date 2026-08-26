package com.example.x.model.account.user;

import com.example.x.model.account.AccountBadge;
import com.example.x.model.account.Account;
import com.example.x.model.hashtag.Hashtag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class User extends Account implements Serializable {
    protected int accountScore;
    protected int token;
    protected List<String> followerIds;
    protected List<String> followingIds;
    protected List<String> postIds;
    protected List<String> likedPostIds;
    protected List<String> repliesPostIds;
    protected List<String> mediaPostIds;
    protected List<Hashtag> personalizationHashtags;
    protected String bio;
    protected AccountBadge accountBadge;
    protected boolean isBlocked;

    public User(String fullName, String phoneNumber, String userName, String password, String userEmail, AccountBadge accountBadge) {
        super(fullName, phoneNumber, userName, password, userEmail);
        this.accountScore = 0;
        this.token = 0;
        this.followerIds = new ArrayList<>();
        this.followingIds = new ArrayList<>();
        this.repliesPostIds = new ArrayList<>();
        this.mediaPostIds = new ArrayList<>();
        this.postIds = new ArrayList<>();
        this.likedPostIds = new ArrayList<>();
        this.personalizationHashtags = new ArrayList<>(4);
        this.accountBadge = accountBadge;
        this.isBlocked = false;
    }

    public List<String> getFollowerIds() {
        return followerIds;
    }

    public void setFollowerIds(List<String> followerIds) {
        this.followerIds = followerIds;
    }

    public List<String> getFollowingIds() {
        return followingIds;
    }

    public void setFollowingIds(List<String> followingIds) {
        this.followingIds = followingIds;
    }

    public void addFollowingIds(User followingUser) {
        if (followingUser == null || followingUser.getId().equals(this.getId())) {
            return;
        }
        if (!this.followingIds.contains(followingUser.getId())) {
            this.followingIds.add(followingUser.getId());
        }
    }

    public void addFollowerIds(User followerUser) {
        if (followerUser == null || followerUser.getId().equals(this.getId())) {
            return;
        }
        if (!this.followerIds.contains(followerUser.getId())) {
            this.followerIds.add(followerUser.getId());
        }
    }

    public void removeFollowingIds(User followingUser) {
        if (followingUser != null) {
            this.followingIds.remove(followingUser.getId());
        }
    }

    public void removeFollowerIds(User followerUser) {
        if (followerUser != null) {
            this.followerIds.remove(followerUser.getId());
        }
    }


    public int getAccountScore() { return accountScore; }
    public int getTokens() { return token; }
    public void setTokens(int token) { this.token += token; }
    public List<String> getPostIds() { return postIds; }
    public void setPostIds(List<String> postIds) { this.postIds = postIds; }
    public List<String> getLikedPostIds() { return likedPostIds; }
    public void addLikedPostIds(String likedPostId) { this.likedPostIds.add(likedPostId); }
    public void removeLikedPostIds(String postId) {this.likedPostIds.remove(postId); }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public AccountBadge getAccountBadge() { return accountBadge; }
    public void setAccountBadge(AccountBadge accountBadge) { this.accountBadge = accountBadge; }
    public boolean isBlocked() { return isBlocked; }
    public void setBlocked(boolean blocked) { isBlocked = blocked; }
    public List<Hashtag> getPersonalizationHashtags() { return personalizationHashtags; }
    public void setPersonalizationHashtags(List<Hashtag> personalizationHashtags) { this.personalizationHashtags = personalizationHashtags; }
    public void deductTokens(int cost) { this.token -= cost; }
    public List<String> getRepliesPostIds() { return repliesPostIds; }
    public List<String> getMediaPostIds() { return mediaPostIds; }

    public void setAccountScore(int accountScore) {
        this.accountScore = accountScore;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public void setLikedPostIds(List<String> likedPostIds) {
        this.likedPostIds = likedPostIds;
    }

    public void setRepliesPostIds(List<String> repliesPostIds) {
        this.repliesPostIds = repliesPostIds;
    }

    public void setMediaPostIds(List<String> mediaPostIds) {
        this.mediaPostIds = mediaPostIds;
    }
}