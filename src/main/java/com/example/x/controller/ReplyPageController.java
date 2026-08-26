package com.example.x.controller;

import com.example.x.model.database.DataManager;
import com.example.x.model.post.Post;
import com.example.x.view.ReplyPageView;
import com.example.x.view.card.PostCardView;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.List;

public class ReplyPageController {

    private final DataManager dataManager;
    private final ReplyPageView view;

    public ReplyPageController(ReplyPageView view) {
        this.dataManager = DataManager.getInstance();
        this.view = view;
    }

    public void loadPostWithReplies(Post post) {
        view.getMainPostContainer().getChildren().clear();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/PostCard.fxml"));
            AnchorPane card = loader.load();
            PostCardView controller = loader.getController();
            controller.setPostData(post);
            view.getMainPostContainer().getChildren().add(card);
        } catch (IOException e) {
            e.printStackTrace();
        }

        view.getRepliesContainer().getChildren().clear();

        List<Post> replies = dataManager.getReplies(post.getPostId());

        if (replies.isEmpty()) {
            view.setReplyCount(0);
            return;
        }

        int replyCount = 0;
        for (Post reply : replies) {
            if (reply != null && !reply.isDeleted()) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/PostCard.fxml"));
                    AnchorPane card = loader.load();
                    PostCardView controller = loader.getController();
                    controller.setPostData(reply);
                    view.getRepliesContainer().getChildren().add(card);
                    replyCount++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        view.setReplyCount(replyCount);
    }
}