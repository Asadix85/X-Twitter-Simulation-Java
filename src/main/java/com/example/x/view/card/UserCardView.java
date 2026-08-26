package com.example.x.view.card;

import com.example.x.Main;
import com.example.x.model.account.Account;
import com.example.x.model.account.user.User;
import com.example.x.model.account.user.premiumUser.BluePremiumUser;
import com.example.x.model.account.user.premiumUser.GoldPremiumUser;
import com.example.x.model.chat.ChatMessage;
import com.example.x.model.database.DataManager;
import com.example.x.repository.MessageRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.List;

public class UserCardView {

    DataManager dataManager = DataManager.getInstance();
    MessageRepository messageRepository = new MessageRepository();
    User currentUser = dataManager.getCurrentUser();

    @FXML private ImageView userProfilePic;
    @FXML private Label fullNameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label bioLabel;
    @FXML private ImageView AccountLabel;
    @FXML private Button followButton;
    @FXML private Button chatButton;
    @FXML private Button countOfUnreadMessage;

    private Account account;
    private Runnable onCardClick;

    public void setUserData(Account account, Runnable onClick) {
        this.account = account;
        this.onCardClick = onClick;

        fullNameLabel.setText(account.getFullName() != null ? account.getFullName() : account.getUsername());
        usernameLabel.setText("@" + account.getUsername());

        if (account instanceof User) {
            bioLabel.setText(((User) account).getBio() != null ? ((User) account).getBio() : "");
        }

        if (account instanceof BluePremiumUser) {
            AccountLabel.setImage(new Image(getClass().getResourceAsStream("/Icon/approvalB_480px.png")));
            AccountLabel.setVisible(true);
        } else if (account instanceof GoldPremiumUser) {
            AccountLabel.setVisible(true);
        } else {
            AccountLabel.setVisible(false);
        }

        if (account.getId().equals(currentUser.getId())) {
            followButton.setVisible(false);
            followButton.setManaged(false);
        } else {
            followButton.setVisible(true);
            followButton.setManaged(true);
            updateFollowButton();
        }

        if (account.getId().equals(currentUser.getId())) {
            chatButton.setVisible(false);
            chatButton.setManaged(false);
        } else {
            chatButton.setVisible(true);
            chatButton.setManaged(true);
        }
        loadUnreadCount(account.getId());
    }

    private void updateFollowButton() {
        boolean isFollowing = dataManager.isFollowing(currentUser.getId(), account.getId());

        if (isFollowing) {
            followButton.setText("Following");
            followButton.setStyle("-fx-background-color: green; -fx-background-radius: 25;");
        } else {
            followButton.setText("Follow");
            followButton.setStyle("-fx-background-color: blue; -fx-background-radius: 25;");
        }
    }

    private void loadUnreadCount(String otherUserId) {
        if (currentUser == null || otherUserId.equals(currentUser.getId())) {
            countOfUnreadMessage.setVisible(false);
            countOfUnreadMessage.setManaged(false);
            return;
        }

        int unreadCount = messageRepository.getUnreadCountBetween(currentUser.getId(), otherUserId);

        if (unreadCount > 0) {
            countOfUnreadMessage.setText(String.valueOf(unreadCount));
            countOfUnreadMessage.setManaged(true);
        } else {
            countOfUnreadMessage.setText(String.valueOf(0));
        }
    }

    @FXML
    void followButtonClicked(MouseEvent event) {
        if (account == null || !(account instanceof User)) return;

        if (followButton.getText().equals("Follow")) {
            dataManager.followUser(currentUser.getId(), account.getId());

            currentUser.addFollowingIds((User) account);
            ((User) account).addFollowerIds(currentUser);

            followButton.setText("Following");
            followButton.setStyle("-fx-background-color: green; -fx-background-radius: 25;");
        } else {
            dataManager.unfollowUser(currentUser.getId(), account.getId());
            currentUser.removeFollowingIds((User) account);
            ((User) account).removeFollowerIds(currentUser);

            followButton.setText("Follow");
            followButton.setStyle("-fx-background-color: blue; -fx-background-radius: 25;");
        }
    }

    @FXML
    void userProfileClicked(MouseEvent event) throws IOException {
        if (account != null && account instanceof User) {
            Main.getInstance().goToOtherprofile((User) account);
        }
    }

    @FXML
    void chatButtonClicked(MouseEvent event) {
        if (account != null) {
            try {
                Main.getInstance().goToChatPage(account.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}