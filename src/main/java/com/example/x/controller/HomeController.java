package com.example.x.controller;

import com.example.x.model.account.Account;
import com.example.x.model.account.user.User;
import com.example.x.model.database.DataManager;
import com.example.x.model.hashtag.Hashtag;
import com.example.x.model.post.Post;
import com.example.x.view.card.HashtagCardView;
import com.example.x.view.HomePageView;
import com.example.x.view.card.PostCardView;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HomeController {

    private final DataManager dataManager ;
    private final User currentUser;
    private final HomePageView view;

    public HomeController(HomePageView view) {
        this.dataManager  = DataManager.getInstance();
        this.currentUser = dataManager.getCurrentUser();
        this.view = view;
    }

    public void loadFeed() throws IOException {
        view.getVBoxMain().getChildren().clear();

        List<Post> feedPosts;

        if (currentUser.getFollowingIds().isEmpty()) {
            feedPosts = dataManager.getAllPosts().stream()
                    .sorted(Comparator.comparingInt(Post::getTotalLikes).reversed())
                    .limit(10)
                    .collect(Collectors.toList());
        } else {
            feedPosts = dataManager.getAllPosts().stream()
                    .filter(p -> currentUser.getFollowingIds().contains(p.getAuthorId()))
                    .sorted(Comparator.comparing(Post::getPublishDate).reversed())
                    .limit(10)
                    .collect(Collectors.toList());
        }

        List<Hashtag> userHashtags = currentUser.getPersonalizationHashtags();
        if (userHashtags != null && !userHashtags.isEmpty()) {
            for (Hashtag hashtag : userHashtags) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/HashtagCard.fxml"));
                AnchorPane card = loader.load();
                HashtagCardView controller = loader.getController();
                controller.setHashtagData(hashtag, () -> {
                    System.out.println("Clicked: " + hashtag.getTitle());
                });
                view.getVBoxMain().getChildren().add(card);
            }
        }

        for (Post post : feedPosts) {
            Account author = dataManager.findUserById(post.getAuthorId());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/PostCard.fxml"));
            AnchorPane card = loader.load();
            PostCardView controller = loader.getController();
            controller.setPostData(post);
            view.getVBoxMain().getChildren().add(card);
        }
    }
}