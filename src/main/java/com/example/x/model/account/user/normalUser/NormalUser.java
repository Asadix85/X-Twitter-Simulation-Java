package com.example.x.model.account.user.normalUser;

import com.example.x.model.account.AccountBadge;
import com.example.x.model.account.user.User;

import java.io.Serializable;

public class NormalUser extends User implements Serializable {
    public NormalUser(String fullName, String phoneNumber, String userName, String password, String userEmail) {
        super(fullName, phoneNumber, userName, password, userEmail, AccountBadge.NONE_BADGE);
    }
}
