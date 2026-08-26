package com.example.x.model.account;

import com.example.x.model.file.Photo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

abstract public class Account implements Serializable {
    protected String id;
    protected String fullName;
    protected String phoneNumber;
    protected String username;
    protected LocalDate birthDay;
    protected Photo profilePic;
    protected String password;
    protected String email;
    protected LocalDate memberShipDate;
    protected boolean isOnline;

    public Account(String fullName, String phoneNumber, String username, String password, String email) {
        this.id = UUID.randomUUID().toString();
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.email = email;
        this.memberShipDate = LocalDate.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    public Photo getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Photo profilePic) {
        this.profilePic = profilePic;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getMemberShipDate() {
        return memberShipDate;
    }

    public void setMemberShipDate(LocalDate memberShipDate) {
        this.memberShipDate = memberShipDate;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }
}
