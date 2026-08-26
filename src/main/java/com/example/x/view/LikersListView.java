package com.example.x.view;

import com.example.x.Main;
import com.example.x.model.account.Account;
import com.example.x.model.database.DataManager;
import com.example.x.model.post.Post;
import com.example.x.view.card.UserCardView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class LikersListView {

    private DataManager dataManager;
    private Post post;

    @FXML private Label likersCountLabel;
    @FXML private VBox likersListContainer;

    @FXML
    public void initialize() {
        dataManager = DataManager.getInstance();
    }

    public void setPost(Post post) {
        this.post = post;
        loadLikers();
    }

    private void loadLikers() {
        likersListContainer.getChildren().clear();

        if (post == null) {
            likersCountLabel.setText("0 likes");
            return;
        }

        List<String> likerIds = dataManager.getLikerIds(post.getPostId());

        if (likerIds.isEmpty()) {
            likersCountLabel.setText("0 likes");
            return;
        }

        int count = 0;
        for (String userId : likerIds) {
            Account user = dataManager.findUserById(userId);
            if (user != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/UserCard.fxml"));
                    AnchorPane card = loader.load();
                    UserCardView controller = loader.getController();
                    controller.setUserData(user, () -> openProfile(user));
                    likersListContainer.getChildren().add(card);
                    count++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        likersCountLabel.setText(count + " likes");
    }

    private void openProfile(Account user) {
        if (user instanceof com.example.x.model.account.user.User) {
            try {
                Main.getInstance().goToOtherprofile((com.example.x.model.account.user.User) user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void backClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToHomePage();
    }
}