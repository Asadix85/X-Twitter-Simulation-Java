package com.example.x.view;

import com.example.x.Main;
import com.example.x.controller.SearchPageController;
import com.example.x.model.account.Account;
import com.example.x.model.hashtag.Hashtag;
import com.example.x.model.post.Post;
import com.example.x.view.card.PostCardView;
import com.example.x.view.card.UserCardView;
import com.example.x.view.card.HashtagCardView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SearchPageView {

    private SearchPageController controller;

    @FXML private TextField searchTextField;
    @FXML private VBox SearchList;
    @FXML private ComboBox<String> searchTypeComboBox;

    @FXML
    public void initialize() {
        controller = new SearchPageController(this);

        searchTypeComboBox.getItems().addAll("Username", "Keyword", "Hashtag");
        searchTypeComboBox.setValue("Username");

        searchTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            performSearch();
        });

        searchTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            performSearch();
        });
    }

    private void performSearch() {
        String selectedType = searchTypeComboBox.getValue();
        String searchText = searchTextField.getText();
        controller.handleSearch(selectedType, searchText);
    }

    @FXML
    void homeClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToHomePage();
    }

    @FXML
    void newButtonClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToNewPostPage();
    }

    @FXML
    void profileClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToUserprofile();
    }

    @FXML void messagesClicked(MouseEvent event) throws IOException {Main.getInstance().goToMessagesList(); }

    public void clearResults() {
        SearchList.getChildren().clear();
    }

    public void addUserCard(Account user, Runnable onClick) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/UserCard.fxml"));
            AnchorPane card = loader.load();
            UserCardView controller = loader.getController();
            controller.setUserData(user, onClick);
            SearchList.getChildren().add(card);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPostCard(Post post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/PostCard.fxml"));
            AnchorPane card = loader.load();
            PostCardView controller = loader.getController();
            controller.setPostData(post);
            SearchList.getChildren().add(card);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHashtagCard(Hashtag hashtag, Runnable onClick) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/HashtagCard.fxml"));
            AnchorPane card = loader.load();
            HashtagCardView controller = loader.getController();
            controller.setHashtagData(hashtag, onClick);
            SearchList.getChildren().add(card);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}