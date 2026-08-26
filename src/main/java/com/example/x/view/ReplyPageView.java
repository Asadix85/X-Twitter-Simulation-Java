package com.example.x.view;

import com.example.x.Main;
import com.example.x.controller.ReplyPageController;
import com.example.x.model.post.Post;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ReplyPageView {

    private ReplyPageController controller;
    private Post post;

    @FXML private VBox mainPostContainer;
    @FXML private Label replyCountLabel;
    @FXML private VBox repliesContainer;

    @FXML
    public void initialize() {
        controller = new ReplyPageController(this);
    }

    public void setPost(Post post) {
        this.post = post;
        controller.loadPostWithReplies(post);
    }

    public VBox getMainPostContainer() {
        return mainPostContainer;
    }

    public VBox getRepliesContainer() {
        return repliesContainer;
    }

    public void setReplyCount(int count) {
        replyCountLabel.setText(count + " replies");
    }

    @FXML
    void backClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToHomePage();
    }
}