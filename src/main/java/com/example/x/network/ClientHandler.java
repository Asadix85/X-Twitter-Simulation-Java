package com.example.x.network;

import com.example.x.model.account.Account;
import com.example.x.model.chat.ChatMessage;
import com.example.x.model.notification.Notification;
import com.example.x.model.post.Post;
import com.example.x.repository.MessageRepository;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.UUID;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ChatServer server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile boolean running = true;

    private String userId;
    private String sessionId;

    private final MessageRepository messageRepository;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
        this.messageRepository = new MessageRepository();
        this.sessionId = UUID.randomUUID().toString();

        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Failed to initialize streams: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println("Client handler started for session: " + sessionId);

        try {
            while (running && socket != null && !socket.isClosed()) {
                try {
                    NetworkPacket packet = (NetworkPacket) in.readObject();
                    if (packet != null) {
                        handlePacket(packet);
                    }
                } catch (SocketException e) {
                    System.out.println("Connection closed by client: " + e.getMessage());
                    break;
                } catch (ClassNotFoundException e) {
                    System.err.println("Unknown packet type: " + e.getMessage());
                } catch (IOException e) {
                    if (running) {
                        System.err.println("IO Error: " + e.getMessage());
                    }
                    break;
                }
            }
        } finally {
            cleanup();
        }
    }

    private void handlePacket(NetworkPacket packet) {
        System.out.println("Received: " + packet);

        switch (packet.getType()) {
            case LOGIN:
                handleLogin(packet);
                break;
            case LOGOUT:
                handleLogout();
                break;
            case SEND_MESSAGE:
                handleSendMessage(packet);
                break;
            case GET_HISTORY:
                handleGetHistory(packet);
                break;
            case GET_CONVERSATION:
                handleGetConversation(packet);
                break;
            case MARK_AS_READ:
                handleMarkAsRead(packet);
                break;
            case MARK_ALL_AS_READ:
                handleMarkAllAsRead(packet);
                break;
            case DELETE_MESSAGE:
                handleDeleteMessage(packet);
                break;
            case TYPING_INDICATOR:
                handleTypingIndicator(packet);
                break;
            case TYPING_START:
                handleTypingStart(packet);
                break;
            case TYPING_STOP:
                handleTypingStop(packet);
                break;
            case GET_ONLINE_USERS:
                handleGetOnlineUsers();
                break;
            case GET_USER_BY_ID:
                handleGetUserById(packet);
                break;
            case CREATE_POST:
                handleCreatePost(packet);
                break;
            case GET_POSTS:
                handleGetPosts();
                break;
            case LIKE_POST:
                handleLikePost(packet);
                break;
            case NOTIFICATION:
                handleNotification(packet);
                break;
            case PING:
                handlePing();
                break;
            case DISCONNECT:
                running = false;
                break;
            case ERROR:
                System.err.println("Received error: " + packet.getData());
                break;
            default:
                System.out.println("Unhandled packet type: " + packet.getType());
        }
    }

    private void handleLogin(NetworkPacket packet) {
        try {
            String[] credentials = (String[]) packet.getData();
            String username = credentials[0];
            String password = credentials[1];

            Account account = server.getDataManager().findUser(username);

            if (account == null) {
                sendError("User not found!");
                return;
            }

            if (!account.getPassword().equals(password)) {
                sendError("Invalid password!");
                return;
            }

            this.userId = account.getId();

            server.registerClient(userId, sessionId, this);

            sendPendingMessages();

            NetworkPacket response = new NetworkPacket(
                    NetworkPacket.RequestType.LOGIN,
                    account
            );
            sendPacket(response);

            System.out.println("User logged in: " + username + " (" + userId + ")");
        } catch (Exception e) {
            e.printStackTrace();
            sendError("Login failed: " + e.getMessage());
        }
    }

    private void handleLogout() {
        if (userId != null) {
            server.unregisterClient(sessionId);
            userId = null;
        }
        running = false;
    }

    private void handleSendMessage(NetworkPacket packet) {
        try {
            String content = (String) packet.getData();
            String receiverId = packet.getReceiverId();

            if (content == null || content.trim().isEmpty()) {
                sendError("Message cannot be empty!");
                return;
            }

            ChatMessage message = new ChatMessage(userId, receiverId, content);

            boolean isReceiverOnline = server.isUserOnline(receiverId);

            if (isReceiverOnline) {
                message.setStatus(ChatMessage.MessageStatus.SENT);
            } else {
                message.setStatus(ChatMessage.MessageStatus.DELIVERED);
            }

//            boolean saved = messageRepository.save(message);
//
//            if (!saved) {
//                sendError("Failed to save message!");
//                return;
//            }

            if (isReceiverOnline) {
                NetworkPacket messagePacket = new NetworkPacket(
                        NetworkPacket.RequestType.SEND_MESSAGE,
                        message,
                        userId,
                        receiverId
                );

                boolean delivered = server.sendToUser(receiverId, messagePacket);

                if (delivered) {
                    message.setStatus(ChatMessage.MessageStatus.DELIVERED);
                    messageRepository.updateStatus(message.getId(), ChatMessage.MessageStatus.DELIVERED);
                    System.out.println("Message delivered to online user: " + receiverId);
                } else {
                    System.out.println("User is online but message not delivered. Saved for later.");
                }

            } else {
                System.out.println("Message saved for offline user: " + receiverId + " (status: DELIVERED)");
            }

            NetworkPacket ack = new NetworkPacket(
                    NetworkPacket.RequestType.SEND_MESSAGE,
                    message,
                    userId,
                    userId
            );
            sendPacket(ack);

        } catch (Exception e) {
            e.printStackTrace();
            sendError("Failed to send message: " + e.getMessage());
        }
    }

    public boolean isRunning() {
        return running && socket != null && !socket.isClosed();
    }

    private void sendPendingMessages() {
        try {
            List<ChatMessage> pendingMessages = messageRepository.getUnreadMessages(userId);

            if (pendingMessages.isEmpty()) {
                System.out.println("No pending messages for " + userId);
                return;
            }

            System.out.println("Sending " + pendingMessages.size() + " pending messages to " + userId);

            int sentCount = 0;
            for (ChatMessage msg : pendingMessages) {
                if (msg.getStatus() == ChatMessage.MessageStatus.SENT) {
                    NetworkPacket packet = new NetworkPacket(
                            NetworkPacket.RequestType.SEND_MESSAGE,
                            msg,
                            msg.getSenderId(),
                            userId
                    );

                    boolean delivered = sendPacket(packet);

                    if (delivered) {
                        msg.setStatus(ChatMessage.MessageStatus.DELIVERED);
                        messageRepository.updateStatus(msg.getId(), ChatMessage.MessageStatus.DELIVERED);
                        sentCount++;
                        System.out.println("Sent pending message " + msg.getId());
                    }
                }
            }

            System.out.println("Sent " + sentCount + " pending messages to " + userId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGetHistory(NetworkPacket packet) {
        try {
            List<ChatMessage> messages = messageRepository.getMessagesForUser(userId);
            NetworkPacket response = new NetworkPacket(
                    NetworkPacket.RequestType.GET_HISTORY,
                    messages
            );
            sendPacket(response);
        } catch (Exception e) {
            e.printStackTrace();
            sendError("Failed to get history: " + e.getMessage());
        }
    }

    private void handleGetConversation(NetworkPacket packet) {
        try {
            String otherUserId = (String) packet.getData();
            List<ChatMessage> conversation = messageRepository.getConversation(userId, otherUserId);

            messageRepository.markAllAsRead(userId, otherUserId);

            NetworkPacket response = new NetworkPacket(
                    NetworkPacket.RequestType.GET_CONVERSATION,
                    conversation
            );
            sendPacket(response);
        } catch (Exception e) {
            e.printStackTrace();
            sendError("Failed to get conversation: " + e.getMessage());
        }
    }

    private void handleMarkAsRead(NetworkPacket packet) {
        try {
            int messageId = (int) packet.getData();
            messageRepository.markAsRead(messageId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMarkAllAsRead(NetworkPacket packet) {
        try {
            String otherUserId = (String) packet.getData();
            messageRepository.markAllAsRead(userId, otherUserId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteMessage(NetworkPacket packet) {
        try {
            int messageId = (int) packet.getData();
            messageRepository.deleteForEveryone(messageId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleTypingIndicator(NetworkPacket packet) {
        String receiverId = packet.getReceiverId();
        if (receiverId != null) {
            NetworkPacket typingPacket = new NetworkPacket(
                    NetworkPacket.RequestType.TYPING_INDICATOR,
                    userId,
                    userId,
                    receiverId
            );
            server.sendToUser(receiverId, typingPacket);
        }
    }

    private void handleGetOnlineUsers() {
        try {
            NetworkPacket response = new NetworkPacket(
                    NetworkPacket.RequestType.GET_ONLINE_USERS,
                    server.getOnlineUsers()
            );
            sendPacket(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGetUserById(NetworkPacket packet) {
        try {
            String targetUserId = (String) packet.getData();
            Account user = server.getDataManager().findUserById(targetUserId);
            NetworkPacket response = new NetworkPacket(
                    NetworkPacket.RequestType.GET_USER_BY_ID,
                    user
            );
            sendPacket(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCreatePost(NetworkPacket packet) {
        try {
            Post post = (Post) packet.getData();
            boolean saved = server.getDataManager().addPost(post);
            if (saved) {
                NetworkPacket response = new NetworkPacket(
                        NetworkPacket.RequestType.CREATE_POST,
                        post
                );
                sendPacket(response);
            } else {
                sendError("Failed to create post!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError("Failed to create post: " + e.getMessage());
        }
    }

    private void handleGetPosts() {
        try {
            List<Post> posts = server.getDataManager().getAllPosts();
            NetworkPacket response = new NetworkPacket(
                    NetworkPacket.RequestType.GET_POSTS,
                    posts
            );
            sendPacket(response);
        } catch (Exception e) {
            e.printStackTrace();
            sendError("Failed to get posts: " + e.getMessage());
        }
    }

    private void handleLikePost(NetworkPacket packet) {
        try {
            String postId = (String) packet.getData();
            server.getDataManager().likePost(postId, userId);

            Post post = server.getDataManager().findPostById(postId);
            if (post != null && !post.getAuthorId().equals(userId)) {

                Notification notification = new Notification(
                        post.getAuthorId(),
                        userId,
                        Notification.NotificationType.LIKE,
                        userId + " liked your post!",
                        postId
                );

                server.getDataManager().saveNotification(notification);

                NetworkPacket notifPacket = new NetworkPacket(
                        NetworkPacket.RequestType.NOTIFICATION,
                        notification,
                        userId,
                        post.getAuthorId()
                );

                server.sendToUser(post.getAuthorId(), notifPacket);
                System.out.println("🛎 Like notification sent to " + post.getAuthorId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleNotification(NetworkPacket packet) {
        Notification notification = (Notification) packet.getData();
        String targetUserId = notification.getUserId();
        server.sendToUser(targetUserId, packet);
    }

    private void handlePing() {
        sendPacket(PacketFactory.createPing());
    }

    private void sendError(String message) {
        sendPacket(PacketFactory.createError(message));
        System.err.println("Error sent: " + message);
    }

    public boolean sendPacket(NetworkPacket packet) {
        try {
            if (out != null && socket != null && !socket.isClosed() && running) {
                out.writeObject(packet);
                out.flush();
                return true;
            }
            return false;
        } catch (IOException e) {
            System.err.println("Failed to send packet: " + e.getMessage());
            if (running) {
                running = false;
                cleanup();
            }
            return false;
        }
    }

    private void cleanup() {
        if (!running) return;
        running = false;

        try {
            if (userId != null) {
                server.unregisterClient(sessionId);
            }
            if (in != null) {
                try { in.close(); } catch (IOException e) {}
            }
            if (out != null) {
                try { out.close(); } catch (IOException e) {}
            }
            if (socket != null && !socket.isClosed()) {
                try { socket.close(); } catch (IOException e) {}
            }
        } catch (Exception e) {
            e.getMessage();
        }

        System.out.println("Client cleaned up: " + sessionId);
    }

    public void stopHandler() {
        if (!running) return;
        running = false;
        System.out.println("Stopping handler for: " + sessionId);
        cleanup();
    }

    private void handleTypingStart(NetworkPacket packet) {
        String senderId = packet.getSenderId();
        String receiverId = packet.getReceiverId();

        NetworkPacket forwardPacket = new NetworkPacket(
                NetworkPacket.RequestType.TYPING_START,
                senderId,
                senderId,
                receiverId
        );
        server.sendToUser(receiverId, forwardPacket);
    }

    private void handleTypingStop(NetworkPacket packet) {
        String senderId = packet.getSenderId();
        String receiverId = packet.getReceiverId();

        NetworkPacket forwardPacket = new NetworkPacket(
                NetworkPacket.RequestType.TYPING_STOP,
                senderId,
                senderId,
                receiverId
        );
        server.sendToUser(receiverId, forwardPacket);
    }
}