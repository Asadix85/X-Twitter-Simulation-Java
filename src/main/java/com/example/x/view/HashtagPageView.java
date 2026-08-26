package com.example.x.view;

import com.example.x.Main;
import com.example.x.controller.HashtagPageController;
import com.example.x.model.hashtag.Hashtag;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class HashtagPageView {

    private HashtagPageController controller;
    private Hashtag currentHashtag;

    @FXML private Label hashtagTitleLabel;
    @FXML private Label postCountLabel;
    @FXML private VBox hashtagPostsContainer;

    @FXML
    public void initialize() {
        controller = new HashtagPageController(this);
        currentHashtag = Main.getInstance().getCurrentHashtag();
        if (currentHashtag != null) {
            setHashtag(currentHashtag);
        } else {
            hashtagTitleLabel.setText("#Unknown");
        }
    }

    public void setHashtag(Hashtag hashtag) {
        this.currentHashtag = hashtag;
        hashtagTitleLabel.setText(hashtag.getTitle());
        controller.loadHashtagPosts(hashtag);
    }

    public VBox getHashtagPostsContainer() {
        return hashtagPostsContainer;
    }

    public void setPostCount(int count) {
        postCountLabel.setText(count + " posts");
    }

    @FXML
    void backClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToHomePage();
    }
}