package com.example.x.view;

import com.example.x.Main;
import com.example.x.model.database.DataManager;
import com.example.x.model.notification.Notification;
import com.example.x.repository.NotificationRepository;
import com.example.x.view.card.NotificationCardView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class NotificationsView {

    private DataManager dataManager = DataManager.getInstance();
    private NotificationRepository notificationRepository = new NotificationRepository();

    @FXML private VBox notificationsContainer;
    @FXML private Label unreadCountLabel;

    @FXML
    public void initialize() {
        loadNotifications();
    }

    private void loadNotifications() {
        notificationsContainer.getChildren().clear();

        String userId = dataManager.getCurrentUser().getId();
        List<Notification> notifications = notificationRepository.findByUserId(userId);

        if (notifications.isEmpty()) {
            Label emptyLabel = new Label("No notifications yet!");
            emptyLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 14;");
            notificationsContainer.getChildren().add(emptyLabel);
            unreadCountLabel.setText("0");
            return;
        }

        int unreadCount = 0;
        for (Notification notification : notifications) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/NotificationCard.fxml"));
                HBox card = loader.load();
                NotificationCardView controller = loader.getController();
                controller.setNotification(notification);
                notificationsContainer.getChildren().add(card);

                if (!notification.isRead()) {
                    unreadCount++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        unreadCountLabel.setText(String.valueOf(unreadCount));
    }

    @FXML
    void markAllAsRead(MouseEvent event) {
        String userId = dataManager.getCurrentUser().getId();
        notificationRepository.markAllAsRead(userId);
        loadNotifications();
    }

    @FXML
    void backClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToHomePage();
    }
}