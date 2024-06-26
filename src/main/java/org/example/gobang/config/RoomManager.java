package org.example.gobang.config;

import org.example.gobang.model.User;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;


@Component
public class RoomManager {
    private ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<String, Room>();

    private ConcurrentHashMap<Integer, String> userIdToRoomId = new ConcurrentHashMap<>();

    public void add(Room room, User userId1, User userId2) {
        rooms.put(room.getRoomId(),room);
        userIdToRoomId.put(userId1.getUserId(),room.getRoomId());
        userIdToRoomId.put(userId2.getUserId(),room.getRoomId());
    }

    public void remove(String roomId,Integer userId1,Integer userId2) {
        rooms.remove(roomId);
        userIdToRoomId.remove(userId1);
        userIdToRoomId.remove(userId2);
    }

    public Room getRoomFromRoomId(String roomId) {
        return rooms.get(roomId);
    }

    public Room getRoomFromUserId(Integer userId) {
        String roomId = userIdToRoomId.get(userId);
        if(roomId == null) {
            return null;
        }
        return getRoomFromRoomId(roomId);
    }
}
