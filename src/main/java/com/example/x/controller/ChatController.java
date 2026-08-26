package com.example.x.controller;

import com.example.x.model.account.Account;
import com.example.x.model.account.user.User;
import com.example.x.model.chat.ChatMessage;
import com.example.x.model.database.DataManager;
import com.example.x.model.notification.Notification;
import com.example.x.network.NetworkPacket;
import com.example.x.network.NetworkManager;
import com.example.x.repository.MessageRepository;
import com.example.x.view.ChatPageView;
import javafx.application.Platform;
import java.util.List;

public class ChatController {

    private final DataManager dataManager;
    private final MessageRepository messageRepository;
    private final NetworkManager networkManager;
    private final ChatPageView view;

    private User currentUser;
    private Account otherUser;
    private String otherUserId;
    private boolean isConnected = false;

    public ChatController(ChatPageView view) {
        this.view = view;
        this.dataManager = DataManager.getInstance();
        this.messageRepository = new MessageRepository();
        this.networkManager = new NetworkManager();
        this.currentUser = dataManager.getCurrentUser();
        setupNetworkListeners();
    }

    private void setupNetworkListeners() {
        networkManager.setOnMessageReceived(this::handleNewMessage);
        networkManager.setOnDisconnected(() -> Platform.runLater(() -> {
            isConnected = false;
            view.showError("Disconnected from server! Reconnecting...");
        }));
        networkManager.setOnError(error -> Platform.runLater(() -> view.showError(error)));
    }

    public void connectToServer() {
        if (networkManager.isConnected()) return;

        boolean connected = networkManager.connect("localhost", 12345);
        if (connected) {
            isConnected = true;
            System.out.println("Connected to chat server!");

            if (currentUser != null) {
                networkManager.login(currentUser.getUsername(), currentUser.getPassword());
            }
            networkManager.getOnlineUsers();
            loadMessages();
        } else {
            view.showError("Failed to connect to chat server!");
        }
    }

    private void loadMessages() {
        if (currentUser == null || otherUserId == null) {
            view.showError("loadMessages: currentUser or otherUserId is null");
            return;
        }

        List<ChatMessage> conversation = messageRepository.getConversation(
                currentUser.getId(), otherUserId);

        messageRepository.markAllAsRead(currentUser.getId(), otherUserId);

        Platform.runLater(() -> {
            view.clearMessages();
            for (ChatMessage msg : conversation) {
                boolean isMine = msg.getSenderId().equals(currentUser.getId());
                view.addMessage(msg, isMine);
            }
            view.scrollToBottom();
        });
    }

    public void setOtherUser(String userId) {
        this.otherUserId = userId;
        this.otherUser = dataManager.findUserById(userId);

        if (otherUser != null) {
            Platform.runLater(() -> view.setChatUsername("Chat with " + otherUser.getUsername()));
        }
        connectToServer();
    }

    public void sendMessage(String content) {
        if (content == null || content.trim().isEmpty()) return;

        if (currentUser == null || otherUserId == null) {
            view.showError("User not selected!");
            return;
        }

        ChatMessage message = new ChatMessage(currentUser.getId(), otherUserId, content.trim());
        message.setStatus(ChatMessage.MessageStatus.SENT);

        boolean saved = messageRepository.save(message);
        if (!saved) {
            view.showError("Failed to save message!");
            return;
        }

        Platform.runLater(() -> {
            view.addMessage(message, true);
            view.clearMessageInput();
            view.scrollToBottom();
        });

        if (isConnected && networkManager.isConnected()) {
            networkManager.sendMessage(otherUserId, content.trim());
        } else {
            view.showError("Server not connected. Message will be sent later.");
        }
    }

    private void handleNewMessage(NetworkPacket packet) {
        try {
            switch (packet.getType()) {
                case SEND_MESSAGE:
                    handleSendMessage(packet);
                    break;
                case TYPING_START:
                    handleTypingStart(packet);
                    break;
                case TYPING_STOP:
                    handleTypingStop(packet);
                    break;
                case NOTIFICATION:
                    handleNotification(packet);
                    break;
                default:
                    System.out.println("Received packet: " + packet.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSendMessage(NetworkPacket packet) {
        try {
            Object data = packet.getData();
            if (!(data instanceof ChatMessage)) return;

            ChatMessage message = (ChatMessage) data;

            if (message.getReceiverId().equals(currentUser.getId())) {
                if (!messageRepository.exists(message.getId())) {
                    messageRepository.save(message);
                }

                Platform.runLater(() -> {
                    view.addMessage(message, false);
                    view.scrollToBottom();
                });

                if (message.getSenderId().equals(otherUserId)) {
                    messageRepository.markAsRead(message.getId());
                }
            } else if (message.getSenderId().equals(currentUser.getId())) {
                Platform.runLater(() -> view.updateMessageStatus(message.getId(), message.getStatus()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleNotification(NetworkPacket packet) {
        try {
            Object data = packet.getData();
            if (data instanceof Notification notification) {
                dataManager.saveNotification(notification);

                Platform.runLater(() -> {
                    view.showNotification(notification.getContent());
                });

                System.out.println("🛎 New notification received: " + notification.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (networkManager.isConnected()) {
            networkManager.disconnect();
        }
        isConnected = false;
    }

    private void handleTypingStart(NetworkPacket packet) {
        String senderId = packet.getSenderId();
        Platform.runLater(() -> {
            view.showTypingIndicator(senderId, true);
        });
    }

    private void handleTypingStop(NetworkPacket packet) {
        Platform.runLater(() -> {
            view.showTypingIndicator(null, false);
        });
    }

    public void sendTypingStart() {
        if (isConnected && networkManager.isConnected() && otherUserId != null) {
            networkManager.sendTypingStart(otherUserId);
        }
    }

    public void sendTypingStop() {
        if (isConnected && networkManager.isConnected() && otherUserId != null) {
            networkManager.sendTypingStop(otherUserId);
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public Account getOtherUser() {
        return otherUser;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }
}