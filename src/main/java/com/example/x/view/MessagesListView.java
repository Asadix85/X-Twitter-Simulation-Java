package com.example.x.view;

import com.example.x.Main;
import com.example.x.model.account.Account;
import com.example.x.model.database.DataManager;
import com.example.x.model.account.user.User;
import com.example.x.model.chat.ChatMessage;
import com.example.x.repository.MessageRepository;
import com.example.x.view.card.UserCardView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.*;

public class MessagesListView {

    @FXML private VBox chatListContainer;

    private DataManager dataManager;
    private MessageRepository messageRepository;

    @FXML
    public void initialize() {
        this.dataManager = DataManager.getInstance();
        this.messageRepository = new MessageRepository();
        loadChatList();
    }

    private void loadChatList() {
        chatListContainer.getChildren().clear();

        User currentUser = dataManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("No user logged in!");
            return;
        }

        List<ChatMessage> allMessages = messageRepository.getMessagesForUser(currentUser.getId());

        Set<String> chatUserIds = new HashSet<>();
        for (ChatMessage msg : allMessages) {
            if (msg.getSenderId().equals(currentUser.getId())) {
                chatUserIds.add(msg.getReceiverId());
            } else if (msg.getReceiverId().equals(currentUser.getId())) {
                chatUserIds.add(msg.getSenderId());
            }
        }

        for (String userId : chatUserIds) {
            Account user = dataManager.findUserById(userId);
            if (user != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/x/UserCard.fxml"));
                    AnchorPane card = loader.load();
                    UserCardView controller = loader.getController();
                    controller.setUserData(user, () -> openChat(user));
                    chatListContainer.getChildren().add(card);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void openChat(Account user) {
        try {
            Main.getInstance().goToChatPage(user.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML void homeClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToHomePage();
    }

    @FXML void SearchClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToSearchPage();
    }

    @FXML void profileClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToUserprofile();
    }

    @FXML void newButtonClicked(MouseEvent event) throws IOException {
        Main.getInstance().goToNewPostPage();
    }
}