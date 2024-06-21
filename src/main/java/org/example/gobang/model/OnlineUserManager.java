package org.example.gobang.model;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class OnlineUserManager {
    private ConcurrentHashMap<Integer, WebSocketSession> gameHall = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Integer, WebSocketSession> gameRoom = new ConcurrentHashMap<>();

    public void enterGameHall(int userId,WebSocketSession session) {
        gameHall.put(userId, session);
    }

    public void exitGameHall(int userId) {
        gameHall.remove(userId);
    }

    public WebSocketSession getFromGameHall(int userId) {
        return gameHall.get(userId);
    }

    public void enterGameRoom(int userId,WebSocketSession session) {
        gameRoom.put(userId, session);
    }

    public void exitGameRoom(int userId) {
        gameRoom.remove(userId);
    }

    public WebSocketSession getFromGameRoom(int userId) {
        return gameRoom.get(userId);
    }
}
