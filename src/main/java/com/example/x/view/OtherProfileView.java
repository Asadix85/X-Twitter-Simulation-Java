package com.example.x.view;

import com.example.x.Main;
import com.example.x.controller.OtherProfileController;
import com.example.x.model.account.AccountBadge;
import com.example.x.model.account.user.User;
import com.example.x.model.database.DataManager;
import com.example.x.model.post.Post;
import com.example.x.view.card.PostCardView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.List;

public class OtherProfileView {

    private OtherProfileController controller;
    private DataManager dataManager = DataManager.getInstance();
    private User currentUser;
    private User otherUser;

    @FXML private ImageView ProfilePic;
    @FXML private Label bioLabel;
    @FXML private Button followButton;
    @FXML private Button chatButton;
    @FXML private Label fullNameLabel;
    @FXML private VBox mediaList;
    @FXML private Label memberShipDateLabel;
    @FXML private VBox postsList;
    @FXML private VBox repliesList;
    @FXML private ImageView AccountLabel;
    @FXML private Button reportButton;
    @FXML private Label totalFollowerLabel;
    @FXML private Label totalFollowingLabel;
    @FXML private Label usernameLabel;

    @FXML
    public void initialize() {
        currentUser = dataManager.getCurrentUser();
        otherUser = Main.getInstance().getViewingUser();

        controller = new OtherProfileController();
        controller.setOtherUser(otherUser);

        fullNameLabel.setText(controller.getName());
        usernameLabel.setText("@" + controller.getUsername());
        bioLabel.setText(controller.getBio() != null ? controller.getBio() : "No bio yet.");
        memberShipDateLabel.setText(controller.getMemberShipDate().toString());

        int followerCount = dataManager.getFollowerCount(otherUser.getId());
        int followingCount = dataManager.getFollowingCount(otherUser.getId());
        totalFollowerLabel.setText(String.valueOf(followerCount));
        totalFollowingLabel.setText(String.valueOf(followingCount));

        if (controller.getAccountBadge().equals(AccountBadge.BLUE_BADGE)) {
            AccountLabel.setImage(new Image(getClass().getResourceAsStream("/Icon/approvalB_480px.png")));
            AccountLabel.setVisible(true);
        }
        if (controller.getAccountBadge().equals(AccountBadge.GOLD_BADGE)) {
            AccountLabel.setVisible(true);
        }

        boolean isFollowing = dataManager.isFollowing(currentUser.getId(), otherUser.getId());
        if (isFollowing) {
            followButton.setText("Following");
            followButton.setStyle("-fx-background-color: green; -fx-background-radius: 25;");
        } else {
            followButton.setText("Follow");
            followButton.setStyle("-fx-background-color: blue; -fx-background-radius: 25;");
        }

        if (chatButton != null) {
            chatButton.setVisible(true);
            chatButton.setManaged(true);
        }

        try {
            loadPostsList();
            loadRepliesList();
            loadMediaList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPostsList() throws IOException {
        postsList.getChildren().clear();

        List<Post> posts = dataManager.getPostsByAuthor(otherUser.getId());

        for (Post post : posts) {
            if (!post.isDeleted()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/PostCard.fxml"));
                AnchorPane card = loader.load();
                PostCardView cardController = loader.getController();
                cardController.setPostData(post);
                postsList.getChildren().add(card);
            }
        }
    }

    private void loadRepliesList() throws IOException {
        repliesList.getChildren().clear();

        List<Post> replies = dataManager.getRepliesByAuthor(otherUser.getId());

        for (Post reply : replies) {
            if (!reply.isDeleted()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/PostCard.fxml"));
                AnchorPane card = loader.load();
                PostCardView cardController = loader.getController();
                cardController.setPostData(reply);
                repliesList.getChildren().add(card);
            }
        }
    }

    private void loadMediaList() throws IOException {
        mediaList.getChildren().clear();

        List<Post> media = dataManager.getMediaByAuthor(otherUser.getId());

        for (Post post : media) {
            if (post.hasAttachment() && !post.isDeleted()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/PostCard.fxml"));
                AnchorPane card = loader.load();
                PostCardView cardController = loader.getController();
                cardController.setPostData(post);
                mediaList.getChildren().add(card);
            }
        }
    }

    @FXML
    void followersList(MouseEvent event) throws IOException {
        Main.getInstance().goToFollowersList(otherUser);
    }

    @FXML
    void followingList(MouseEvent event) throws IOException {
        Main.getInstance().goToFollowingList(otherUser);
    }

    @FXML
    void followButtonClicked(MouseEvent event) {
        if (followButton.getText().equals("Follow")) {
            dataManager.followUser(currentUser.getId(), otherUser.getId());

            currentUser.addFollowingIds(otherUser);
            otherUser.addFollowerIds(currentUser);

            followButton.setText("Following");
            followButton.setStyle("-fx-background-color: green; -fx-background-radius: 25;");
        } else {
            dataManager.unfollowUser(currentUser.getId(), otherUser.getId());

            currentUser.removeFollowingIds(otherUser);
            otherUser.removeFollowerIds(currentUser);

            followButton.setText("Follow");
            followButton.setStyle("-fx-background-color: blue; -fx-background-radius: 25;");
        }
        int followerCount = dataManager.getFollowerCount(otherUser.getId());
        totalFollowerLabel.setText(String.valueOf(followerCount));
    }

    @FXML
    void reportButtonClicked(MouseEvent event) {
        controller.addToReportsList(currentUser);
        reportButton.setText("Reported");
        reportButton.setDisable(true);
    }

    @FXML
    void backClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToHomePage();
    }

    @FXML
    void chatButtonClicked(MouseEvent event) {
        if (otherUser != null) {
            try {
                Main.getInstance().goToChatPage(otherUser.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}