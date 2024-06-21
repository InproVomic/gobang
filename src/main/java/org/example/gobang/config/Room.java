package org.example.gobang.config;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Room {
    private String roomId;
    private Integer user1;
    private Integer user2;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Integer getUser1() {
        return user1;
    }

    public void setUser1(Integer user1) {
        this.user1 = user1;
    }

    public Integer getUser2() {
        return user2;
    }

    public void setUser2(Integer user2) {
        this.user2 = user2;
    }

    Room(){
        roomId = UUID.randomUUID().toString();
    }
}

