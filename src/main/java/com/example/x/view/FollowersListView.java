package com.example.x.view;

import com.example.x.Main;
import com.example.x.model.account.Account;
import com.example.x.model.account.user.User;
import com.example.x.model.database.DataManager;
import com.example.x.view.card.UserCardView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class FollowersListView {

    private DataManager dataManager;
    private User otherUser;

    @FXML private Label followersCountLabel;
    @FXML private VBox followersListContainer;

    @FXML
    public void initialize() {
        dataManager = DataManager.getInstance();
        otherUser = Main.getInstance().getViewingUser();

        if (otherUser != null) {
            loadFollowers();
        } else {
            followersCountLabel.setText("0 followers");
        }
    }

    private void loadFollowers() {
        followersListContainer.getChildren().clear();

        if (otherUser == null) {
            followersCountLabel.setText("0 followers");
            return;
        }

        List<String> followerIds = dataManager.getFollowerIds(otherUser.getId());

        if (followerIds.isEmpty()) {
            followersCountLabel.setText("0 followers");
            return;
        }

        int count = 0;
        for (String followerId : followerIds) {
            Account follower = dataManager.findUserById(followerId);
            if (follower != null && follower instanceof User) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/UserCard.fxml"));
                    AnchorPane card = loader.load();
                    UserCardView controller = loader.getController();
                    controller.setUserData((User) follower, () -> {
                        try {
                            Main.getInstance().goToOtherprofile((User) follower);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    followersListContainer.getChildren().add(card);
                    count++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        followersCountLabel.setText(count + " followers");
    }

    @FXML
    void backClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToUserprofile();
    }
}