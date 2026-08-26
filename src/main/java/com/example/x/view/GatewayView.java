package com.example.x.view;

import com.example.x.Main;
import com.example.x.controller.GatewayController.LoginController;
import com.example.x.controller.GatewayController.RegisterController;
import com.example.x.exceptions.authExceptions.AuthException;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.IOException;

public class GatewayView {
    private String confirmPassword;
    private String fullName;
    private String userEmail;
    private String password;
    private String phoneNumber;
    private String username;
    private String loginUsernameOrEmail;
    private String loginPassword;

    RegisterController registerController = new RegisterController();
    LoginController loginController = new LoginController();

    @FXML private TextField ConfirmPR;
    @FXML private Text registerCondition;
    @FXML private PasswordField LoginPasswordPF;
    @FXML private TextField emailR;
    @FXML private TextField loginUsernameOrEmailTF;
    @FXML private TextField nameR;
    @FXML private TextField passwordR;
    @FXML private TextField phoneNumberR;
    @FXML private TextField usernameR;

    @FXML
    void SignInClicked(MouseEvent event) throws IOException {
        loginUsernameOrEmail = loginUsernameOrEmailTF.getText();
        loginPassword = LoginPasswordPF.getText();

        try {
            String result = loginController.loginHandle(loginUsernameOrEmail, loginPassword);

            if (loginController.isLoginSuccessful) {
                registerCondition.setText(result);
                registerCondition.setVisible(true);

                if (loginController.isAdmin) {
                    Main.getInstance().goToAdminPage();
                    return;
                }

                if (loginController.shouldGoToPersonalization()) {
                    Main.getInstance().goToPersonalization();
                } else {
                    Main.getInstance().goToHomePage();
                }
            }
        } catch (AuthException e) {
            registerCondition.setText("Authentication error: " + e.getMessage());
            registerCondition.setVisible(true);
        } catch (Exception e) {
            registerCondition.setText("An unexpected error occurred: " + e.getMessage());
            registerCondition.setVisible(true);
            e.printStackTrace();
        }
    }

    @FXML
    void SignUpClicked(MouseEvent event) {
        username = usernameR.getText();
        password = passwordR.getText();
        fullName = nameR.getText();
        phoneNumber = phoneNumberR.getText();
        confirmPassword = ConfirmPR.getText();
        userEmail = emailR.getText();

        try {
            String result = registerController.handleSignUp(fullName, username, phoneNumber, userEmail, password, confirmPassword);
            registerCondition.setText(result);
            registerCondition.setVisible(true);
            if (registerController.isRegisterSuccessful) {
                usernameR.clear();
                passwordR.clear();
                nameR.clear();
                phoneNumberR.clear();
                ConfirmPR.clear();
                emailR.clear();
                try {
                    Main.getInstance().goToPersonalization();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (AuthException e) {
            registerCondition.setText(e.getMessage());
            registerCondition.setVisible(true);

        } catch (Exception e) {
            registerCondition.setText("An unexpected error occurred: " + e.getMessage());
            registerCondition.setVisible(true);
            e.printStackTrace();
        }
    }
}