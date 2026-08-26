package com.example.x.model.database;

import com.example.x.model.account.user.User;
import com.example.x.model.account.user.normalUser.NormalUser;
import com.example.x.model.account.user.premiumUser.BluePremiumUser;
import com.example.x.model.account.user.premiumUser.GoldPremiumUser;
import com.example.x.model.chat.ChatMessage;
import com.example.x.model.hashtag.Hashtag;
import com.example.x.model.post.Post;
import com.example.x.model.report.Report;
import com.example.x.model.account.Account;
import com.example.x.model.account.admin.Admin;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private static Database instance;
    private User currentUser;
    private List<Account> users;
    private List<Post> posts;
    private List<Hashtag> hashtags;
    private List<Report> reports;
    private List<ChatMessage> messages;
    public List<ChatMessage> getMessages() { return messages; }
    public void addMessage(ChatMessage msg) { messages.add(msg); }

    private Database() {
        this.users = new ArrayList<>();
        this.posts = new ArrayList<>();
        this.hashtags = new ArrayList<>();
        this.reports = new ArrayList<>();
        messages = new ArrayList<>();
    }

    public static Database getInstance() {
        if (instance == null)
            instance = new Database();
        return instance;
    }

    public void initalizeDefaulfs() {
        Admin admin = new Admin("SDX", "1385", "mohammad8asadi5@gmail.com");
        users.add(admin);
        NormalUser test = new NormalUser("Mohammad Asadi" , "09908630528" , "ASADIX" , "1385" , "mohammad@gmail.com");
        users.add(test);
        BluePremiumUser btest = new BluePremiumUser("Mohammad Asadi" , "09908630528" , "Asadix" , "1385" , "mohammdad@gmail.com");
        users.add(btest);
        GoldPremiumUser gtest = new GoldPremiumUser("Mohammad Asadi" , "09908630528" , "asadix" , "1385" , "mohamdmdad@gmail.com");
        users.add(gtest);

        hashtags.add(new Hashtag("#News"));
        hashtags.add(new Hashtag("#Sports"));
        hashtags.add(new Hashtag("#Education"));
        hashtags.add(new Hashtag("#Iran"));
    }

    public Account findUser(String username) {
        for (Account u : users) {
            if (u.getUsername().equals(username))
                return u;
        }
        return null;
    }
    public void addUser(User user) {
        users.add(user);
    }

    public void addPost(Post post) {
        posts.add(post);
    }

    public User getCurrentUser() {
        return currentUser;
    }
    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    public List<Account> getUsers() {
        return users;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public List<Hashtag> getHashtags() {
        return hashtags;
    }

    public List<Report> getReports() {
        return reports;
    }


    public List<Account> searchUsers(String query) {
        List<Account> results = new ArrayList<>();
        for (Account user : users) {
            if (user.getUsername().toLowerCase().contains(query) || (user.getFullName() != null && user.getFullName().toLowerCase().contains(query))) {
                results.add(user);
            }
        }
        return results;
    }

    public List<Post> searchPosts(String query) {
        List<Post> results = new ArrayList<>();
        for (Post post : posts) {
            if (post.getPostText().toLowerCase().contains(query)) {
                results.add(post);
            }
        }
        return results;
    }

    public List<Hashtag> searchHashtags(String query) {
        List<Hashtag> results = new ArrayList<>();
        for (Hashtag hashtag : hashtags) {
            if (hashtag.getTitle().toLowerCase().contains(query)) {
                results.add(hashtag);
            }
        }
        return results;
    }

    public Account getUserById(String userId) {
        for (Account user : users) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }
    public Hashtag findHashtagByTitle(String title) {
        for (Hashtag h : hashtags) {
            if (h.getTitle().equalsIgnoreCase(title)) {
                return h;
            }
        }
        return null;
    }

    public void addHashtag(Hashtag hashtag) {
        if (findHashtagByTitle(hashtag.getTitle()) == null) {
            hashtags.add(hashtag);
        }
    }

    public Post getPostById(String reportedContentId) {
        for (Post post : posts) {
            if (post.getPostId().equals(reportedContentId)) {
                return post;
            }
        }
        return null;
    }

    public Hashtag getHashtagById(String hashtagId) {
        for (Hashtag hashtag : hashtags) {
            if (hashtag.getHashtagId().equals(hashtagId)) {
                return hashtag;
            }
        }
        return null;
    }


}
