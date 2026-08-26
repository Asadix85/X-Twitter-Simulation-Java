package com.example.x.controller;

import com.example.x.model.account.Account;
import com.example.x.model.account.AccountBadge;
import com.example.x.model.account.user.User;
import com.example.x.model.account.user.premiumUser.BluePremiumUser;
import com.example.x.model.account.user.premiumUser.GoldPremiumUser;
import com.example.x.model.database.DataManager;
import com.example.x.model.file.Photo;
import com.example.x.view.UserProfileView;

import java.time.LocalDate;
import java.util.List;

public class UserProfileController {

    private DataManager dataManager = DataManager.getInstance();
    private User currentUser = dataManager.getCurrentUser();
    private UserProfileView view;
    private final int BUY_BLUE_BADGE_COST = 9000;
    private final int BUY_GOLD_BADGE_COST = 19000;

    public void setView(UserProfileView view) {
        this.view = view;
    }

    public void refreshUserData() {
        if (currentUser != null) {
            Account refreshed = dataManager.findUserById(currentUser.getId());
            if (refreshed != null && refreshed instanceof User) {
                this.currentUser = (User) refreshed;
                dataManager.setCurrentUser(this.currentUser);
            }
        }
    }

    public void saveUserData() {
        if (currentUser != null) {
            dataManager.updateUser(currentUser);
            refreshUserData();
        }
    }

    public String getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }

    public String getName() {
        return currentUser != null ? currentUser.getFullName() : "";
    }

    public void setName(String name) {
        if (currentUser != null) {
            currentUser.setFullName(name);
        }
    }

    public String getBio() {
        return currentUser != null ? currentUser.getBio() : "";
    }

    public void setBio(String bio) {
        if (currentUser != null) {
            currentUser.setBio(bio);
        }
    }

    public AccountBadge getAccountBadge() {
        return currentUser != null ? currentUser.getAccountBadge() : AccountBadge.NONE_BADGE;
    }

    public LocalDate getMemberShipDate() {
        return currentUser != null ? currentUser.getMemberShipDate() : LocalDate.now();
    }

    public Photo getProfilePic() {
        return currentUser != null ? currentUser.getProfilePic() : null;
    }

    public int getTotalFollowers() {
        if (currentUser == null) return 0;
        return dataManager.getFollowerCount(currentUser.getId());
    }

    public int getTotalFollowing() {
        if (currentUser == null) return 0;
        return dataManager.getFollowingCount(currentUser.getId());
    }

    public void setNewEmail(String email) {
        if (currentUser != null) {
            currentUser.setEmail(email);
        }
    }

    public void setPassword(String password) {
        if (currentUser != null) {
            currentUser.setPassword(password);
        }
    }

    public void setBirthday(LocalDate birthday) {
        if (currentUser != null) {
            currentUser.setBirthDay(birthday);
        }
    }

    public String getUsername() {
        return currentUser != null ? currentUser.getUsername() : "";
    }

    public int getTokens() {
        return currentUser != null ? currentUser.getTokens() : 0;
    }

    public void setTokens(int tokens) {
        if (currentUser != null) {
            currentUser.setTokens(tokens);
            dataManager.updateTokenBalance(currentUser.getId(), currentUser.getTokens());
        }
    }

    public List<String> getPosts() {
        return currentUser != null ? currentUser.getPostIds() : List.of();
    }

    public List<String> getRepliesPostIds() {
        return currentUser != null ? currentUser.getRepliesPostIds() : List.of();
    }

    public List<String> getMediaPostIds() {
        return currentUser != null ? currentUser.getMediaPostIds() : List.of();
    }

    public boolean handleBuyBlueBadge() {
        if (currentUser == null) return false;

        if (currentUser.getTokens() >= BUY_BLUE_BADGE_COST) {
            currentUser.setTokens(-BUY_BLUE_BADGE_COST);

            BluePremiumUser blueUser = new BluePremiumUser(
                    currentUser.getFullName(),
                    currentUser.getPhoneNumber(),
                    currentUser.getUsername(),
                    currentUser.getPassword(),
                    currentUser.getEmail()
            );

            copyUserData(currentUser, blueUser);

            dataManager.deleteUser(currentUser.getId());
            dataManager.addUser(blueUser);
            dataManager.setCurrentUser(blueUser);
            currentUser = blueUser;

            return true;
        }
        return false;
    }

    public boolean handleBuyGoldBadge() {
        if (currentUser == null) return false;

        if (currentUser.getTokens() >= BUY_GOLD_BADGE_COST) {
            currentUser.setTokens(-BUY_GOLD_BADGE_COST);

            GoldPremiumUser goldUser = new GoldPremiumUser(
                    currentUser.getFullName(),
                    currentUser.getPhoneNumber(),
                    currentUser.getUsername(),
                    currentUser.getPassword(),
                    currentUser.getEmail()
            );

            copyUserData(currentUser, goldUser);

            dataManager.deleteUser(currentUser.getId());
            dataManager.addUser(goldUser);
            dataManager.setCurrentUser(goldUser);
            currentUser = goldUser;

            return true;
        }
        return false;
    }

    private void copyUserData(User from, User to) {
        to.setId(from.getId());
        to.setBio(from.getBio());
        to.setTokens(from.getTokens());
        to.setFollowerIds(from.getFollowerIds());
        to.setFollowingIds(from.getFollowingIds());
        to.setPostIds(from.getPostIds());
        to.setBlocked(from.isBlocked());
        to.setBirthDay(from.getBirthDay());
        to.setProfilePic(from.getProfilePic());
        to.setMemberShipDate(from.getMemberShipDate());
        to.setPersonalizationHashtags(from.getPersonalizationHashtags());
        to.setLikedPostIds(from.getLikedPostIds());
        to.setRepliesPostIds(from.getRepliesPostIds());
        to.setMediaPostIds(from.getMediaPostIds());
        to.setFullName(from.getFullName());
        to.setPhoneNumber(from.getPhoneNumber());
        to.setUsername(from.getUsername());
        to.setPassword(from.getPassword());
        to.setEmail(from.getEmail());
    }
}