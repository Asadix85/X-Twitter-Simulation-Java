package com.example.x.view;

import com.example.x.Main;
import com.example.x.controller.AdminPageController;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class UsersListView {

    private AdminPageController controller;

    @FXML private TextField userSearchField;
    @FXML private VBox usersListContainer;


    @FXML
    public void initialize() throws IOException {
        controller = new AdminPageController();
        controller.loadAllUsers(this);
    }

    @FXML
    private void searchUsers() throws IOException {
        controller.searchUsers(this, userSearchField.getText());
    }

    @FXML
    private void backToAdminClicked() throws IOException {
        Main.getInstance().goToAdminPage();
    }

    public VBox getUsersListContainer() { return usersListContainer; }
}