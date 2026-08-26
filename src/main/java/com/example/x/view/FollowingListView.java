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

public class FollowingListView {

    private DataManager dataManager;
    private User otherUser;

    @FXML private Label followingCountLabel;
    @FXML private VBox followingListContainer;

    @FXML
    public void initialize() {
        dataManager = DataManager.getInstance();
        otherUser = Main.getInstance().getViewingUser();

        if (otherUser != null) {
            loadFollowing();
        } else {
            followingCountLabel.setText("0 following");
        }
    }

    private void loadFollowing() {
        followingListContainer.getChildren().clear();

        if (otherUser == null) {
            followingCountLabel.setText("0 following");
            return;
        }

        List<String> followingIds = dataManager.getFollowingIds(otherUser.getId());

        if (followingIds.isEmpty()) {
            followingCountLabel.setText("0 following");
            return;
        }

        int count = 0;
        for (String followingId : followingIds) {
            Account following = dataManager.findUserById(followingId);
            if (following != null && following instanceof User) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/UserCard.fxml"));
                    AnchorPane card = loader.load();
                    UserCardView controller = loader.getController();
                    controller.setUserData((User) following, () -> {
                        try {
                            Main.getInstance().goToOtherprofile((User) following);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    followingListContainer.getChildren().add(card);
                    count++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        followingCountLabel.setText(count + " following");
    }

    @FXML
    void backClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToUserprofile();
    }

}