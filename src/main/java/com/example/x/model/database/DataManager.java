package com.example.x.model.database;

import com.example.x.model.account.Account;
import com.example.x.model.account.user.User;
import com.example.x.model.chat.ChatMessage;
import com.example.x.model.hashtag.Hashtag;
import com.example.x.model.notification.Notification;
import com.example.x.model.post.Post;
import com.example.x.model.report.Report;
import com.example.x.repository.*;

import java.util.List;

public class DataManager {
    private static DataManager instance;

    private UserRepository userRepository;
    private PostRepository postRepository;
    private HashtagRepository hashtagRepository;
    private MessageRepository messageRepository;
    private ReportRepository reportRepository;
    private NotificationRepository notificationRepository;

    private User currentUser;

    private DataManager() {
        this.userRepository = new UserRepository();
        this.postRepository = new PostRepository();
        this.hashtagRepository = new HashtagRepository();
        this.messageRepository = new MessageRepository();
        this.reportRepository = new ReportRepository();
        notificationRepository = new NotificationRepository();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public Account findUser(String username) {
        return userRepository.findByUsername(username);
    }

    public Account findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Account findUserById(String id) {
        return userRepository.findById(id);
    }

    public boolean addUser(User user) {
        return userRepository.add(user);
    }

    public List<Account> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Account> searchUsers(String query) {
        return userRepository.searchUsers(query);
    }

    public void updateOnlineStatus(String userId, boolean isOnline) {
        userRepository.updateOnlineStatus(userId, isOnline);
    }

    public void updateTokenBalance(String userId, int newBalance) {
        userRepository.updateTokenBalance(userId, newBalance);
    }

    public void deleteUser(String userId) {
        userRepository.delete(userId);
    }

    public int getFollowerCount(String userId) {
        return userRepository.getFollowerCount(userId);
    }

    public int getFollowingCount(String userId) {
        return userRepository.getFollowingCount(userId);
    }

    public boolean addPost(Post post) {
        return postRepository.add(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post findPostById(String postId) {
        return postRepository.findById(postId);
    }

    public List<Post> getPostsByAuthor(String authorId) {
        return postRepository.findByAuthorId(authorId);
    }

    public List<Post> searchPosts(String query) {
        return postRepository.searchPosts(query);
    }

    public List<Post> getMostLikedPosts(int limit) {
        return postRepository.getMostLikedPosts(limit);
    }

    public List<Post> getReplies(String postId) {
        return postRepository.getReplies(postId);
    }

    public void incrementViewCount(String postId) {
        postRepository.incrementViewCount(postId);
    }

    public void incrementLikeCount(String postId , String userId) {
        postRepository.incrementLikeCount(postId);
        userRepository.addLikedPost(userId, postId);

    }

    public void decrementLikeCount(String postId , String userId) {
        postRepository.decrementLikeCount(postId);
        userRepository.removeLikedPost(userId, postId);
    }

    public boolean hasUserLikedPost(String userId, String postId) {
        return userRepository.hasLikedPost(userId, postId);
    }

    public void incrementCommentCount(String postId) {
        postRepository.incrementCommentCount(postId);
    }

    public void deletePost(String postId) {
        Post post = postRepository.findById(postId);
        if (post == null) return;

        String parentPostId = post.getParentPostId();

        postRepository.delete(postId);

        if (parentPostId != null && !parentPostId.isEmpty()) {
            postRepository.decrementCommentCount(parentPostId);
        }
    }

    public boolean addHashtag(Hashtag hashtag) {
        return hashtagRepository.add(hashtag);
    }

    public Hashtag findHashtagByTitle(String title) {
        return hashtagRepository.findByTitle(title);
    }

    public List<Hashtag> getAllHashtags() {
        return hashtagRepository.findAll();
    }

    public List<Hashtag> searchHashtags(String query) {
        return hashtagRepository.searchHashtags(query);
    }

    public List<Hashtag> getMostPopularHashtags(int limit) {
        return hashtagRepository.getMostPopularHashtags(limit);
    }

    public void addPostToHashtag(String postId, String hashtagId) {
        hashtagRepository.addPostToHashtag(postId, hashtagId);
    }

    public List<Hashtag> getHashtagsForPost(String postId) {
        return hashtagRepository.getHashtagsForPost(postId);
    }

    public void syncPostHashtags(String postId, List<String> hashtagIds) {
        hashtagRepository.syncPostHashtags(postId, hashtagIds);
    }

    public boolean sendMessage(ChatMessage message) {
        return messageRepository.save(message);
    }

    public List<ChatMessage> getConversation(String userId1, String userId2) {
        return messageRepository.getConversation(userId1, userId2);
    }

    public List<ChatMessage> getMessagesForUser(String userId) {
        return messageRepository.getMessagesForUser(userId);
    }

    public List<ChatMessage> getUnreadMessages(String userId) {
        return messageRepository.getUnreadMessages(userId);
    }

    public int getUnreadCount(String userId) {
        return messageRepository.getUnreadCount(userId);
    }

    public void markMessageAsRead(int messageId) {
        messageRepository.markAsRead(messageId);
    }

    public void markAllAsRead(String userId, String otherUserId) {
        messageRepository.markAllAsRead(userId, otherUserId);
    }

    public void deleteMessageForEveryone(int messageId) {
        messageRepository.deleteForEveryone(messageId);
    }

    public boolean addReport(Report report) {
        return reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Report findReportById(int id) {
        return reportRepository.findById(id);
    }

    public List<Report> getReportsByStatus(String status) {
        return reportRepository.findByStatus(status);
    }

    public List<Report> getPendingReports() {
        return reportRepository.getPendingReports();
    }

    public List<Report> getReportsByReporter(String reporterId) {
        return reportRepository.findByReporter(reporterId);
    }

    public List<Report> getReportsByReportedUser(String reportedUserId) {
        return reportRepository.findByReportedUser(reportedUserId);
    }

    public boolean confirmReport(int id) {
        return reportRepository.confirmReport(id);
    }

    public boolean rejectReport(int id) {
        return reportRepository.rejectReport(id);
    }

    public boolean hasUserReported(String reporterId, String reportedContentId) {
        return reportRepository.hasUserReported(reporterId, reportedContentId);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            updateOnlineStatus(user.getId(), true);
        }
    }

    public void logoutCurrentUser() {
        if (currentUser != null) {
            updateOnlineStatus(currentUser.getId(), false);
        }
        currentUser = null;
    }

    public boolean addHashtagIfNotExists(Hashtag hashtag) {
        if (hashtagRepository.findByTitle(hashtag.getTitle()) == null) {
            return hashtagRepository.add(hashtag);
        }
        return false;
    }

    public void updatePost(Post post) {
        postRepository.update(post.getPostId(), post);
    }

    public void updateUser(Account account) {
        userRepository.update(account.getId(), account);
    }


    public int getLikeCount(String postId) {
        return postRepository.getLikeCount(postId);
    }

    public void addReply(String parentPostId, String replyPostId) {
        postRepository.addReply(parentPostId, replyPostId);
    }

    public void removeReply(String parentPostId, String replyPostId) {
        postRepository.removeReply(parentPostId, replyPostId);
        postRepository.decrementCommentCount(parentPostId);
    }

    public int getCommentCount(String postId) {
        return postRepository.getCommentCount(postId);
    }

    public void followUser(String followerId, String followingId) {
        userRepository.addFollower(followerId, followingId);
    }

    public void unfollowUser(String followerId, String followingId) {
        userRepository.removeFollower(followerId, followingId);
    }

    public boolean isFollowing(String followerId, String followingId) {
        return userRepository.isFollowing(followerId, followingId);
    }

    public int getViewCount(String postId) {
        return postRepository.getViewCount(postId);
    }

    public List<String> getFollowerIds(String userId) {
        return userRepository.getFollowerIds(userId);
    }

    public List<String> getFollowingIds(String userId) {
        return userRepository.getFollowingIds(userId);
    }

    public List<String> getLikerIds(String postId) {
        return postRepository.getLikerIds(postId);
    }

    public List<Post> getRepliesByAuthor(String authorId) {
        return postRepository.findByAuthorId(authorId);
    }

    public List<Post> getMediaByAuthor(String authorId) {
        return postRepository.findByAuthorId(authorId);
    }

    public void likePost(String postId, String userId) {

    }

    public void saveNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    public List<Notification> getNotifications(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    public int getUnreadNotificationCount(String userId) {
        return notificationRepository.getUnreadCount(userId);
    }

    public void markNotificationAsRead(String notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    public void markAllNotificationsAsRead(String userId) {
        notificationRepository.markAllAsRead(userId);
    }
}