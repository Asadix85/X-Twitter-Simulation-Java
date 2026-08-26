package com.example.x.view;

import com.example.x.Main;
import com.example.x.model.database.DataManager;
import com.example.x.model.post.Post;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class EditPostView {

    private Post post;
    DataManager dataManager = DataManager.getInstance();

    @FXML private TextArea editTextArea;
    @FXML private Label errorLabel;

    public void setPost(Post post) {
        this.post = post;
        editTextArea.setText(post.getPostText());
    }

    @FXML
    void saveClicked(MouseEvent event) throws IOException {
        String newText = editTextArea.getText().trim();

        if (newText.isEmpty()) {
            errorLabel.setText("Post text cannot be empty!");
            errorLabel.setVisible(true);
            return;
        }

        post.setPostText(newText);
        dataManager.updatePost(post);
        post.autoProcessHashtags();

        Main.getInstance().goToHomePage();
    }

    @FXML
    void backClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToHomePage();
    }
}