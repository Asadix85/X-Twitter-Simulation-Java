package com.example.x.controller;

import com.example.x.Main;
import com.example.x.model.account.user.User;
import com.example.x.model.account.user.normalUser.NormalUser;
import com.example.x.model.account.user.premiumUser.BluePremiumUser;
import com.example.x.model.account.user.premiumUser.GoldPremiumUser;
import com.example.x.model.database.DataManager;
import com.example.x.model.file.Photo;
import com.example.x.model.file.Video;
import com.example.x.model.file.File;
import com.example.x.model.post.Post;
import com.example.x.view.NewPostView;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class NewPostController {

    private final DataManager dataManager;
    private final User currentUser;
    private NewPostView view;
    private java.io.File selectedFile;
    private boolean created;

    public NewPostController(NewPostView view) {
        this.dataManager = DataManager.getInstance();
        this.currentUser = dataManager.getCurrentUser();
        this.view = view;
        this.selectedFile = null;
        this.created = false;
    }

    public java.io.File getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(java.io.File selectedFile) {
        this.selectedFile = selectedFile;
    }

    public boolean isCreated() {
        return created;
    }

    public void handleUploadMedia(Button uploadButton, Label fileLabel, Button removeButton) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose photo or video:");

        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Images", "*.jpg", "*.jpeg", "*.png", "*.svg", "*.webp");

        FileChooser.ExtensionFilter videoFilter = new FileChooser.ExtensionFilter(
                "Videos", "*.mp4", "*.mkv", "*.mov", "*.wmv");

        fileChooser.getExtensionFilters().addAll(imageFilter, videoFilter);

        Stage stage = (Stage) uploadButton.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            fileLabel.setText(selectedFile.getName());
            fileLabel.setStyle("-fx-text-fill: green;");
            removeButton.setVisible(true);
        }
    }

    public void handleCreatePost(String text) {
        created = false;

        if (text.isEmpty() && selectedFile == null) {
            view.showError("Post text or attachment cannot both be empty!");
            return;
        }

        Post post = new Post(text, currentUser.getId());

        Post replyingTo = Main.getInstance().getReplyingToPost();
        if (replyingTo != null) {
            post.setParentPostId(replyingTo.getPostId());
        }

        if (selectedFile != null) {
            String fileName = selectedFile.getName();
            String filePath = selectedFile.getAbsolutePath();
            String format = getFormatFromFileName(fileName);

            File media;

            if (Photo.isValidFormat(format.toLowerCase())) {
                media = new Photo(filePath, format);
            } else if (Video.isValidFormat(format.toLowerCase())) {
                media = new Video(filePath, 720, format, 0);
            } else {
                view.showError("File format is not supported!");
                return;
            }

            post.attachMedia(media);
        }

        int cost = calculatePostCost(post);

        if (currentUser.getTokens() < cost) {
            view.showError("Insufficient tokens! Need: " + cost + " tokens");
            return;
        }

        currentUser.deductTokens(cost);
        dataManager.updateTokenBalance(currentUser.getId(), currentUser.getTokens());

        currentUser.getPostIds().add(post.getPostId());
        boolean postSaved = dataManager.addPost(post);

        if (!postSaved) {
            view.showError("Failed to save post!");
            return;
        }

        if (replyingTo != null) {
            dataManager.addReply(replyingTo.getPostId(), post.getPostId());
            dataManager.incrementCommentCount(replyingTo.getPostId());

            replyingTo.addReply(post.getPostId());
            currentUser.getRepliesPostIds().add(post.getPostId());
        }

        created = true;
    }

    public String getReplyInfo() {
        Post replyingTo = Main.getInstance().getReplyingToPost();
        if (replyingTo != null) {
            User author = (User) dataManager.findUserById(replyingTo.getAuthorId());
            String authorName = author != null ? author.getUsername() : "Unknown";
            String preview = replyingTo.getPostText();
            if (preview.length() > 30) preview = preview.substring(0, 30) + "...";
            return "Replying to @" + authorName + ": " + preview;
        }
        return null;
    }

    private String getFormatFromFileName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toUpperCase();
        }
        return "UNKNOWN";
    }

    private int calculatePostCost(Post post) {
        int postTextLength = post.getPostText().length();

        if (currentUser instanceof NormalUser) {
            if (selectedFile == null)
                return postTextLength;
            else
                return postTextLength + 10;
        }

        if (currentUser instanceof BluePremiumUser) {
            if (selectedFile == null)
                return postTextLength / 2;
            else
                return postTextLength / 2 + 5;
        }

        if (currentUser instanceof GoldPremiumUser) {
            return 5;
        }

        return 0;
    }
}