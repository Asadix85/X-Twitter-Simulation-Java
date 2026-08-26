package com.example.x.view.card;

import com.example.x.model.chat.ChatMessage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;

public class MessageBubbleController {

    @FXML private VBox messageBox;
    @FXML private Label textLabel;
    @FXML private Label timeLabel;

    public void setMessage(ChatMessage message, boolean isMine) {
        textLabel.setText(message.getContent());
        String timeText = message.getSendTime().format(DateTimeFormatter.ofPattern("HH:mm"));

        String statusText = "";
        if (isMine) {
            switch (message.getStatus()) {
                case SENT:
                    statusText = " ✓";
                    break;
                case DELIVERED:
                    statusText = " ✓✓";
                    break;
                case READ:
                    statusText = " ✓✓✓";
                    break;
            }
        }
        timeLabel.setText(timeText + statusText);
    }
}