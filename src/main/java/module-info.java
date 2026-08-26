module com.example.x {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.x to javafx.fxml;
    opens com.example.x.view to javafx.fxml;
    opens com.example.x.controller to javafx.fxml;
    exports com.example.x;
    opens com.example.x.controller.GatewayController to javafx.fxml;
    exports com.example.x.controller;
    opens com.example.x.view.card to javafx.fxml;
}