package com.example.x.view;

import com.example.x.Main;
import com.example.x.controller.UserProfileController;
import com.example.x.model.account.Account;
import com.example.x.model.account.AccountBadge;
import com.example.x.model.database.DataManager;
import com.example.x.model.post.Post;

import com.example.x.view.card.PostCardView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class UserProfileView {

    private UserProfileController userProfileController;
    private DataManager dataManager;

    @FXML private ImageView ProfilePic;
    @FXML private ImageView AccountLabel;
    @FXML private Label bioLabel;
    @FXML private Label totalTokensLabel;
    @FXML private DatePicker birthdayTF;
    @FXML private Button blueTickBuy;
    @FXML private Label fullNameLabel;
    @FXML private Button goldTickBuy;
    @FXML private VBox mediaList;
    @FXML private Label memberShipDateLabel;
    @FXML private TextField newBioTF;
    @FXML private HBox blueBadgeHB;
    @FXML private TextField newFullNameTF;
    @FXML private TextField newPasswordTF;
    @FXML private TextField newEmailTF;
    @FXML private VBox postsList;
    @FXML private VBox repliesList;
    @FXML private Label totalFollowerLabel;
    @FXML private Label totalFollowingLabel;
    @FXML private Label usernameLabel;
    @FXML private TextField tokenAmountTB;

    @FXML
    public void initialize() throws IOException {
        dataManager = DataManager.getInstance();
        userProfileController = new UserProfileController();
        userProfileController.setView(this);
        loadUserProfile();
    }

    private void loadUserProfile() throws IOException {
        userProfileController.refreshUserData();

        fullNameLabel.setText(userProfileController.getName());
        totalTokensLabel.setText(String.valueOf(userProfileController.getTokens()));
        memberShipDateLabel.setText(userProfileController.getMemberShipDate().toString());
        usernameLabel.setText("@" + userProfileController.getUsername());
        bioLabel.setText(userProfileController.getBio());

        int followerCount = dataManager.getFollowerCount(userProfileController.getCurrentUserId());
        int followingCount = dataManager.getFollowingCount(userProfileController.getCurrentUserId());
        totalFollowerLabel.setText(String.valueOf(followerCount));
        totalFollowingLabel.setText(String.valueOf(followingCount));

        AccountBadge badge = userProfileController.getAccountBadge();
        if (badge == AccountBadge.BLUE_BADGE) {
            AccountLabel.setImage(new Image(getClass().getResourceAsStream("/Icon/approvalB_480px.png")));
            AccountLabel.setVisible(true);
            blueTickBuy.setText("Purchased");
            blueTickBuy.setDisable(true);
        }
        if (badge == AccountBadge.GOLD_BADGE) {
            AccountLabel.setVisible(true);
            goldTickBuy.setText("Purchased");
            goldTickBuy.setDisable(true);
        }

        loadUserPosts();
        loadUserReplies();
        loadUserMedia();
    }

    private void loadUserPosts() throws IOException {
        postsList.getChildren().clear();
        String userId = userProfileController.getCurrentUserId();
        List<Post> posts = dataManager.getPostsByAuthor(userId);

        for (Post post : posts) {
            if (!post.isDeleted()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/PostCard.fxml"));
                AnchorPane card = loader.load();
                PostCardView controller = loader.getController();
                controller.setPostData(post);
                postsList.getChildren().add(card);
            }
        }
    }

    private void loadUserReplies() throws IOException {
        repliesList.getChildren().clear();

        String userId = userProfileController.getCurrentUserId();
        List<Post> replies = dataManager.getRepliesByAuthor(userId);

        for (Post reply : replies) {
            if (!reply.isDeleted()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/PostCard.fxml"));
                AnchorPane card = loader.load();
                PostCardView controller = loader.getController();
                controller.setPostData(reply);
                repliesList.getChildren().add(card);
            }
        }
    }

    private void loadUserMedia() throws IOException {
        mediaList.getChildren().clear();

        String userId = userProfileController.getCurrentUserId();
        List<Post> media = dataManager.getMediaByAuthor(userId);

        for (Post post : media) {
            if (post.hasAttachment() && !post.isDeleted()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/PostCard.fxml"));
                AnchorPane card = loader.load();
                PostCardView controller = loader.getController();
                controller.setPostData(post);
                mediaList.getChildren().add(card);
            }
        }
    }

    @FXML
    void PayButtonClicked(MouseEvent event) {
        String tokenAmount = tokenAmountTB.getText();
        if (!tokenAmount.isEmpty()) {
            int amount = Integer.parseInt(tokenAmount);
            userProfileController.setTokens(amount);
            totalTokensLabel.setText(String.valueOf(userProfileController.getTokens()));
            tokenAmountTB.clear();
        }
    }

    @FXML
    void blueTickBuyClicked(MouseEvent event) {
        if (userProfileController.handleBuyBlueBadge()) {
            blueTickBuy.setText("Purchased");
            blueTickBuy.setDisable(true);
            totalTokensLabel.setText(String.valueOf(userProfileController.getTokens()));
        }
    }

    @FXML
    void goldTickBuyClicked(MouseEvent event) {
        if (userProfileController.handleBuyGoldBadge()) {
            goldTickBuy.setText("Purchased");
            goldTickBuy.setDisable(true);
            totalTokensLabel.setText(String.valueOf(userProfileController.getTokens()));
        }
    }

    @FXML
    void okEditButtonClicked(MouseEvent event) throws IOException {
        if (!newFullNameTF.getText().isBlank()) {
            fullNameLabel.setText(newFullNameTF.getText());
            userProfileController.setName(newFullNameTF.getText());
            newFullNameTF.clear();
        }
        if (!newEmailTF.getText().isBlank()) {
            userProfileController.setNewEmail(newEmailTF.getText());
            newEmailTF.clear();
        }
        if (!newPasswordTF.getText().isBlank()) {
            userProfileController.setPassword(newPasswordTF.getText());
            newPasswordTF.clear();
        }
        if (!newBioTF.getText().isBlank()) {
            bioLabel.setText(newBioTF.getText());
            userProfileController.setBio(newBioTF.getText());
            newBioTF.clear();
        }
        if (birthdayTF.getValue() != null) {
            userProfileController.setBirthday(birthdayTF.getValue());
            birthdayTF.setValue(null);
        }
        userProfileController.saveUserData();
        loadUserProfile();
    }

    @FXML void SearchClicked(MouseEvent event) throws IOException { Main.getInstance().goToSearchPage(); }
    @FXML void homeClicked(MouseEvent event) throws IOException { Main.getInstance().goToHomePage(); }
    @FXML void newButtonClicked(MouseEvent event) throws IOException { Main.getInstance().goToNewPostPage(); }
    @FXML void messagesClicked(MouseEvent event) throws IOException {Main.getInstance().goToMessagesList(); }

    @FXML
    void followersList(MouseEvent event) throws IOException {
        Main.getInstance().goToFollowersList(dataManager.getCurrentUser());
    }

    @FXML
    void followingList(MouseEvent event) throws IOException {
        Main.getInstance().goToFollowingList(dataManager.getCurrentUser());
    }
}