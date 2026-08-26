package com.example.x.model.report;

import java.time.LocalDateTime;
import java.util.UUID;

public class Report {

    private int id;
    private String reportId;
    private String reporterId;
    private String reportedContentId;
    private String reportedUserId;
    private String status;
    private String description;
    private LocalDateTime reportDate;

    public Report(String reporterId, String reportedContentId, String reportedUserId, String description) {
        this.reportId = UUID.randomUUID().toString();
        this.reporterId = reporterId;
        this.reportedContentId = reportedContentId;
        this.reportedUserId = reportedUserId;
        this.description = description;
        this.status = "WAITING";
        this.reportDate = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getReporterId() { return reporterId; }
    public void setReporterId(String reporterId) { this.reporterId = reporterId; }

    public String getReportedContentId() { return reportedContentId; }
    public void setReportedContentId(String reportedContentId) { this.reportedContentId = reportedContentId; }

    public String getReportedUserId() { return reportedUserId; }
    public void setReportedUserId(String reportedUserId) { this.reportedUserId = reportedUserId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getReportDate() { return reportDate; }
    public void setReportDate(LocalDateTime reportDate) { this.reportDate = reportDate; }

    public boolean isPending() { return status.equals("WAITING"); }
    public boolean isConfirmed() { return status.equals("CONFIRMED"); }
    public boolean isRejected() { return status.equals("REJECTED"); }
}