package com.example.x.network;

import com.example.x.model.database.ConnectionManager;
import com.example.x.model.database.DataManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private static final int PORT = 12345;
    private static final int THREAD_POOL_SIZE = 50;

    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private volatile boolean running = true;

    private final Map<String, ClientHandler> connectedClients = new ConcurrentHashMap<>();
    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();

    private final DataManager dataManager;

    public ChatServer() {
        this.dataManager = DataManager.getInstance();
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Chat Server started on port " + PORT);
            System.out.println("Waiting for clients...");

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected");

                    ClientHandler handler = new ClientHandler(clientSocket, this);
                    threadPool.execute(handler);

                } catch (SocketException e) {
                    if (running) {
                        System.err.println("Socket error: " + e.getMessage());
                    }
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Accept error: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        System.out.println("Shutting down server...");
        running = false;

        for (ClientHandler handler : connectedClients.values()) {
            handler.stopHandler();
        }
        connectedClients.clear();
        userSessionMap.clear();

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        threadPool.shutdown();
        System.out.println("Server stopped.");
    }

    public void registerClient(String userId, String sessionId, ClientHandler handler) {
        String oldSession = userSessionMap.entrySet().stream()
                .filter(e -> e.getValue().equals(userId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (oldSession != null) {
            System.out.println("User " + userId + " already connected.");

            ClientHandler oldHandler = connectedClients.get(oldSession);
            if (oldHandler != null) {
                oldHandler.stopHandler();
            }
            connectedClients.remove(oldSession);
            userSessionMap.remove(oldSession);
        }

        userSessionMap.put(sessionId, userId);
        connectedClients.put(sessionId, handler);

        dataManager.updateOnlineStatus(userId, true);

        System.out.println("User registered: " + userId + " (Session: " + sessionId + ")");
        System.out.println("Online users: " + connectedClients.size());

        broadcastUserStatus(userId, true);
    }

    public void unregisterClient(String sessionId) {
        String userId = userSessionMap.remove(sessionId);
        connectedClients.remove(sessionId);

        if (userId != null) {
            dataManager.updateOnlineStatus(userId, false);
            System.out.println("User disconnected: " + userId);
            System.out.println("Online users: " + connectedClients.size());

            broadcastUserStatus(userId, false);
        }
    }

    public boolean sendToUser(String receiverId, NetworkPacket packet) {
        String sessionId = userSessionMap.entrySet().stream()
                .filter(e -> e.getValue().equals(receiverId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (sessionId == null) {
            System.out.println("User " + receiverId + " is offline.");
            return false;
        }

        ClientHandler handler = connectedClients.get(sessionId);
        if (handler == null) {
            System.out.println("Handler not found for user: " + receiverId);
            return false;
        }

        if (handler.isRunning()) {
            return handler.sendPacket(packet);
        } else {
            System.out.println("Handler is not running for user: " + receiverId);
            return false;
        }
    }

    public void broadcastToAllExcept(String excludeUserId, NetworkPacket packet) {
        for (Map.Entry<String, ClientHandler> entry : connectedClients.entrySet()) {
            String userId = userSessionMap.get(entry.getKey());
            if (!userId.equals(excludeUserId)) {
                entry.getValue().sendPacket(packet);
            }
        }
    }

    private void broadcastUserStatus(String userId, boolean isOnline) {
        NetworkPacket statusPacket = new NetworkPacket(
                NetworkPacket.RequestType.UPDATE_USER_STATUS,
                isOnline,
                userId,
                null
        );
        broadcastToAllExcept(userId, statusPacket);
    }

    public Map<String, String> getOnlineUsers() {
        return new ConcurrentHashMap<>(userSessionMap);
    }

    public boolean isUserOnline(String userId) {
        return userSessionMap.containsValue(userId);
    }

    public int getOnlineCount() {
        return connectedClients.size();
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public boolean isRunning() {
        return running;
    }

    public static void main(String[] args) {
        try {
            ConnectionManager.getConnection();
            System.out.println("Database connected.");
        } catch (Exception e) {
            System.err.println("Failed to connect to database!");
            e.printStackTrace();
            return;
        }

        ChatServer server = new ChatServer();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down gracefully...");
            server.stop();
        }));

        server.start();
    }
}