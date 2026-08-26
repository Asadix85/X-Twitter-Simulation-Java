package com.example.x.view.card;

import com.example.x.model.notification.Notification;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.time.format.DateTimeFormatter;

public class NotificationCardView {

    @FXML private HBox rootContainer;
    @FXML private ImageView iconImageView;
    @FXML private Label contentLabel;
    @FXML private Label timeLabel;
    @FXML private Label unreadIndicator;

    private Notification notification;

    public void setNotification(Notification notification) {
        this.notification = notification;

        String iconPath = switch (notification.getType()) {
            case LIKE -> "/Icon/favorite_208px.png";
            case FOLLOW -> "/Icon/follow_480px.png";
            case MESSAGE -> "/Icon/email_send_384px.png";
            case REPLY -> "/Icon/reply_480px.png";
            case MENTION -> "/Icon/mention_480px.png";
            case REPORT -> "/Icon/report_480px.png";
        };
        iconImageView.setImage(new Image(getClass().getResourceAsStream(iconPath)));

        contentLabel.setText(notification.getContent());

        String time = notification.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
        timeLabel.setText(time);

        unreadIndicator.setVisible(!notification.isRead());

        rootContainer.setOnMouseClicked(e -> {
            if (!notification.isRead()) {
                // notifyRepository.markAsRead(notification.getId());
                notification.setRead(true);
                unreadIndicator.setVisible(false);
            }
        });
    }
}