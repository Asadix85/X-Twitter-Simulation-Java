package com.example.x.view.card;

import com.example.x.Main;
import com.example.x.model.account.Account;
import com.example.x.model.account.user.User;
import com.example.x.model.account.user.premiumUser.BluePremiumUser;
import com.example.x.model.account.user.premiumUser.GoldPremiumUser;
import com.example.x.model.account.user.premiumUser.PremiumUser;
import com.example.x.model.database.DataManager;
import com.example.x.model.hashtag.Hashtag;
import com.example.x.model.post.Post;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostCardView {

    private boolean isLiked = false;
    private boolean isFollowing = false;

    @FXML private ImageView userProfilePic;
    @FXML private Label accountNameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label postPublicationDateLabel;
    @FXML private Button followButton;
    @FXML private Label totalLikesLabel;
    @FXML private Label totalCommentsLabel;
    @FXML private Label viewLabel;
    @FXML private ImageView likeIcon;
    @FXML private VBox postContentBox;
    @FXML private VBox mediaContainer;
    @FXML private Label editText;
    @FXML private ImageView editIcon;
    @FXML private ImageView accountBadge;
    @FXML private ImageView deletePostIcon;

    private Post post;
    private Account author;
    private DataManager dataManager = DataManager.getInstance();
    private User currentUser = dataManager.getCurrentUser();

    public void setPostData(Post post) {
        this.post = post;
        this.author = dataManager.findUserById(post.getAuthorId());

        if (author != null) {
            accountNameLabel.setText(author.getFullName() != null ? author.getFullName() : "");
            usernameLabel.setText("@" + author.getUsername());

            if (author.getId().equals(currentUser.getId())) {
                followButton.setVisible(false);
            } else {
                isFollowing = dataManager.isFollowing(currentUser.getId(), author.getId());
                updateFollowButton();
            }


            boolean isOwner = post.getAuthorId().equals(currentUser.getId());
            editIcon.setVisible(isOwner);
            editIcon.setDisable(!isOwner);
            editText.setVisible(isOwner);
            deletePostIcon.setVisible(isOwner);
            deletePostIcon.setDisable(!isOwner);

            if (author instanceof PremiumUser) {
                editIcon.setImage(new Image(getClass().getResourceAsStream("/Icon/edit_file2_208px.png")));
            }

            if (author instanceof BluePremiumUser) {
                accountBadge.setImage(new Image(getClass().getResourceAsStream("/Icon/approvalB_480px.png")));
                accountBadge.setVisible(true);
            } else if (author instanceof GoldPremiumUser) {
                accountBadge.setVisible(true);
            }

            if (post.hasAttachment()) {
                showMedia(post.getMedia());
            } else {
                mediaContainer.setVisible(false);
                mediaContainer.setPrefHeight(0);
            }
        }

        buildPostText(post.getPostText());

        if (post.getPublishDate() != null) {
            postPublicationDateLabel.setText(post.getPublishDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd 'at' HH:mm")));
        }

        int likeCount = dataManager.getLikeCount(post.getPostId());
        totalLikesLabel.setText(String.valueOf(likeCount));

        int commentCount = dataManager.getCommentCount(post.getPostId());
        totalCommentsLabel.setText(String.valueOf(commentCount));

        viewLabel.setText(String.valueOf(dataManager.getViewCount(post.getPostId())));

        if (dataManager.hasUserLikedPost(currentUser.getId(), post.getPostId())) {
            isLiked = true;
            likeIcon.setImage(new Image(getClass().getResourceAsStream("/Icon/favorite_208px.png")));
        } else {
            isLiked = false;
            likeIcon.setImage(new Image(getClass().getResourceAsStream("/Icon/favorite_480px.png")));
        }
    }

    private void updateFollowButton() {
        if (isFollowing) {
            followButton.setText("Following");
            followButton.setStyle("-fx-background-color: green; -fx-background-radius: 25;");
        } else {
            followButton.setText("Follow");
            followButton.setStyle("-fx-background-color: blue; -fx-background-radius: 25;");
        }
    }

    private void buildPostText(String text) {
        postContentBox.getChildren().clear();

        if (text == null || text.isEmpty()) return;

        TextFlow textFlow = new TextFlow();
        textFlow.setLineSpacing(3);

        Pattern pattern = Pattern.compile("(#\\w+)");
        Matcher matcher = pattern.matcher(text);

        int lastEnd = 0;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                Text normalText = new Text(text.substring(lastEnd, matcher.start()));
                normalText.setFont(Font.font("System", 13));
                textFlow.getChildren().add(normalText);
            }

            String hashtagText = matcher.group(1);
            Text hashtagNode = new Text(hashtagText);
            hashtagNode.setFill(Color.web("#1DA1F2"));
            hashtagNode.setFont(Font.font("System Bold", 13));
            hashtagNode.setCursor(Cursor.HAND);
            hashtagNode.setOnMouseClicked(e -> {
                try {
                    openHashtagPage(hashtagText);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            hashtagNode.setOnMouseEntered(e -> hashtagNode.setUnderline(true));
            hashtagNode.setOnMouseExited(e -> hashtagNode.setUnderline(false));

            textFlow.getChildren().add(hashtagNode);
            lastEnd = matcher.end();
        }

        if (lastEnd < text.length()) {
            Text remainingText = new Text(text.substring(lastEnd));
            remainingText.setFont(Font.font("System", 13));
            textFlow.getChildren().add(remainingText);
        }

        postContentBox.getChildren().add(textFlow);
    }

    private void openHashtagPage(String hashtagTitle) throws IOException {
        if (hashtagTitle != null && !hashtagTitle.isEmpty()) {
            String title = hashtagTitle.startsWith("#") ? hashtagTitle : "#" + hashtagTitle;
            Hashtag hashtag = dataManager.findHashtagByTitle(title);
            if (hashtag != null) {
                Main.getInstance().goToHashtagPage(hashtag);
            }
        }
    }

    @FXML
    void userProfileClicked(MouseEvent event) throws IOException {
        if (author != null && author instanceof User) {
            Main.getInstance().goToOtherprofile((User) author);
        }
    }

    @FXML
    void followButtonClicked(MouseEvent event) {
        if (author instanceof User) {
            if (isFollowing) {
                dataManager.unfollowUser(currentUser.getId(), author.getId());
                currentUser.removeFollowingIds((User) author);
                ((User) author).removeFollowerIds(currentUser);
                isFollowing = false;
            } else {
                dataManager.followUser(currentUser.getId(), author.getId());
                currentUser.addFollowingIds((User) author);
                ((User) author).addFollowerIds(currentUser);
                isFollowing = true;
            }
            updateFollowButton();
        }
    }

    @FXML
    void likeClicked(MouseEvent event) {
        if (!isLiked) {
            dataManager.incrementLikeCount(post.getPostId(), currentUser.getId());
            post.incrementLikeCount();
            post.addLikedUserId(currentUser.getId());
            likeIcon.setImage(new Image(getClass().getResourceAsStream("/Icon/favorite_208px.png")));
            isLiked = true;
            currentUser.addLikedPostIds(post.getPostId());
        } else {
            dataManager.decrementLikeCount(post.getPostId(), currentUser.getId());
            post.decrementLikeCount();
            post.removeLikedUserId(currentUser.getId());
            likeIcon.setImage(new Image(getClass().getResourceAsStream("/Icon/favorite_480px.png")));
            isLiked = false;
            currentUser.removeLikedPostIds(post.getPostId());
        }
        int likeCount = dataManager.getLikeCount(post.getPostId());
        totalLikesLabel.setText(String.valueOf(likeCount));
    }

    @FXML
    void likesCountClicked(MouseEvent event) throws IOException {
        if (post != null) {
            Main.getInstance().goToLikersPage(post);
        }
    }

    @FXML
    void replyButtonClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToReplyPostPage(post);
    }

    @FXML
    void commentClicked(MouseEvent event) throws IOException {
        if (post != null) {
            Main.getInstance().goToReplyPage(post);
        }
    }

    @FXML
    void increaseView(MouseEvent event) {
        dataManager.incrementViewCount(post.getPostId());
        viewLabel.setText(String.valueOf(dataManager.getViewCount(post.getPostId())));
    }

    @FXML
    void editIconClicked(MouseEvent event) throws IOException {
        if (currentUser instanceof PremiumUser) {
            Main.getInstance().goToEditPostPage(post);
        }
    }

    @FXML
    void deletePostClicked(MouseEvent event) {
        if (post == null) return;
        dataManager.deletePost(post.getPostId());
        currentUser.getPostIds().remove(post.getPostId());
        System.out.println("Post deleted: " + post.getPostId());
        try {
            Main.getInstance().goToHomePage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMedia(com.example.x.model.file.File media) {
        mediaContainer.getChildren().clear();
        mediaContainer.setVisible(true);

        if (media instanceof com.example.x.model.file.Photo) {
            com.example.x.model.file.Photo photo = (com.example.x.model.file.Photo) media;

            HBox previewBox = new HBox(10);
            previewBox.setAlignment(Pos.CENTER_LEFT);
            previewBox.setPadding(new Insets(5));
            previewBox.setStyle("-fx-background-color: #e8f5e9; -fx-background-radius: 8;");

            Label icon = new Label("🖼");
            icon.setStyle("-fx-font-size: 20;");

            Label info = new Label(" (" + photo.getFormat() + ")");
            info.setStyle("-fx-font-size: 11; -fx-text-fill: gray;");

            previewBox.getChildren().addAll(icon, info);
            mediaContainer.getChildren().add(previewBox);

        } else if (media instanceof com.example.x.model.file.Video) {
            com.example.x.model.file.Video video = (com.example.x.model.file.Video) media;

            HBox previewBox = new HBox(10);
            previewBox.setAlignment(Pos.CENTER_LEFT);
            previewBox.setPadding(new Insets(5));
            previewBox.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 8;");

            Label icon = new Label("🎬");
            icon.setStyle("-fx-font-size: 20;");

            Label info = new Label(" (" + video.getQuality() + "p)");
            info.setStyle("-fx-font-size: 11; -fx-text-fill: gray;");

            previewBox.getChildren().addAll(icon, info);
            mediaContainer.getChildren().add(previewBox);
        }

    }
}