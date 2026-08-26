package com.example.x.controller;

import com.example.x.model.account.user.User;
import com.example.x.model.database.DataManager;
import com.example.x.model.hashtag.Hashtag;

import java.util.ArrayList;
import java.util.List;

public class PersonalizationController {

    private final DataManager dataManager = DataManager.getInstance();
    private final User currentUser = dataManager.getCurrentUser();
    private List<Hashtag> selectedHashtags;
    private boolean confirmed;

    public PersonalizationController() {
        this.selectedHashtags = new ArrayList<>();
        this.confirmed = false;
        loadUserHashtags();
    }

    private void loadUserHashtags() {
        if (currentUser != null && currentUser.getPersonalizationHashtags() != null) {
            for (Hashtag tag : currentUser.getPersonalizationHashtags()) {
                if (!selectedHashtags.contains(tag)) {
                    selectedHashtags.add(tag);
                }
            }
        }
    }

    public boolean hasUserSelectedHashtags() {
        return currentUser != null &&
                currentUser.getPersonalizationHashtags() != null &&
                !currentUser.getPersonalizationHashtags().isEmpty();
    }

    public List<Hashtag> getUserHashtags() {
        if (currentUser != null && currentUser.getPersonalizationHashtags() != null) {
            return currentUser.getPersonalizationHashtags();
        }
        return new ArrayList<>();
    }

    public void toggleHashtag(String title) {
        Hashtag found = findHashtag(title);

        if (found != null) {
            selectedHashtags.remove(found);
        } else {
            if (selectedHashtags.size() < 4) {
                Hashtag existingTag = dataManager.findHashtagByTitle("#" + title);
                if (existingTag != null) {
                    selectedHashtags.add(existingTag);
                } else {
                    Hashtag newTag = new Hashtag("#" + title);
                    dataManager.addHashtag(newTag);
                    selectedHashtags.add(newTag);
                }
            }
        }
    }

    private Hashtag findHashtag(String title) {
        String fullTitle = title.startsWith("#") ? title : "#" + title;
        for (Hashtag h : selectedHashtags) {
            if (h.getTitle().equalsIgnoreCase(fullTitle)) {
                return h;
            }
        }
        return null;
    }

    public boolean isSelected(String title) {
        return findHashtag(title) != null;
    }

    public int getSelectedCount() {
        return selectedHashtags.size();
    }

    public List<Hashtag> getSelectedHashtags() {
        return selectedHashtags;
    }

    public void confirm() {
        if (selectedHashtags.size() <= 4 && selectedHashtags.size() > 0) {
            confirmed = true;
            currentUser.setPersonalizationHashtags(selectedHashtags);
            dataManager.updateUser(currentUser);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}