package com.example.x.network;

import com.example.x.network.NetworkPacket;
import com.example.x.network.PacketFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class NetworkManager implements NetworkConnection {

    private static final int RECONNECT_DELAY = 3000;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private String sessionId;
    private String userId;
    private volatile boolean connected = false;
    private volatile boolean running = false;

    private ExecutorService listenerExecutor;

    private Consumer<NetworkPacket> onMessageReceived;
    private Consumer<NetworkPacket> onPacketReceived;
    private Runnable onDisconnected;
    private Runnable onReconnecting;
    private Consumer<String> onError;

    private String serverHost;
    private int serverPort;

    public NetworkManager() {
        this.listenerExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public boolean connect(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;

        try {
            if (socket != null && !socket.isClosed()) {
                disconnect();
            }

            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            connected = true;
            running = true;

            startListening();

            System.out.println("Connected to server: " + host + ":" + port);
            return true;

        } catch (IOException e) {
            System.err.println("Failed to connect: " + e.getMessage());
            connected = false;
            running = false;
            return false;
        }
    }

    @Override
    public boolean send(NetworkPacket packet) {
        if (!connected || out == null) {
            System.err.println("Cannot send: not connected!");
            return false;
        }

        try {
            out.writeObject(packet);
            out.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Failed to send packet: " + e.getMessage());
            handleDisconnect();
            return false;
        }
    }

    @Override
    public NetworkPacket listen() {
        if (!connected || in == null) {
            return null;
        }

        try {
            return (NetworkPacket) in.readObject();
        } catch (SocketException e) {
            if (running) {
                System.err.println("Connection lost: " + e.getMessage());
                handleDisconnect();
            }
        } catch (IOException | ClassNotFoundException e) {
            if (running) {
                System.err.println("Listen error: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public void disconnect() {
        running = false;
        connected = false;

        if (listenerExecutor != null) {
            listenerExecutor.shutdownNow();
        }

        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Disconnected from server.");
    }

    @Override
    public List<NetworkPacket> syncHistory(int userId) {
        List<NetworkPacket> history = new ArrayList<>();

        if (!connected) {
            System.err.println("Cannot sync history: not connected!");
            return history;
        }

        NetworkPacket request = new NetworkPacket(
                NetworkPacket.RequestType.GET_HISTORY,
                String.valueOf(userId)
        );
        send(request);


        return history;
    }

    @Override
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }

    private void startListening() {
        listenerExecutor.submit(() -> {
            while (running && connected) {
                NetworkPacket packet = listen();
                if (packet != null) {
                    handleIncomingPacket(packet);
                }
            }
        });
    }

    private void handleIncomingPacket(NetworkPacket packet) {
        if (onPacketReceived != null) {
            onPacketReceived.accept(packet);
        }
        switch (packet.getType()) {
            case SEND_MESSAGE:
                if (onMessageReceived != null) {
                    onMessageReceived.accept(packet);
                }
                break;

            case TYPING_INDICATOR:
                System.out.println("User " + packet.getSenderId() + " is typing...");
                break;

            case GET_ONLINE_USERS:
                System.out.println("Online users updated");
                break;

            case UPDATE_USER_STATUS:
                boolean isOnline = (boolean) packet.getData();
                String userId = packet.getSenderId();
                System.out.println("User " + userId + " is " + (isOnline ? "online" : "offline"));
                break;

            case ERROR:
                String errorMsg = (String) packet.getData();
                System.err.println("Server error: " + errorMsg);
                if (onError != null) {
                    onError.accept(errorMsg);
                }
                break;

            case PING:
                send(PacketFactory.createPing());
                break;

            case DISCONNECT:
                handleDisconnect();
                break;

            default:
                System.out.println("Received packet: " + packet.getType());
        }
    }
    private void handleDisconnect() {
        if (!running) return;

        connected = false;
        System.err.println("Disconnected from server!");

        if (onDisconnected != null) {
            onDisconnected.run();
        }

        if (running) {
            reconnect();
        }
    }

    private void reconnect() {
        System.out.println("Reconnecting in " + (RECONNECT_DELAY / 1000) + " seconds...");

        if (onReconnecting != null) {
            onReconnecting.run();
        }

        try {
            Thread.sleep(RECONNECT_DELAY);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        if (running) {
            System.out.println("Attempting to reconnect...");
            connect(serverHost, serverPort);
        }
    }

    public void sendTypingStart(String receiverId) {
        NetworkPacket packet = new NetworkPacket(
                NetworkPacket.RequestType.TYPING_START,
                null,
                userId,
                receiverId
        );
        send(packet);
    }

    public void sendTypingStop(String receiverId) {
        NetworkPacket packet = new NetworkPacket(
                NetworkPacket.RequestType.TYPING_STOP,
                null,
                userId,
                receiverId
        );
        send(packet);
    }

    public void login(String username, String password) {
        NetworkPacket packet = PacketFactory.createLoginRequest(username, password);
        send(packet);
    }

    public void sendMessage(String receiverId, String content) {
        NetworkPacket packet = PacketFactory.createMessage(userId, receiverId, content);
        send(packet);
    }

    public void getConversation(String otherUserId) {
        NetworkPacket packet = new NetworkPacket(
                NetworkPacket.RequestType.GET_CONVERSATION,
                otherUserId
        );
        send(packet);
    }

    public void markAsRead(int messageId) {
        NetworkPacket packet = new NetworkPacket(
                NetworkPacket.RequestType.MARK_AS_READ,
                messageId
        );
        send(packet);
    }

    public void getOnlineUsers() {
        NetworkPacket packet = new NetworkPacket(
                NetworkPacket.RequestType.GET_ONLINE_USERS,
                null
        );
        send(packet);
    }

    public void sendTypingIndicator(String receiverId) {
        NetworkPacket packet = new NetworkPacket(
                NetworkPacket.RequestType.TYPING_INDICATOR,
                null,
                userId,
                receiverId
        );
        send(packet);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setOnMessageReceived(Consumer<NetworkPacket> listener) {
        this.onMessageReceived = listener;
    }

    public void setOnPacketReceived(Consumer<NetworkPacket> listener) {
        this.onPacketReceived = listener;
    }

    public void setOnDisconnected(Runnable listener) {
        this.onDisconnected = listener;
    }

    public void setOnReconnecting(Runnable listener) {
        this.onReconnecting = listener;
    }

    public void setOnError(Consumer<String> listener) {
        this.onError = listener;
    }
}