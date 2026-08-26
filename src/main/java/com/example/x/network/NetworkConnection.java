package com.example.x.network;

import java.util.List;

public interface NetworkConnection {

    boolean connect(String host, int port);

    boolean send(NetworkPacket packet);

    NetworkPacket listen();

    void disconnect();

    List<NetworkPacket> syncHistory(int userId);

    boolean isConnected();
}