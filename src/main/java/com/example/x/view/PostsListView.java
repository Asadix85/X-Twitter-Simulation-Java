package com.example.x.view;

import com.example.x.Main;
import com.example.x.controller.AdminPageController;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class PostsListView {
    private AdminPageController controller;

    @FXML private TextField postSearchField;
    @FXML private VBox postsListContainer;

    @FXML
    public void initialize() throws IOException {
        controller = new AdminPageController();
        controller.loadAllPosts(this);
    }

    @FXML
    private void searchPosts() throws IOException {
        controller.searchPosts(this, postSearchField.getText());
    }

    @FXML
    private void backToAdminClicked() throws IOException {
        Main.getInstance().goToAdminPage();
    }

    public VBox getPostsListContainer() { return postsListContainer; }
}