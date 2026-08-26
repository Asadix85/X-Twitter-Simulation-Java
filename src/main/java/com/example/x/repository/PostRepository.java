package com.example.x.repository;

import com.example.x.model.file.Photo;
import com.example.x.model.file.Video;
import com.example.x.model.post.Post;
import com.example.x.model.database.ConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PostRepository implements IRepository<Post> {

    @Override
    public boolean add(Post post) {
        if (post.getPostId() == null || post.getPostId().isEmpty()) {
            post.setPostId(UUID.randomUUID().toString());
        }

        String sql = "INSERT INTO posts (id, postId, authorId, postText, createDate, " +
                "viewCount, likeCount, isLocked, isDeleted, " +
                "mediaFileId, mediaFilePath, mediaFileType, " +
                "photoFormat, photoHeight, photoWidth, " +
                "videoQuality, videoPlayFormat, videoTotalTime, " +
                "totalComments, parentPostId) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, post.getPostId());
            stmt.setString(2, post.getPostId());
            stmt.setString(3, post.getAuthorId());
            stmt.setString(4, post.getPostText());
            stmt.setTimestamp(5, Timestamp.valueOf(post.getPublishDate()));
            stmt.setInt(6, post.getTotalViews());
            stmt.setInt(7, post.getTotalLikes());
            stmt.setBoolean(8, post.isLocked());
            stmt.setBoolean(9, post.isDeleted());

            var media = post.getMedia();
            if (media != null) {
                stmt.setString(10, media.getFileId());
                stmt.setString(11, media.getFilePath());
                stmt.setString(12, media.getFileType());

                if (media instanceof Photo) {
                    Photo photo = (Photo) media;
                    stmt.setString(13, photo.getFormat());
                    stmt.setInt(14, photo.getHeight());
                    stmt.setInt(15, photo.getWeight());
                    stmt.setNull(16, Types.INTEGER);
                    stmt.setNull(17, Types.VARCHAR);
                    stmt.setNull(18, Types.VARCHAR);
                } else if (media instanceof Video) {
                    Video video = (Video) media;
                    stmt.setNull(13, Types.VARCHAR);
                    stmt.setNull(14, Types.INTEGER);
                    stmt.setNull(15, Types.INTEGER);
                    stmt.setInt(16, video.getQuality());
                    stmt.setString(17, video.getPlayFormat());
                    stmt.setString(18, video.getTotalTime());
                } else {
                    stmt.setNull(13, Types.VARCHAR);
                    stmt.setNull(14, Types.INTEGER);
                    stmt.setNull(15, Types.INTEGER);
                    stmt.setNull(16, Types.INTEGER);
                    stmt.setNull(17, Types.VARCHAR);
                    stmt.setNull(18, Types.VARCHAR);
                }
            } else {
                stmt.setNull(10, Types.VARCHAR);
                stmt.setNull(11, Types.VARCHAR);
                stmt.setNull(12, Types.VARCHAR);
                stmt.setNull(13, Types.VARCHAR);
                stmt.setNull(14, Types.INTEGER);
                stmt.setNull(15, Types.INTEGER);
                stmt.setNull(16, Types.INTEGER);
                stmt.setNull(17, Types.VARCHAR);
                stmt.setNull(18, Types.VARCHAR);
            }

            stmt.setInt(19, post.getTotalComments());
            stmt.setString(20, post.getParentPostId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        Post post = findById(id);
        boolean isReply = post != null && post.getParentPostId() != null && !post.getParentPostId().isEmpty();

        String sql = "UPDATE posts SET isDeleted = true WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            boolean result = stmt.executeUpdate() > 0;
            if (result && isReply && post != null) {
                decrementCommentCount(post.getParentPostId());
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(String id, Post post) {
        String sql = "UPDATE posts SET postText = ?, viewCount = ?, likeCount = ?, " +
                "isLocked = ?, isDeleted = ?, totalComments = ?, parentPostId = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, post.getPostText());
            stmt.setInt(2, post.getTotalViews());
            stmt.setInt(3, post.getTotalLikes());
            stmt.setBoolean(4, post.isLocked());
            stmt.setBoolean(5, post.isDeleted());
            stmt.setInt(6, post.getTotalComments());
            stmt.setString(7, post.getParentPostId());
            stmt.setString(8, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Post> findAll() {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE isDeleted = false ORDER BY createDate DESC";

        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Map<String, Object>> rows = new ArrayList<>();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                rows.add(row);
            }

            for (Map<String, Object> row : rows) {
                posts.add(mapRowToPost(row));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(String id) {
        String sql = "SELECT * FROM posts WHERE id = ? AND isDeleted = false";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                return mapRowToPost(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Post> findByAuthorId(String authorId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE authorId = ? AND isDeleted = false ORDER BY createDate DESC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authorId);
            ResultSet rs = stmt.executeQuery();

            List<Map<String, Object>> rows = resultSetToList(rs);
            for (Map<String, Object> row : rows) {
                posts.add(mapRowToPost(row));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public List<Post> searchPosts(String query) {
        List<Post> results = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE postText LIKE ? AND isDeleted = false";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();

            List<Map<String, Object>> rows = resultSetToList(rs);
            for (Map<String, Object> row : rows) {
                results.add(mapRowToPost(row));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<Post> getMostLikedPosts(int limit) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE isDeleted = false ORDER BY likeCount DESC LIMIT ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            List<Map<String, Object>> rows = resultSetToList(rs);
            for (Map<String, Object> row : rows) {
                posts.add(mapRowToPost(row));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public List<Post> getReplies(String parentPostId) {
        List<Post> replies = new ArrayList<>();
        String sql = "SELECT p.* FROM posts p " +
                "JOIN replies r ON p.id = r.replyPostId " +
                "WHERE r.parentPostId = ? AND p.isDeleted = false " +
                "ORDER BY p.createDate ASC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, parentPostId);
            ResultSet rs = stmt.executeQuery();

            List<Map<String, Object>> rows = resultSetToList(rs);
            for (Map<String, Object> row : rows) {
                replies.add(mapRowToPost(row));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return replies;
    }

    public List<Post> getRepliesByAuthor(String authorId) {
        List<Post> replies = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE authorId = ? AND parentPostId IS NOT NULL AND isDeleted = false ORDER BY createDate DESC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authorId);
            ResultSet rs = stmt.executeQuery();

            List<Map<String, Object>> rows = resultSetToList(rs);
            for (Map<String, Object> row : rows) {
                replies.add(mapRowToPost(row));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return replies;
    }

    public List<Post> getMediaByAuthor(String authorId) {
        List<Post> media = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE authorId = ? AND mediaFileId IS NOT NULL AND isDeleted = false ORDER BY createDate DESC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authorId);
            ResultSet rs = stmt.executeQuery();

            List<Map<String, Object>> rows = resultSetToList(rs);
            for (Map<String, Object> row : rows) {
                media.add(mapRowToPost(row));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return media;
    }

    public List<String> getLikerIds(String postId) {
        List<String> likerIds = new ArrayList<>();
        String sql = "SELECT userId FROM likes WHERE postId = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, postId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                likerIds.add(rs.getString("userId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return likerIds;
    }

    private List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(metaData.getColumnName(i), rs.getObject(i));
            }
            rows.add(row);
        }
        return rows;
    }

    private Post mapRowToPost(Map<String, Object> row) {
        String postId = (String) row.get("id");
        String authorId = (String) row.get("authorId");
        String postText = (String) row.get("postText");
        String parentPostId = (String) row.get("parentPostId");

        int viewCount = (Integer) row.getOrDefault("viewCount", 0);
        int likeCount = (Integer) row.getOrDefault("likeCount", 0);
        int totalComments = (Integer) row.getOrDefault("totalComments", 0);

        boolean isLocked = (Boolean) row.getOrDefault("isLocked", false);
        boolean isDeleted = (Boolean) row.getOrDefault("isDeleted", false);

        Timestamp ts = (Timestamp) row.get("createDate");

        Post post = new Post(postText, authorId);
        post.setPostId(postId);
        post.setParentPostId(parentPostId);
        post.setTotalViews(viewCount);
        post.setTotalLikes(likeCount);
        post.setTotalComments(totalComments);
        post.setLocked(isLocked);
        post.setDeleted(isDeleted);

        if (ts != null) {
            post.setPublishDate(ts.toLocalDateTime());
        }

        String mediaFileId = (String) row.get("mediaFileId");
        if (mediaFileId != null && !mediaFileId.isEmpty()) {
            String mediaFilePath = (String) row.get("mediaFilePath");
            String mediaFileType = (String) row.get("mediaFileType");

            if ("Photo".equals(mediaFileType)) {
                String format = (String) row.get("photoFormat");
                Photo photo = new Photo(mediaFilePath, format != null ? format : "JPEG");
                photo.setFileId(mediaFileId);
                photo.setHeight((Integer) row.getOrDefault("photoHeight", 0));
                photo.setWeight((Integer) row.getOrDefault("photoWidth", 0));
                post.setMedia(photo);
            } else if ("Video".equals(mediaFileType)) {
                int quality = (Integer) row.getOrDefault("videoQuality", 720);
                String playFormat = (String) row.get("videoPlayFormat");
                Video video = new Video(mediaFilePath, quality, playFormat != null ? playFormat : "MP4", 0);
                video.setFileId(mediaFileId);
                String totalTime = (String) row.get("videoTotalTime");
                if (totalTime != null) {
                    video.setTotalTime(parseTotalTime(totalTime));
                }
                post.setMedia(video);
            }
        }

        return post;
    }

    private int parseTotalTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return 0;
        try {
            String[] parts = timeStr.split(":");
            if (parts.length == 2) {
                return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
            }
        } catch (NumberFormatException e) {
            e.getMessage();
        }
        return 0;
    }

    public void incrementViewCount(String postId) {
        String sql = "UPDATE posts SET viewCount = viewCount + 1 WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, postId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getViewCount(String postId) {
        String sql = "SELECT viewCount FROM posts WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, postId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("viewCount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void incrementLikeCount(String postId) {
        String sql = "UPDATE posts SET likeCount = likeCount + 1 WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, postId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void decrementLikeCount(String postId) {
        String sql = "UPDATE posts SET likeCount = likeCount - 1 WHERE id = ? AND likeCount > 0";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, postId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getLikeCount(String postId) {
        String sql = "SELECT likeCount FROM posts WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, postId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("likeCount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void incrementCommentCount(String postId) {
        String sql = "UPDATE posts SET totalComments = totalComments + 1 WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, postId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void decrementCommentCount(String postId) {
        String sql = "UPDATE posts SET totalComments = totalComments - 1 WHERE id = ? AND totalComments > 0";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, postId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCommentCount(String postId) {
        String sql = "SELECT totalComments FROM posts WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, postId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("totalComments");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addReply(String parentPostId, String replyPostId) {
        String sql = "INSERT INTO replies (parentPostId, replyPostId) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, parentPostId);
            stmt.setString(2, replyPostId);
            int result = stmt.executeUpdate();
            System.out.println("addReply executed: " + result + " row(s) affected");
        } catch (SQLException e) {
            System.err.println("addReply failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeReply(String parentPostId, String replyPostId) {
        String sql = "DELETE FROM replies WHERE parentPostId = ? AND replyPostId = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, parentPostId);
            stmt.setString(2, replyPostId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}