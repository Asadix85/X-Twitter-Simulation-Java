package com.example.x;

import com.example.x.model.account.user.User;
import com.example.x.model.hashtag.Hashtag;
import com.example.x.model.post.Post;
import com.example.x.view.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Main instance;
    private Stage primaryStage;
    private User viewingUser;
    private Post replyingToPost;
    private Hashtag currentHashtag;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) throws IOException {
        instance = this;
        this.primaryStage = stage;
        goToGateway();
    }

    private void openScene(String fxml, int width, int height) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        primaryStage.setTitle("X");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void goToGateway() throws IOException {
        openScene("Gateway.fxml", 480, 503);
    }

    public void goToPersonalization() throws IOException {
        openScene("Personalization.fxml", 480, 503);
    }

    public void goToAdminPage() throws IOException {
        openScene("AdminPage.fxml", 651, 468);
    }

    public void goToHomePage() throws IOException {
        openScene("HomePage.fxml", 651, 468);
    }

    public void goToNewPostPage() throws IOException {
        this.replyingToPost = null;
        openScene("NewPost.fxml", 651, 468);
    }

    public void goToSearchPage() throws IOException {
        openScene("SearchPage.fxml", 651, 468);
    }

    public void goToUserprofile() throws IOException {
        openScene("UserProfile.fxml", 651, 468);
    }

    public void goToPostsListPage() throws IOException {
        openScene("PostsListPage.fxml", 651, 468);
    }

    public void goToReportListPage() throws IOException {
        openScene("ReportListPage.fxml", 651, 468);
    }

    public void goToPopularPostsPage() throws IOException {
        openScene("PopularPostsPage.fxml", 651, 468);
    }

    public void goToPopularHashtagsPage() throws IOException {
        openScene("PopularHashtagsPage.fxml", 651, 468);
    }

    public void goToUsersListPage() throws IOException {
        openScene("UsersListPage.fxml", 651, 468);
    }

    public void goToOtherprofile(User user) throws IOException {
        this.viewingUser = user;
        openScene("OtherProfile.fxml", 651, 468);
    }

    public User getViewingUser() {
        return viewingUser;
    }

    public void goToFollowersList(User user) throws IOException {
        this.viewingUser = user;
        openScene("FollowersListPage.fxml", 651, 468);
    }

    public void goToFollowingList(User user) throws IOException {
        this.viewingUser = user;
        openScene("FollowingListPage.fxml", 651, 468);
    }

    public void goToReplyPostPage(Post post) throws IOException {
        this.replyingToPost = post;
        openScene("NewPost.fxml", 651, 468);
    }

    public Post getReplyingToPost() {
        return replyingToPost;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void goToHashtagPage(Hashtag hashtag) throws IOException {
        this.currentHashtag = hashtag;
        openScene("HashtagPage.fxml", 651, 468);
    }

    public Hashtag getCurrentHashtag() {
        return currentHashtag;
    }

    public void goToLikersPage(Post post) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/LikersPage.fxml"));
        Scene scene = new Scene(loader.load(), 651, 468);

        LikersListView view = loader.getController();
        view.setPost(post);

        primaryStage.setTitle("X - Likes");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void goToReplyPage(Post post) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/ReplyPage.fxml"));
        Scene scene = new Scene(loader.load(), 700, 468);

        ReplyPageView view = loader.getController();
        view.setPost(post);

        primaryStage.setTitle("X - Replies");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void goToChatPage(String userId) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/ChatPage.fxml"));
        Scene scene = new Scene(loader.load(), 651, 468);

        ChatPageView view = loader.getController();
        view.setOtherUserId(userId);

        primaryStage.setTitle("X - Chat");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void goToMessagesList() throws IOException {
        openScene("MessagesListPage.fxml" , 651 , 468);
    }

    public void goToEditPostPage(Post post) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/EditPostPage.fxml"));
        Scene scene = new Scene(loader.load(), 651, 468);

        EditPostView view = loader.getController();
        view.setPost(post);

        primaryStage.setTitle("X - Edit Post");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void goToNotificationsPage() throws IOException {
        openScene("NotificationPage.fxml", 651, 468);
    }
}