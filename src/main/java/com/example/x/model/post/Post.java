package com.example.x.model.post;

import com.example.x.model.database.DataManager;
import com.example.x.model.file.File;
import com.example.x.model.hashtag.Hashtag;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Post implements Serializable {
    private String postId;
    private LocalDateTime publishDate;
    private String postText;
    private File media;
    private int totalLikes;
    private int totalComments;
    private int totalViews;
    private String authorId;
    private List<String> hashtagsIds;
    private boolean locked;
    private boolean deleted;
    private String parentPostId;
    private List<String> repliesIds;
    private List<String> likedUserIds;

    public Post(String postText, String authorId) {
        this.postId = UUID.randomUUID().toString();
        this.publishDate = LocalDateTime.now();
        this.postText = postText;
        this.media = null;
        this.authorId = authorId;
        this.totalLikes = 0;
        this.totalComments = 0;
        this.totalViews = 0;
        this.hashtagsIds = new ArrayList<>();
        this.locked = false;
        this.deleted = false;
        this.parentPostId = null;
        this.repliesIds = new ArrayList<>();
        autoProcessHashtags();
    }

    public void autoProcessHashtags() {
        List<String> extractTags = extractHashtags();
        for (String tag : extractTags) {
            Hashtag existingHashtag = DataManager.getInstance().findHashtagByTitle("#" + tag);
            if (existingHashtag != null) {
                addHashtag(existingHashtag.getHashtagId());
                existingHashtag.addPost(this.postId);
            } else {
                Hashtag newHashtag = new Hashtag("#" + tag);
                DataManager.getInstance().addHashtag(newHashtag);
                addHashtag(newHashtag.getHashtagId());
                newHashtag.addPost(this.postId);
            }
        }
    }

    public List<String> extractHashtags() {
        List<String> extractHashtags = new ArrayList<>();
        if (this.postText == null || postText.isBlank())
            return extractHashtags;
        Pattern pattern = Pattern.compile("#([a-zA-Z0-9]+)");
        Matcher matcher = pattern.matcher(this.postText);
        while (matcher.find()) {
            String hashtag = matcher.group(1);
            if (!extractHashtags.contains(hashtag))
                extractHashtags.add(hashtag);
        }
        return extractHashtags;
    }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public LocalDateTime getPublishDate() { return publishDate; }
    public void setPublishDate(LocalDateTime publishDate) { this.publishDate = publishDate; }

    public String getPostText() { return postText; }
    public void setPostText(String postText) {
        this.postText = postText;
    }

    public File getMedia() { return media; }
    public void setMedia(File media) { this.media = media; }

    public int getTotalLikes() { return totalLikes; }
    public void setTotalLikes(int totalLikes) { this.totalLikes = totalLikes; }

    public int getTotalComments() { return totalComments; }
    public void setTotalComments(int totalComments) { this.totalComments = totalComments; }

    public int getTotalViews() { return totalViews; }
    public void setTotalViews(int totalViews) { this.totalViews = totalViews; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public List<String> getHashtagsIds() {
        if (hashtagsIds == null) hashtagsIds = new ArrayList<>();
        return hashtagsIds;
    }
    public void setHashtagsIds(List<String> hashtagsIds) { this.hashtagsIds = hashtagsIds; }

    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public String getParentPostId() { return parentPostId;

    }
    public void setParentPostId(String parentPostId) { this.parentPostId = parentPostId; }

    public List<String> getRepliesIds() {
        if (repliesIds == null) repliesIds = new ArrayList<>();
        return repliesIds;
    }
    public void setRepliesIds(List<String> repliesIds) { this.repliesIds = repliesIds; }


    public void attachMedia(File media) { this.media = media; }
    public void removeMedia() { this.media = null; }
    public boolean hasAttachment() { return media != null; }

    public String getMediaType() {
        if (media == null) return "None";
        if (media instanceof com.example.x.model.file.Photo) return "Photo";
        if (media instanceof com.example.x.model.file.Video) return "Video";
        return "Unknown";
    }

    public void incrementLikeCount() { this.totalLikes++; }
    public void incrementCommentCount() { this.totalComments++; }
    public void incrementViewCount() { this.totalViews++; }

    public void addHashtag(String hashtagId) {
        if (hashtagsIds == null) hashtagsIds = new ArrayList<>();
        if (!hashtagsIds.contains(hashtagId))
            hashtagsIds.add(hashtagId);
    }

    public void removeHashtag(String hashtagId) {
        if (hashtagsIds != null)
            hashtagsIds.remove(hashtagId);
    }

    public void addReply(String replyPostId) {
        if (repliesIds == null) repliesIds = new ArrayList<>();
        if (!repliesIds.contains(replyPostId))
            repliesIds.add(replyPostId);
        totalComments++;
    }

    public void decrementLikeCount() {
        this.totalLikes--;
    }

    public List<String> getLikedUserIds() {
        if (likedUserIds == null) likedUserIds = new ArrayList<>();
        return likedUserIds;
    }

    public void addLikedUserId(String userId) {
        if (likedUserIds == null) likedUserIds = new ArrayList<>();
        if (!likedUserIds.contains(userId)) {
            likedUserIds.add(userId);
        }
    }

    public void removeLikedUserId(String userId) {
        if (likedUserIds != null) {
            likedUserIds.remove(userId);
        }
    }

}