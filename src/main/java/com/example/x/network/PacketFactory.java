package com.example.x.network;

import java.time.LocalDateTime;

public class PacketFactory {

    public static NetworkPacket createMessage(String senderId, String receiverId, String content) {
        return new NetworkPacket(
                NetworkPacket.RequestType.SEND_MESSAGE,
                content,
                senderId,
                receiverId
        );
    }

    public static NetworkPacket createTypingIndicator(String senderId, String receiverId) {
        return new NetworkPacket(
                NetworkPacket.RequestType.TYPING_INDICATOR,
                null,
                senderId,
                receiverId
        );
    }

    public static NetworkPacket createLoginRequest(String username, String password) {
        return new NetworkPacket(
                NetworkPacket.RequestType.LOGIN,
                new String[]{username, password}
        );
    }

    public static NetworkPacket createGetHistory(String userId) {
        return new NetworkPacket(
                NetworkPacket.RequestType.GET_HISTORY,
                userId,
                userId,
                null
        );
    }

    public static NetworkPacket createError(String message) {
        NetworkPacket packet = new NetworkPacket();
        packet.setType(NetworkPacket.RequestType.ERROR);
        packet.setData(message);
        return packet;
    }

    public static NetworkPacket createPing() {
        return new NetworkPacket(
                NetworkPacket.RequestType.PING,
                System.currentTimeMillis()
        );
    }

    public static NetworkPacket createDisconnect(String userId) {
        return new NetworkPacket(
                NetworkPacket.RequestType.DISCONNECT,
                userId,
                userId,
                null
        );
    }
}