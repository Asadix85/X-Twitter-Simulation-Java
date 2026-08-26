package com.example.x.view;

import com.example.x.Main;
import com.example.x.controller.HomeController;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class HomePageView {
    private HomeController homeController;

    @FXML
    private VBox vBoxMain;

    @FXML
    public void initialize() throws IOException {
        homeController = new HomeController(this);
        homeController.loadFeed();
    }

    @FXML
    void SearchClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToSearchPage();
    }

    @FXML
    void newButtonClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToNewPostPage();
    }

    @FXML
    void profileClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToUserprofile();
    }

    @FXML
    void signOutClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToGateway();
    }

    @FXML
    void messagesClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToMessagesList();
    }

    public VBox getVBoxMain() {
        return vBoxMain;
    }

    public void notificationClicked(MouseEvent mouseEvent) throws IOException {
        Main.getInstance().goToNotificationsPage();
    }
}