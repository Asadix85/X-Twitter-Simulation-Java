package com.example.x.repository;

import com.example.x.model.chat.ChatMessage;
import com.example.x.model.database.ConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository {

    public boolean save(ChatMessage message) {
        String sql = "INSERT INTO messages (senderId, receiverId, content, sendTime, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, message.getSenderId());
            stmt.setString(2, message.getReceiverId());
            stmt.setString(3, message.getContent());
            stmt.setTimestamp(4, Timestamp.valueOf(message.getSendTime()));
            stmt.setString(5, message.getStatus().name());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    message.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "UPDATE messages SET isDeletedForEveryone = true WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int id, ChatMessage.MessageStatus status) {
        String sql = "UPDATE messages SET status = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, id);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ChatMessage> getConversation(String userId1, String userId2) {
        List<ChatMessage> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE isDeletedForEveryone = false AND " +
                "((senderId = ? AND receiverId = ?) OR (senderId = ? AND receiverId = ?)) " +
                "ORDER BY sendTime ASC";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId1);
            stmt.setString(2, userId2);
            stmt.setString(3, userId2);
            stmt.setString(4, userId1);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public List<ChatMessage> getMessagesForUser(String userId) {
        List<ChatMessage> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE isDeletedForEveryone = false AND " +
                "(senderId = ? OR receiverId = ?) ORDER BY sendTime DESC";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public List<ChatMessage> getUnreadMessages(String userId) {
        List<ChatMessage> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE receiverId = ? AND status = 'SENT' AND isDeletedForEveryone = false ORDER BY sendTime ASC";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public void markAsRead(int messageId) {
        String sql = "UPDATE messages SET status = 'READ' WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void markAllAsRead(String userId, String otherUserId) {
        String sql = "UPDATE messages SET status = 'READ' WHERE senderId = ? AND receiverId = ? AND status = 'SENT'";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, otherUserId);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void markAsDelivered(int messageId) {
        String sql = "UPDATE messages SET status = 'DELIVERED' WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteForEveryone(int messageId) {
        String sql = "UPDATE messages SET isDeletedForEveryone = true WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getUnreadCount(String userId) {
        String sql = "SELECT COUNT(*) FROM messages WHERE receiverId = ? AND status = 'SENT' AND isDeletedForEveryone = false";
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

    public int getUnreadCountBetween(String userId, String otherUserId) {
        String sql = "SELECT COUNT(*) FROM messages WHERE senderId = ? AND receiverId = ? AND status = 'SENT' AND isDeletedForEveryone = false";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, otherUserId);
            stmt.setString(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private ChatMessage mapResultSetToMessage(ResultSet rs) throws SQLException {
        ChatMessage message = new ChatMessage();
        message.setId(rs.getInt("id"));
        message.setSenderId(rs.getString("senderId"));
        message.setReceiverId(rs.getString("receiverId"));
        message.setContent(rs.getString("content"));

        Timestamp timestamp = rs.getTimestamp("sendTime");
        if (timestamp != null) {
            message.setSendTime(timestamp.toLocalDateTime());
        } else {
            message.setSendTime(LocalDateTime.now());
        }

        message.setStatus(ChatMessage.MessageStatus.valueOf(rs.getString("status")));
        message.setDeletedForEveryone(rs.getBoolean("isDeletedForEveryone"));
        return message;
    }

    public boolean exists(int messageId) {
        if (messageId <= 0) return false;
        String sql = "SELECT 1 FROM messages WHERE id = ? AND isDeletedForEveryone = false";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}