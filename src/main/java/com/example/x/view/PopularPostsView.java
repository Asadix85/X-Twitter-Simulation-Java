package com.example.x.view;

import com.example.x.Main;
import com.example.x.controller.AdminPageController;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class PopularPostsView {
    private AdminPageController controller;

    @FXML private VBox popularPostsContainer;

    @FXML
    public void initialize() throws IOException {
        controller = new AdminPageController();
        controller.loadPopularPosts(this);
    }

    @FXML
    private void backToAdminClicked() throws IOException {
        Main.getInstance().goToAdminPage();
    }

    public VBox getPopularPostsContainer() { return popularPostsContainer; }
}