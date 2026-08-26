package com.example.x.controller.GatewayController;

import com.example.x.exceptions.authExceptions.AuthException;
import com.example.x.exceptions.authExceptions.UserNotFoundException;
import com.example.x.exceptions.authExceptions.WrongPasswordException;
import com.example.x.model.account.Account;
import com.example.x.model.account.admin.Admin;
import com.example.x.model.account.user.User;
import com.example.x.model.database.DataManager;

public class LoginController {

    DataManager dataManager = DataManager.getInstance();
    public boolean isLoginSuccessful = false;
    public boolean hasPersonalizationHashtags = false;
    public boolean isAdmin = false;
    Account account;

    public String loginHandle(String username, String password) throws AuthException {
        account = dataManager.findUser(username);

        if (account == null) {
            throw new UserNotFoundException();
        } else if (!account.getPassword().equals(password)) {
            throw new WrongPasswordException();
        } else {
            isLoginSuccessful = true;

            if (account instanceof User) {
                dataManager.setCurrentUser((User) account);

                var personalizationHashtags = dataManager.getCurrentUser().getPersonalizationHashtags();
                hasPersonalizationHashtags = personalizationHashtags != null && !personalizationHashtags.isEmpty();
            }

            if (account instanceof Admin) {
                isAdmin = true;
            }

            return "Login successful";
        }
    }
    public boolean shouldGoToPersonalization() {
        return isLoginSuccessful &&
                !isAdmin &&
                account instanceof User &&
                !hasPersonalizationHashtags;
    }
}