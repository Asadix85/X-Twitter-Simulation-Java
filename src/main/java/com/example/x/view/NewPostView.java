package com.example.x.view;

import com.example.x.Main;
import com.example.x.controller.NewPostController;
import com.example.x.model.account.Account;
import com.example.x.model.database.DataManager;
import com.example.x.model.post.Post;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class NewPostView {
    private NewPostController newPostController;

    @FXML private Label errorLabel;
    @FXML private Label fileLabel;
    @FXML private Button postButton;
    @FXML private Label replyLabel;
    @FXML private TextArea postTextArea;
    @FXML private Button removeButton;
    @FXML private Button uploadButton;


    @FXML
    public void initialize() {
        newPostController = new NewPostController(this);
        DataManager dataManager  = DataManager.getInstance();

        Post replyingTo = Main.getInstance().getReplyingToPost();
        if (replyingTo != null) {
            Account auther = dataManager.findUserById(replyingTo.getAuthorId());
            String info = "Replying to @" + auther.getUsername() + ": ";
            replyLabel.setText(info);
            replyLabel.setVisible(true);
        }
    }

    @FXML
    void SearchClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToSearchPage();
    }

    @FXML void messagesClicked(MouseEvent event) throws IOException {Main.getInstance().goToMessagesList(); }

    @FXML
    void handleCreatePost(ActionEvent event) {
        String text = postTextArea.getText().trim();
        if (text.isEmpty() && newPostController.getSelectedFile() == null) {
            showError("The post text or attachment cannot both be empty!");
            return;
        }
        newPostController.handleCreatePost(text);
        if (newPostController.isCreated()) {
            showSuccess("Post created!");
            postTextArea.clear();
            handleRemoveMedia(null);
        }
    }

    @FXML
    void handleRemoveMedia(ActionEvent event) {
        newPostController.setSelectedFile(null);
        fileLabel.setText("No file selected");
        fileLabel.setStyle("-fx-text-fill: gray;");
        removeButton.setVisible(false);
    }

    @FXML
    void handleUploadMedia(ActionEvent event) {
        newPostController.handleUploadMedia(uploadButton, fileLabel, removeButton);
    }

    @FXML
    void homeClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToHomePage();
    }

    @FXML
    void profileClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToUserprofile();
    }

    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(true);
    }

    public void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: green;");
        errorLabel.setVisible(true);
    }
}