package com.example.x.repository;

import com.example.x.model.hashtag.Hashtag;
import com.example.x.model.database.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HashtagRepository implements IRepository<Hashtag> {

    @Override
    public boolean add(Hashtag hashtag) {
        if (hashtag.getHashtagId() == null || hashtag.getHashtagId().isEmpty()) {
            hashtag.setHashtagId(UUID.randomUUID().toString());
        }

        String sql = "INSERT INTO hashtags (id, hashtagId, title) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String id = UUID.randomUUID().toString();
            stmt.setString(1, id);
            stmt.setString(2, id);
            stmt.setString(3, hashtag.getTitle());

            boolean result = stmt.executeUpdate() > 0;
            if (result) {
                hashtag.setHashtagId(id);
                hashtag.setHashtagId(id);
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM hashtags WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(String id, Hashtag hashtag) {
        String sql = "UPDATE hashtags SET title = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashtag.getTitle());
            stmt.setString(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Hashtag> findAll() {
        List<Hashtag> hashtags = new ArrayList<>();
        String sql = "SELECT * FROM hashtags";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                hashtags.add(mapResultSetToHashtag(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashtags;
    }

    @Override
    public Hashtag findById(String id) {
        String sql = "SELECT * FROM hashtags WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToHashtag(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Hashtag findByTitle(String title) {
        String sql = "SELECT * FROM hashtags WHERE title = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToHashtag(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Hashtag> searchHashtags(String query) {
        List<Hashtag> results = new ArrayList<>();
        String sql = "SELECT * FROM hashtags WHERE title LIKE ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapResultSetToHashtag(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<Hashtag> getMostPopularHashtags(int limit) {
        List<Hashtag> hashtags = new ArrayList<>();
        String sql = "SELECT h.*, COUNT(ph.postId) as usageCount FROM hashtags h " +
                "LEFT JOIN post_hashtags ph ON h.id = ph.hashtagId " +
                "GROUP BY h.id ORDER BY usageCount DESC LIMIT ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Hashtag tag = mapResultSetToHashtag(rs);
                hashtags.add(tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashtags;
    }

    public void addPostToHashtag(String postId, String hashtagId) {
        String sql = "INSERT IGNORE INTO post_hashtags (postId, hashtagId) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, postId);
            stmt.setString(2, hashtagId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removePostFromHashtag(String postId, String hashtagId) {
        String sql = "DELETE FROM post_hashtags WHERE postId = ? AND hashtagId = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, postId);
            stmt.setString(2, hashtagId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getPostIdsForHashtag(String hashtagId) {
        List<String> postIds = new ArrayList<>();
        String sql = "SELECT postId FROM post_hashtags WHERE hashtagId = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashtagId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                postIds.add(rs.getString("postId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postIds;
    }

    public List<Hashtag> getHashtagsForPost(String postId) {
        List<Hashtag> hashtags = new ArrayList<>();
        String sql = "SELECT h.* FROM hashtags h " +
                "JOIN post_hashtags ph ON h.id = ph.hashtagId " +
                "WHERE ph.postId = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, postId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                hashtags.add(mapResultSetToHashtag(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashtags;
    }

    public void syncPostHashtags(String postId, List<String> hashtagIds) {
        String deleteSql = "DELETE FROM post_hashtags WHERE postId = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setString(1, postId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (String hashtagId : hashtagIds) {
            addPostToHashtag(postId, hashtagId);
        }
    }

    private Hashtag mapResultSetToHashtag(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String hashtagId = rs.getString("hashtagId");
        String title = rs.getString("title");

        Hashtag hashtag = new Hashtag(title);
        hashtag.setHashtagId(hashtagId);
        hashtag.setHashtagId(id);

        return hashtag;
    }
}