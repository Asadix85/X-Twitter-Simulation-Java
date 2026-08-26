package com.example.x.controller;

import com.example.x.model.account.AccountBadge;
import com.example.x.model.account.user.User;
import com.example.x.model.database.DataManager;
import com.example.x.model.file.Photo;
import com.example.x.model.report.Report;

import java.time.LocalDate;
import java.util.List;

public class OtherProfileController {
    DataManager dataManager  = DataManager.getInstance();
    User otherUser;

    public void setOtherUser(User otherUser) {
        this.otherUser = otherUser;
    }

    public User getOtherUser() {
        return otherUser;
    }

    public String getName(){
        return otherUser.getFullName();
    }
    public String getBio(){
        return otherUser.getBio();
    }
    public AccountBadge getAccountBadge(){
        return otherUser.getAccountBadge();
    }
    public LocalDate getMemberShipDate(){
        return otherUser.getMemberShipDate();
    }
    public Photo getProfilePic(){
        return otherUser.getProfilePic();
    }
    public int getTotalFollowers(){
        return otherUser.getFollowerIds().size();
    }
    public int getTotalFollowing(){
        return otherUser.getFollowingIds().size();
    }
    public String getUsername() {
        return otherUser.getUsername();
    }
    public void addFollower(User user) {
        otherUser.addFollowerIds(user);
    }
    public List<String> getPosts() {
        return otherUser.getPostIds();
    }
    public void addToReportsList(User user) {
        Report report = new Report(user.getId() , "" , otherUser.getId() , "account reported by " + dataManager.getCurrentUser());
        dataManager.getAllReports().add(report);
    }

}
