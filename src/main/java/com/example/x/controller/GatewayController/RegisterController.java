package com.example.x.controller.GatewayController;

import com.example.x.exceptions.authExceptions.*;
import com.example.x.model.account.Account;
import com.example.x.model.account.AccountBadge;
import com.example.x.model.account.user.User;
import com.example.x.model.account.user.normalUser.NormalUser;
import com.example.x.model.database.DataManager;

public class RegisterController {
    private final DataManager dataManager  = DataManager.getInstance();
    public boolean isRegisterSuccessful = false;

    private static final String USERNAME_REGEX =
            "^[a-zA-Z0-9_]{5,20}$";

    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    private static final String PHONE_REGEX =
            "^(\\+98|0)?9\\d{9}$";

    private static final String NAME_REGEX =
            "^[a-zA-Z\\s]{4,30}$";


    public String handleSignUp(String fullName, String username, String phoneNumber , String email, String password, String confirmPassword) throws AuthException {
        if(fullName.isBlank() || username.isBlank() || phoneNumber.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            throw new FillllFieldsException();
        }

        if(!fullName.trim().matches(NAME_REGEX)) {
            throw new InvalidFullNameException();
        }

        if(!username.trim().matches(USERNAME_REGEX)) {
            throw new InvalidUserNameException();
        }

        if(isUsernameTaken(username.trim())) {
            throw new DuplicateUserException();
        }

        if(!phoneNumber.trim().matches(PHONE_REGEX)) {
            throw new InvalidPhoneNumberException();
        }

        if(!email.trim().matches(EMAIL_REGEX)) {
            throw new InvalidEmailException();
        }

        if(!password.trim().matches(PASSWORD_REGEX)) {
            throw new WeakPasswordException();
        }

        if(!confirmPassword.trim().equals(password)) {
            throw new WrongPasswordException("Passwords do not match");
        }

        User user = new NormalUser(fullName.trim(), phoneNumber.trim() , username.trim(), password.trim(), email.trim());

        dataManager.addUser(user);
        isRegisterSuccessful = true;
        return "Signup successful";
    }


    private boolean isUsernameTaken(String username) {

        for(Account user : dataManager.getAllUsers()) {

            if(user.getUsername().equals(username.trim())) {
                return true;
            }
        }
        return false;
    }
}