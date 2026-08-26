package com.example.x.view.card;

import com.example.x.model.report.Report;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ReportCardView {

    @FXML private Label reportIdLabel;
    @FXML private Label reporterLabel;
    @FXML private Label reportedContentLabel;
    @FXML private Label reportedUserLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label statusLabel;
    @FXML private Button confirmButton;
    @FXML private Button rejectButton;
    @FXML private Button blockUserButton;
    @FXML private Button blockPostButton;

    private Report report;
    private Runnable onConfirm;
    private Runnable onReject;
    private Runnable onBlockUser;
    private Runnable onBlockPost;

    public void setReportData(Report report, Runnable onConfirm, Runnable onReject, Runnable onBlockUser, Runnable onBlockPost) {
        this.report = report;
        this.onConfirm = onConfirm;
        this.onReject = onReject;
        this.onBlockUser = onBlockUser;
        this.onBlockPost = onBlockPost;

        reportIdLabel.setText("Report #" + safeSubstring(report.getReportId(), 0, 8));
        reporterLabel.setText("Reported by: " + safeSubstring(report.getReporterId(), 0, 6));
        reportedContentLabel.setText("Content: " + safeSubstring(report.getReportedContentId(), 0, 6));
        reportedUserLabel.setText("Reported user: " + safeSubstring(report.getReportedUserId(), 0, 6));
        descriptionLabel.setText(report.getDescription() != null ? report.getDescription() : "");

        updateStatus(report.getStatus());
    }

    private String safeSubstring(String str, int start, int end) {
        if (str == null || str.isEmpty()) return "N/A";
        if (str.length() <= end) return str;
        return str.substring(start, end);
    }

    private void updateStatus(String status) {
        switch (status) {
            case "WAITING":
                statusLabel.setText("Waiting");
                statusLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                confirmButton.setVisible(true);
                rejectButton.setVisible(true);
                blockUserButton.setVisible(false);
                blockPostButton.setVisible(false);
                break;
            case "CONFIRMED":
                statusLabel.setText("Confirmed");
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                confirmButton.setVisible(false);
                rejectButton.setVisible(false);
                blockUserButton.setVisible(true);
                blockPostButton.setVisible(true);
                break;
            case "REJECTED":
                statusLabel.setText("Rejected");
                statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                confirmButton.setVisible(false);
                rejectButton.setVisible(false);
                blockUserButton.setVisible(false);
                blockPostButton.setVisible(false);
                break;
        }
    }

    @FXML
    void confirmButtonClicked(MouseEvent event) {
        if (onConfirm != null) onConfirm.run();
    }

    @FXML
    void rejectButtonClicked(MouseEvent event) {
        if (onReject != null) onReject.run();
    }

    @FXML
    void blockUserButtonClicked(MouseEvent event) {
        if (onBlockUser != null) onBlockUser.run();
    }

    @FXML
    void blockPostButtonClicked(MouseEvent event) {
        if (onBlockPost != null) onBlockPost.run();
    }

    public Report getReport() {
        return report;
    }
}