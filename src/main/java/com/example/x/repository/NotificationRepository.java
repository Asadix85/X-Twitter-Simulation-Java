package com.example.x.repository;

import com.example.x.model.database.ConnectionManager;
import com.example.x.model.notification.Notification;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationRepository {

    public boolean save(Notification notification) {
        if (notification.getId() == null) {
            notification.setId(UUID.randomUUID().toString());
        }

        String sql = "INSERT INTO notifications (id, userId, senderId, type, content, targetId, timestamp, isRead) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, notification.getId());
            stmt.setString(2, notification.getUserId());
            stmt.setString(3, notification.getSenderId());
            stmt.setString(4, notification.getType().name());
            stmt.setString(5, notification.getContent());
            stmt.setString(6, notification.getTargetId());
            stmt.setTimestamp(7, Timestamp.valueOf(notification.getTimestamp()));
            stmt.setBoolean(8, notification.isRead());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Notification> findByUserId(String userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE userId = ? ORDER BY timestamp DESC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public List<Notification> getUnreadByUserId(String userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE userId = ? AND isRead = false ORDER BY timestamp DESC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public int getUnreadCount(String userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE userId = ? AND isRead = false";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void markAsRead(String notificationId) {
        String sql = "UPDATE notifications SET isRead = true WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, notificationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void markAllAsRead(String userId) {
        String sql = "UPDATE notifications SET isRead = true WHERE userId = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getString("id"));
        notification.setUserId(rs.getString("userId"));
        notification.setSenderId(rs.getString("senderId"));
        notification.setType(Notification.NotificationType.valueOf(rs.getString("type")));
        notification.setContent(rs.getString("content"));
        notification.setTargetId(rs.getString("targetId"));
        notification.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        notification.setRead(rs.getBoolean("isRead"));
        return notification;
    }
}