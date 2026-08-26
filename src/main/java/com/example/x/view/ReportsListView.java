package com.example.x.view;

import com.example.x.Main;
import com.example.x.controller.AdminPageController;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ReportsListView {


    private AdminPageController controller;

    @FXML private ComboBox<String> reportFilterCombo;
    @FXML private Label reportCountLabel;
    @FXML private VBox reportsListContainer;


    @FXML
    public void initialize() throws IOException {
        controller = new AdminPageController();

        reportFilterCombo.getItems().addAll("ALL", "WAITING", "CONFIRMED", "REJECTED");
        reportFilterCombo.setValue("ALL");

        controller.loadAllReports(this);
    }

    @FXML
    private void filterReports() throws IOException {
        controller.filterReports(this, reportFilterCombo.getValue());
    }

    @FXML
    private void backToAdminClicked() throws IOException {
        Main.getInstance().goToAdminPage();
    }

    public VBox getReportsListContainer() {
        return reportsListContainer;
    }

    public void setReportCount(int count) {
        reportCountLabel.setText(count + " reports");
    }

    public String getCurrentFilter() {
        return reportFilterCombo != null ? reportFilterCombo.getValue() : "ALL";
    }
}