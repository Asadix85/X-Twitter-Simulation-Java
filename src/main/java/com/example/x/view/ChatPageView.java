package com.example.x.view;

import com.example.x.Main;
import com.example.x.controller.ChatController;
import com.example.x.model.account.Account;
import com.example.x.model.chat.ChatMessage;
import com.example.x.model.account.user.User;
import com.example.x.model.database.DataManager;
import com.example.x.model.notification.Notification;
import com.example.x.view.card.MessageBubbleController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.IOException;


public class ChatPageView {

    private ChatController controller;
    private String otherUserId;
    private String typingUserId = null;
    DataManager dataManager;
    private boolean isSending = false;

    @FXML private Label chatUsernameLabel;
    @FXML private VBox messagesContainer;
    @FXML private TextField messageTextField;
    @FXML private ScrollPane scrollPane;
    @FXML private Label typingIndicatorLabel;


    @FXML
    public void initialize() {
        controller = new ChatController(this);
        dataManager = DataManager.getInstance();
        messageTextField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.isEmpty()) {
                controller.sendTypingStart();
            } else {
                controller.sendTypingStop();
            }
        });
    }

    public void setOtherUserId(String userId) {
        this.otherUserId = userId;
        controller.setOtherUser(userId);
    }

    public void addMessage(ChatMessage msg, boolean isMine) {
        Platform.runLater(() -> {
            try {
                String fxmlFile = isMine ?
                        "/com/example/x/MessageBubbleMine.fxml" :
                        "/com/example/x/MessageBubbleOther.fxml";

                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                HBox bubble = loader.load();

                MessageBubbleController controller = loader.getController();
                controller.setMessage(msg, isMine);

                messagesContainer.getChildren().add(bubble);
                scrollToBottom();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void clearMessages() {
        Platform.runLater(() -> {
            messagesContainer.getChildren().clear();
        });
    }

    public void scrollToBottom() {
        Platform.runLater(() -> {
            if (scrollPane != null) {
                scrollPane.setVvalue(1.0);
            }
        });
    }

    public void clearMessageInput() {
        Platform.runLater(() -> {
            messageTextField.clear();
        });
    }

    public void setChatUsername(String username) {
        Platform.runLater(() -> {
            chatUsernameLabel.setText(username);
        });
    }

    public void showError(String error) {
        Platform.runLater(() -> {
            typingIndicatorLabel.setText(error);
        });
    }



    @FXML
    void sendClicked(MouseEvent event) {
        if (isSending) return;

        String text = messageTextField.getText().trim();
        if (!text.isEmpty()) {
            isSending = true;
            controller.sendMessage(text);
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> isSending = false);
            }).start();
        }
    }

    @FXML
    void backClicked(MouseEvent event) throws IOException {
        controller.disconnect();
        Main.getInstance().goToMessagesList();
    }

    public void showNotification(String message) {
        Platform.runLater(() -> {
            Label notification = new Label("🛎 " + message);
            notification.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; "
                    + "-fx-padding: 10; -fx-background-radius: 8;");
            notification.setFont(Font.font("System", 13));

            HBox wrapper = new HBox(notification);
            wrapper.setAlignment(Pos.CENTER);
            wrapper.setPadding(new Insets(8));
            messagesContainer.getChildren().add(0, wrapper);

            new Thread(() -> {
                try { Thread.sleep(5000); } catch (Exception ignored) {}
                Platform.runLater(() -> messagesContainer.getChildren().remove(wrapper));
            }).start();

            scrollToBottom();
        });
    }

    public void updateMessageStatus(int messageId, ChatMessage.MessageStatus status) {
        Platform.runLater(() -> {
            System.out.println("Updated message " + messageId + " to status: " + status);
        });
    }
    public void showTypingIndicator(String userId, boolean isTyping) {
        Platform.runLater(() -> {
            if (isTyping && userId != null) {
                Account user = dataManager.findUserById(userId);
                String name = user != null ? user.getUsername() : "Someone";
                typingIndicatorLabel.setText(name + " is typing...");
                typingIndicatorLabel.setVisible(true);
                typingUserId = userId;
            } else {
                typingIndicatorLabel.setVisible(false);
                typingIndicatorLabel.setText("");
                typingUserId = null;
            }
            scrollToBottom();
        });
    }

    public void showNotification(Notification notification) {
        Platform.runLater(() -> {
            String message = getNotificationText(notification);

            Label label = new Label("🛎 " + message);
            label.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                    "-fx-padding: 12; -fx-background-radius: 10; -fx-font-size: 13;");

            messagesContainer.getChildren().add(0, label);

            new Thread(() -> {
                try { Thread.sleep(5500); } catch (Exception ignored) {}
                Platform.runLater(() -> messagesContainer.getChildren().remove(label));
            }).start();

            scrollToBottom();
        });
    }

    private String getNotificationText(Notification n) {
        return switch (n.getType()) {
            case LIKE -> n.getContent();
            case FOLLOW -> n.getSenderId() + " followed you";
            case REPLY -> "New reply to your post";
            case MESSAGE -> "New message from " + n.getSenderId();
            default -> n.getContent();
        };
    }
}
