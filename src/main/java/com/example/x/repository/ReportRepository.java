package com.example.x.repository;

import com.example.x.model.report.Report;
import com.example.x.model.database.ConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportRepository {


    public boolean save(Report report) {
        String sql = "INSERT INTO reports (reporterId, reportedContentId, reportedUserId, status, description, reportDate) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, report.getReporterId());
            stmt.setString(2, report.getReportedContentId());
            stmt.setString(3, report.getReportedUserId());
            stmt.setString(4, report.getStatus());
            stmt.setString(5, report.getDescription());
            stmt.setTimestamp(6, Timestamp.valueOf(report.getReportDate()));

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    report.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM reports WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE reports SET status = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean confirmReport(int id) {
        return updateStatus(id, "CONFIRMED");
    }

    public boolean rejectReport(int id) {
        return updateStatus(id, "REJECTED");
    }

    public List<Report> findAll() {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports ORDER BY reportDate DESC";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public Report findById(int id) {
        String sql = "SELECT * FROM reports WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToReport(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Report> findByReporter(String reporterId) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE reporterId = ? ORDER BY reportDate DESC";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reporterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public List<Report> findByReportedUser(String reportedUserId) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE reportedUserId = ? ORDER BY reportDate DESC";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reportedUserId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public List<Report> findByStatus(String status) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE status = ? ORDER BY reportDate DESC";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public List<Report> findByContentId(String reportedContentId) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE reportedContentId = ? ORDER BY reportDate DESC";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reportedContentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public List<Report> getPendingReports() {
        return findByStatus("WAITING");
    }

    public List<Report> getConfirmedReports() {
        return findByStatus("CONFIRMED");
    }

    public List<Report> getRejectedReports() {
        return findByStatus("REJECTED");
    }

    public int getReportCount() {
        String sql = "SELECT COUNT(*) FROM reports";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getReportCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM reports WHERE status = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean hasUserReported(String reporterId, String reportedContentId) {
        String sql = "SELECT COUNT(*) FROM reports WHERE reporterId = ? AND reportedContentId = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reporterId);
            stmt.setString(2, reportedContentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAllByReportedContent(String reportedContentId) {
        String sql = "DELETE FROM reports WHERE reportedContentId = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reportedContentId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Report mapResultSetToReport(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String reporterId = rs.getString("reporterId");
        String reportedContentId = rs.getString("reportedContentId");
        String reportedUserId = rs.getString("reportedUserId");
        String status = rs.getString("status");
        String description = rs.getString("description");
        LocalDateTime reportDate = rs.getTimestamp("reportDate").toLocalDateTime();

        Report report = new Report(reporterId, reportedContentId, reportedUserId, description);
        report.setId(id);
        report.setStatus(status);
        report.setReportDate(reportDate);

        return report;
    }
}