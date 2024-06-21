package org.example.gobang.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomManager {
    private ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<String, Room>();

    private ConcurrentHashMap<Integer, String> userIdToRoomId = new ConcurrentHashMap<>();

    public void add(Room room, Integer userId1, Integer userId2) {
        rooms.put(room.getRoomId(),room);
        userIdToRoomId.put(userId1,room.getRoomId());
        userIdToRoomId.put(userId2,room.getRoomId());
    }

    public void remove(Integer roomId,Integer userId1,Integer userId2) {
        rooms.remove(roomId);
        userIdToRoomId.remove(userId1);
        userIdToRoomId.remove(userId2);
    }

    public Room getRoomFromRoomId(String roomId) {
        return rooms.get(roomId);
    }

    public Room getRoomFromUserId(Integer userId) {
        String roomId = userIdToRoomId.get(userId);
        if(roomId != null) {
            return null;
        }
        return getRoomFromRoomId(roomId);
    }
}
