package com.example.x.controller;

import com.example.x.model.database.DataManager;
import com.example.x.model.hashtag.Hashtag;
import com.example.x.model.post.Post;
import com.example.x.view.HashtagPageView;
import com.example.x.view.card.PostCardView;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HashtagPageController {

    private final DataManager dataManager ;
    private final HashtagPageView view;

    public HashtagPageController(HashtagPageView view) {
        this.dataManager = DataManager.getInstance();
        this.view = view;
    }

    public void loadHashtagPosts(Hashtag hashtag) {
        view.getHashtagPostsContainer().getChildren().clear();

        List<Post> hashtagPosts = new ArrayList<>();

        for (Post post : dataManager.getAllPosts()) {
            if (post.getHashtagsIds() != null && post.getHashtagsIds().contains(hashtag.getHashtagId())) {
                if (!post.isDeleted()) {
                    hashtagPosts.add(post);
                }
            }
        }

        view.setPostCount(hashtagPosts.size());

        for (Post post : hashtagPosts) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/PostCard.fxml"));
                AnchorPane card = loader.load();
                PostCardView controller = loader.getController();
                controller.setPostData(post);
                view.getHashtagPostsContainer().getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}