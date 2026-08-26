package com.example.x.view;

import com.example.x.Main;
import com.example.x.controller.PersonalizationController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class PersonalizationView {

    PersonalizationController personalizationController = new PersonalizationController();

    @FXML private TextField hashtag1;
    @FXML private TextField hashtag2;
    @FXML private Label maxChooseLabel;
    @FXML private Button addButton;
    @FXML private Button confirmButton;

    @FXML private Button iranButton;
    @FXML private Button educationButton;
    @FXML private Button javaButton;
    @FXML private Button sportsButton;
    @FXML private Button artButton;
    @FXML private Button gamingButton;
    @FXML private Button techButton;
    @FXML private Button fcbButton;
    @FXML private Button isfButton;

    @FXML
    public void initialize() {
        updateCounter();
        loadUserHashtags();
    }

    private void loadUserHashtags() {
        if (personalizationController.hasUserSelectedHashtags()) {
            var userHashtags = personalizationController.getUserHashtags();
            for (var tag : userHashtags) {
                String title = tag.getTitle().replace("#", "");
                personalizationController.toggleHashtag(title);
            }
        }
    }

    private void updateCounter() {
        int count = personalizationController.getSelectedCount();
        maxChooseLabel.setText(count + "/4");

        if (count >= 4) {
            maxChooseLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            confirmButton.setDisable(false);
        } else if (count > 4) {
            maxChooseLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            confirmButton.setDisable(true);
            confirmButton.setText("Max 4!");
        } else {
            maxChooseLabel.setStyle("-fx-text-fill: black;");
            confirmButton.setDisable(false);
            confirmButton.setText("Confirm");
        }
    }

    private void selectButton(Button button) {
        button.setStyle("-fx-background-color: #1DA1F2; -fx-text-fill: white; -fx-border-color: blue; -fx-background-radius: 20; -fx-border-radius: 20;");
    }

    @FXML
    void iranHashtagClicked(MouseEvent event) {
        personalizationController.toggleHashtag("Iran");
        updateCounter();
        selectButton(iranButton);
    }

    @FXML
    void educationHashtagClicked(MouseEvent event) {
        personalizationController.toggleHashtag("Education");
        updateCounter();
        selectButton(educationButton);
    }

    @FXML
    void javaHashtagClicked(MouseEvent event) {
        personalizationController.toggleHashtag("Java");
        updateCounter();
        selectButton(javaButton);
    }

    @FXML
    void sportsHashtagClicked(MouseEvent event) {
        personalizationController.toggleHashtag("Sport");
        updateCounter();
        selectButton(sportsButton);
    }

    @FXML
    void artHashtagClicked(MouseEvent event) {
        personalizationController.toggleHashtag("Art");
        updateCounter();
        selectButton(artButton);
    }

    @FXML
    void gamingHashtagClicked(MouseEvent event) {
        personalizationController.toggleHashtag("Gaming");
        updateCounter();
        selectButton(gamingButton);
    }

    @FXML
    void techHashtagClicked(MouseEvent event) {
        personalizationController.toggleHashtag("Technology");
        updateCounter();
        selectButton(techButton);
    }

    @FXML
    void fcbHashtagClicked(MouseEvent event) {
        personalizationController.toggleHashtag("FCB");
        updateCounter();
        selectButton(fcbButton);
    }

    @FXML
    void isfHashtagClicked(MouseEvent event) {
        personalizationController.toggleHashtag("Isfahan");
        updateCounter();
        selectButton(isfButton);
    }

    @FXML
    void addCustomHashtagClicked(MouseEvent event) {
        String custom1 = hashtag1.getText().trim();
        String custom2 = hashtag2.getText().trim();

        if (!custom1.isEmpty()) {
            personalizationController.toggleHashtag(custom1);
        }
        if (!custom2.isEmpty()) {
            personalizationController.toggleHashtag(custom2);
        }

        hashtag1.clear();
        hashtag2.clear();
        updateCounter();
        addButton.setText("Added");
    }

    @FXML
    void confirmClicked(MouseEvent event) throws IOException {
        personalizationController.confirm();
        if (personalizationController.isConfirmed()) {
            Main.getInstance().goToHomePage();
        }
    }
}