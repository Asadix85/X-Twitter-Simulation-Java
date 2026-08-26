package com.example.x.model.account.user.premiumUser;

import com.example.x.model.account.AccountBadge;

import java.io.Serializable;

public class BluePremiumUser extends PremiumUser implements Serializable {
    public BluePremiumUser(String fullName, String phoneNumber, String userName, String password, String userEmail) {
        super(fullName, phoneNumber, userName, password, userEmail, AccountBadge.BLUE_BADGE);
    }
}
