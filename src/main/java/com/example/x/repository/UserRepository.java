package com.example.x.repository;

import com.example.x.model.account.Account;
import com.example.x.model.account.admin.Admin;
import com.example.x.model.account.user.User;
import com.example.x.model.account.user.normalUser.NormalUser;
import com.example.x.model.account.user.premiumUser.BluePremiumUser;
import com.example.x.model.account.user.premiumUser.GoldPremiumUser;
import com.example.x.model.database.ConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepository implements IRepository<Account> {

    @Override
    public boolean add(Account account) {
        if (account.getId() == null || account.getId().isEmpty()) {
            account.setId(UUID.randomUUID().toString());
        }

        String sql = "INSERT INTO users (id, username, password, fullName, phoneNumber, email, accountType, tokenBalance, registerDate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, account.getId());
            stmt.setString(2, account.getUsername());
            stmt.setString(3, account.getPassword());
            stmt.setString(4, account.getFullName());
            stmt.setString(5, account.getPhoneNumber());
            stmt.setString(6, account.getEmail());
            stmt.setString(7, getAccountType(account));
            stmt.setInt(8, account instanceof User ? ((User) account).getTokens() : 0);

            if (account.getMemberShipDate() != null) {
                stmt.setDate(9, Date.valueOf(account.getMemberShipDate()));
            } else {
                stmt.setNull(9, Types.DATE);
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM users WHERE id = ?";
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
    public boolean update(String id, Account account) {
        String sql = "UPDATE users SET username = ?, password = ?, fullName = ?, phoneNumber = ?, " +
                "email = ?, accountType = ?, tokenBalance = ?, bio = ?, isBlocked = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, account.getUsername());
            stmt.setString(2, account.getPassword());
            stmt.setString(3, account.getFullName());
            stmt.setString(4, account.getPhoneNumber());
            stmt.setString(5, account.getEmail());
            stmt.setString(6, getAccountType(account));
            stmt.setInt(7, account instanceof User ? ((User) account).getTokens() : 0);

            if (account instanceof User) {
                stmt.setString(8, ((User) account).getBio());
                stmt.setBoolean(9, ((User) account).isBlocked());
            } else {
                stmt.setNull(8, Types.VARCHAR);
                stmt.setBoolean(9, false);
            }

            stmt.setString(10, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Account> findAll() {
        List<Account> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Account findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAccount(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAccount(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAccount(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Account> searchUsers(String query) {
        List<Account> results = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE username LIKE ? OR fullName LIKE ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + query + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public void updateOnlineStatus(String userId, boolean isOnline) {
        String sql = "UPDATE users SET isOnline = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isOnline);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTokenBalance(String userId, int newBalance) {
        String sql = "UPDATE users SET tokenBalance = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newBalance);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private String getAccountType(Account account) {
        if (account instanceof Admin) return "ADMIN";
        if (account instanceof GoldPremiumUser) return "GOLD";
        if (account instanceof BluePremiumUser) return "BLUE";
        if (account instanceof NormalUser) return "NORMAL";
        return "NORMAL";
    }

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        String accountType = rs.getString("accountType");
        Account account = null;

        String id = rs.getString("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String fullName = rs.getString("fullName");
        String phoneNumber = rs.getString("phoneNumber");
        String email = rs.getString("email");
        int tokenBalance = rs.getInt("tokenBalance");

        switch (accountType) {
            case "ADMIN":
                account = new Admin(username, password, email);
                break;
            case "GOLD":
                account = new GoldPremiumUser(fullName, phoneNumber, username, password, email);
                ((User) account).setTokens(tokenBalance);
                break;
            case "BLUE":
                account = new BluePremiumUser(fullName, phoneNumber, username, password, email);
                ((User) account).setTokens(tokenBalance);
                break;
            default:
                account = new NormalUser(fullName, phoneNumber, username, password, email);
                ((User) account).setTokens(tokenBalance);
                break;
        }

        account.setId(id);

        Date registerDate = rs.getDate("registerDate");
        if (registerDate != null) {
            account.setMemberShipDate(registerDate.toLocalDate());
        }

        account.setOnline(rs.getBoolean("isOnline"));

        if (account instanceof User) {
            User user = (User) account;
            user.setBio(rs.getString("bio"));
            user.setBlocked(rs.getBoolean("isBlocked"));
        }

        return account;
    }

    public void addLikedPost(String userId, String postId) {
        String sql = "INSERT INTO likes (userId, postId) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, postId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeLikedPost(String userId, String postId) {
        String sql = "DELETE FROM likes WHERE userId = ? AND postId = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, postId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasLikedPost(String userId, String postId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE userId = ? AND postId = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, postId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addFollower(String followerId, String followingId) {
        String sql = "INSERT IGNORE INTO followers (followerId, followingId) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, followerId);
            stmt.setString(2, followingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeFollower(String followerId, String followingId) {
        String sql = "DELETE FROM followers WHERE followerId = ? AND followingId = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, followerId);
            stmt.setString(2, followingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isFollowing(String followerId, String followingId) {
        String sql = "SELECT COUNT(*) FROM followers WHERE followerId = ? AND followingId = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, followerId);
            stmt.setString(2, followingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getFollowerCount(String userId) {
        String sql = "SELECT COUNT(*) FROM followers WHERE followingId = ?";
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

    public int getFollowingCount(String userId) {
        String sql = "SELECT COUNT(*) FROM followers WHERE followerId = ?";
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

    public List<String> getFollowerIds(String userId) {
        List<String> followers = new ArrayList<>();
        String sql = "SELECT followerId FROM followers WHERE followingId = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                followers.add(rs.getString("followerId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return followers;
    }

    public List<String> getFollowingIds(String userId) {
        List<String> following = new ArrayList<>();
        String sql = "SELECT followingId FROM followers WHERE followerId = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                following.add(rs.getString("followingId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return following;
    }
}