package com.example.x.view;

import com.example.x.Main;
import com.example.x.controller.AdminPageController;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class PopularHashtagsView {
    private AdminPageController controller;

    @FXML private VBox popularHashtagsContainer;


    @FXML
    public void initialize() throws IOException {
        controller = new AdminPageController();
        controller.loadPopularHashtags(this);
    }

    @FXML
    private void backToAdminClicked() throws IOException {
        Main.getInstance().goToAdminPage();
    }

    public VBox getPopularHashtagsContainer() { return popularHashtagsContainer; }
}