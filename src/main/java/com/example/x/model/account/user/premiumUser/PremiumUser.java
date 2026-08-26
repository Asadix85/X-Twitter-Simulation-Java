package com.example.x.model.account.user.premiumUser;

import com.example.x.model.account.AccountBadge;
import com.example.x.model.account.user.User;

import java.io.Serializable;

public class PremiumUser extends User implements Serializable {
    public PremiumUser(String fullName, String phoneNumber, String userName, String password, String userEmail, AccountBadge accountBadge) {
        super(fullName, phoneNumber, userName, password, userEmail, accountBadge);
    }
}
