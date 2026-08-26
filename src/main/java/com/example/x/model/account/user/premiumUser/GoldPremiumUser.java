package com.example.x.model.account.user.premiumUser;

import com.example.x.model.account.AccountBadge;

import java.io.Serializable;

public class GoldPremiumUser extends PremiumUser implements Serializable {
    public GoldPremiumUser(String fullName, String phoneNumber, String userName, String password, String userEmail) {
        super(fullName, phoneNumber, userName, password, userEmail, AccountBadge.GOLD_BADGE);
    }
}
