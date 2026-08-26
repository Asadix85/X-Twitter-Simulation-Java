package com.example.x.controller;

import com.example.x.Main;
import com.example.x.model.account.Account;
import com.example.x.model.account.user.User;
import com.example.x.model.database.DataManager;
import com.example.x.model.hashtag.Hashtag;
import com.example.x.model.post.Post;
import com.example.x.model.report.Report;
import com.example.x.view.*;
import com.example.x.view.card.HashtagCardView;
import com.example.x.view.card.PostCardView;
import com.example.x.view.card.ReportCardView;
import com.example.x.view.card.UserCardView;

import com.example.x.view.PostsListView;
import com.example.x.view.ReportsListView;
import com.example.x.view.UsersListView;
import com.example.x.view.AdminPageView;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AdminPageController {

    private DataManager dataManager;

    public AdminPageController() {
        this.dataManager = DataManager.getInstance();
    }

    public void loadPopularPosts(PopularPostsView view) throws IOException {
        view.getPopularPostsContainer().getChildren().clear();

        List<Post> popularPosts = dataManager.getAllPosts().stream()
                .sorted(Comparator.comparingInt(Post::getTotalLikes).reversed())
                .limit(20)
                .collect(Collectors.toList());

        for (Post post : popularPosts) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/PostCard.fxml"));
            AnchorPane card = loader.load();
            PostCardView controller = loader.getController();
            controller.setPostData(post);
            view.getPopularPostsContainer().getChildren().add(card);
        }
    }

    public void loadPopularHashtags(PopularHashtagsView view) throws IOException {
        view.getPopularHashtagsContainer().getChildren().clear();

        List<Hashtag> popularHashtags = dataManager.getAllHashtags().stream()
                .sorted(Comparator.comparingInt(Hashtag::getUsageCount).reversed())
                .limit(20)
                .collect(Collectors.toList());

        for (Hashtag hashtag : popularHashtags) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/HashtagCard.fxml"));
            AnchorPane card = loader.load();
            HashtagCardView controller = loader.getController();
            controller.setHashtagData(hashtag, () -> {
                try {
                    Main.getInstance().goToHashtagPage(hashtag);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            view.getPopularHashtagsContainer().getChildren().add(card);
        }
    }

    public void loadAllPosts(PostsListView view) throws IOException {
        view.getPostsListContainer().getChildren().clear();

        for (Post post : dataManager.getAllPosts()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/PostCard.fxml"));
            AnchorPane card = loader.load();
            PostCardView controller = loader.getController();
            controller.setPostData(post);
            view.getPostsListContainer().getChildren().add(card);
        }
    }

    public void searchPosts(PostsListView view, String query) throws IOException {
        view.getPostsListContainer().getChildren().clear();

        List<Post> results = dataManager.searchPosts(query);
        for (Post post : results) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/PostCard.fxml"));
            AnchorPane card = loader.load();
            PostCardView controller = loader.getController();
            controller.setPostData(post);
            view.getPostsListContainer().getChildren().add(card);
        }
    }

    public void loadAllUsers(UsersListView view) throws IOException {
        view.getUsersListContainer().getChildren().clear();

        for (Account user : dataManager.getAllUsers()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/UserCard.fxml"));
            AnchorPane card = loader.load();
            UserCardView controller = loader.getController();
            controller.setUserData(user, () -> {
                if (user instanceof User) {
                    try {
                        Main.getInstance().goToOtherprofile((User) user);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            view.getUsersListContainer().getChildren().add(card);
        }
    }

    public void searchUsers(UsersListView view, String query) throws IOException {
        view.getUsersListContainer().getChildren().clear();

        List<Account> results = dataManager.searchUsers(query);
        for (Account user : results) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/UserCard.fxml"));
            AnchorPane card = loader.load();
            UserCardView controller = loader.getController();
            controller.setUserData(user, () -> {
                if (user instanceof User) {
                    try {
                        Main.getInstance().goToOtherprofile((User) user);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            view.getUsersListContainer().getChildren().add(card);
        }
    }

    public void loadAllReports(ReportsListView view) throws IOException {
        view.getReportsListContainer().getChildren().clear();

        List<Report> reports = dataManager.getAllReports();

        for (Report report : reports) {
            addReportCard(view, report);
        }
    }

    public void filterReports(ReportsListView view, String filter) throws IOException {
        view.getReportsListContainer().getChildren().clear();

        List<Report> reports;
        if (filter.equals("ALL")) {
            reports = dataManager.getAllReports();
        } else {
            reports = dataManager.getAllReports().stream()
                    .filter(r -> r.getStatus().equals(filter))
                    .collect(Collectors.toList());
        }

        view.setReportCount(reports.size());

        for (Report report : reports) {
            addReportCard(view, report);
        }
    }

    private void addReportCard(ReportsListView view, Report report) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/ReportCard.fxml"));
        AnchorPane card = loader.load();
        ReportCardView controller = loader.getController();
        controller.setReportData(
                report,
                () -> {
                    dataManager.confirmReport(report.getId());
                    try { filterReports(view, view.getCurrentFilter()); } catch (IOException e) { e.printStackTrace(); }
                },
                () -> {
                    dataManager.rejectReport(report.getId());
                    try { filterReports(view, view.getCurrentFilter()); } catch (IOException e) { e.printStackTrace(); }
                },
                () -> {
                    User user = (User) dataManager.findUserById(report.getReportedUserId());
                    if (user != null) {
                        user.setBlocked(true);
                        dataManager.updateUser(user);
                    }
                },
                () -> {
                    Post post = dataManager.findPostById(report.getReportedContentId());
                    if (post != null) {
                        post.setLocked(true);
                        dataManager.updatePost(post);
                    }
                }
        );
        view.getReportsListContainer().getChildren().add(card);
    }

    public void loadAdminStats(AdminPageView view) {
        view.setTotalPosts(dataManager.getAllPosts().size());
        view.setTotalUsers(dataManager.getAllUsers().size());
    }
}