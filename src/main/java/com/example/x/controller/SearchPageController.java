package com.example.x.controller;

import com.example.x.Main;
import com.example.x.model.account.Account;
import com.example.x.model.account.user.User;
import com.example.x.model.database.DataManager;
import com.example.x.model.hashtag.Hashtag;
import com.example.x.model.post.Post;
import com.example.x.view.SearchPageView;

import java.io.IOException;
import java.util.List;

public class SearchPageController {

    private DataManager dataManager ;
    private SearchPageView searchPageView;

    public SearchPageController(SearchPageView searchPageView) {
        this.dataManager  = DataManager.getInstance();
        this.searchPageView = searchPageView;
    }

    public void handleSearch(String selectedType, String searchText) {
        searchPageView.clearResults();

        if (searchText == null || searchText.trim().isEmpty()) {
            return;
        }

        String query = searchText.trim().toLowerCase();

        if (selectedType.equals("Username")) {
            List<Account> users = dataManager.searchUsers(query);
            for (Account user : users) {
                searchPageView.addUserCard(user, () -> {
                    try {
                        if(user instanceof User)
                            Main.getInstance().goToOtherprofile((User) user);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        else if (selectedType.equals("Keyword")) {
            List<Post> posts = dataManager.searchPosts(query);
            for (Post post : posts) {
                searchPageView.addPostCard(post);
            }
        }
        else if (selectedType.equals("Hashtag")) {
            List<Hashtag> hashtags = dataManager.searchHashtags(query);
            for (Hashtag hashtag : hashtags) {
                searchPageView.addHashtagCard(hashtag, () -> {
                    try {
                        Main.getInstance().goToHashtagPage(hashtag);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }
}