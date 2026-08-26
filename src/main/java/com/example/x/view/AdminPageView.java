package com.example.x.view;

import com.example.x.Main;
import com.example.x.controller.AdminPageController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class AdminPageView {
    private AdminPageController controller;

    @FXML
    private Label totalPostLabel;

    @FXML
    private Label totalUsersLabel;

    @FXML
    public void initialize() {
        controller = new AdminPageController();
        controller.loadAdminStats(this);
    }

    @FXML
    void SignOutClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToGateway();
    }

    @FXML
    void popularHashtagsClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToPopularHashtagsPage();
    }

    @FXML
    void popularPostsClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToPopularPostsPage();
    }

    @FXML
    void postsListClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToPostsListPage();
    }

    @FXML
    void reportsListClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToReportListPage();
    }

    @FXML
    void usersListClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToUsersListPage();
    }

    public void setTotalPosts(int size) {
        totalPostLabel.setText(String.format("%d" , size));
    }

    public void setTotalUsers(int size) {
        totalUsersLabel.setText(String.format("%d" , size));
    }
}