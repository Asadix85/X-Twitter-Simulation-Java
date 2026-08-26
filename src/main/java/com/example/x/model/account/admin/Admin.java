package com.example.x.model.account.admin;

import com.example.x.model.account.Account;

import java.io.Serializable;

public class Admin extends Account implements Serializable {
    private static Admin instance;

    public Admin(String userName, String password, String email) {
        super("", "", userName, password, email);
    }

    public static Admin getInstance(String userName, String password, String email) {
        if (instance == null)
            instance = new Admin(userName, password, email);
        return instance;
    }
}
