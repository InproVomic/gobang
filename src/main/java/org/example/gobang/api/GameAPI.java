package org.example.gobang.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.gobang.config.Room;
import org.example.gobang.config.RoomManager;
import org.example.gobang.mapper.UserMapper;
import org.example.gobang.model.GameReadyResponse;
import org.example.gobang.model.GameResponse;
import org.example.gobang.model.OnlineUserManager;
import org.example.gobang.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Slf4j
@Component
public class GameAPI extends TextWebSocketHandler {
    @Autowired
    private OnlineUserManager onlineUserManager;

    @Autowired
    private RoomManager roomManager;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        User user = (User) session.getAttributes().get("user");
        if (user == null) {
            return;
        }
        WebSocketSession existSession = onlineUserManager.getFromGameRoom(user.getUserId());
        if(existSession==session){
            //加上这个判断是为了避免多开情况下把第一个正常开启的客户端给误关了。
            onlineUserManager.exitGameRoom(user.getUserId());
        }

        log.info("当前用户"+user.getUsername()+"离开了房间");

        noticeThatUserWin(user);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        User user = (User) session.getAttributes().get("user");
        if (user == null) {
            log.info("用户信息为空！");
            return;
        }
        Room room = roomManager.getRoomFromUserId(user.getUserId());
        room.putChess(message.getPayload());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        User user = (User) session.getAttributes().get("user");
        if (user == null) {
            return;
        }
        WebSocketSession existSession = onlineUserManager.getFromGameRoom(user.getUserId());
        if(existSession==session){
            //加上这个判断是为了避免多开情况下把第一个正常开启的客户端给误关了。
            onlineUserManager.exitGameRoom(user.getUserId());
        }

        log.info("当前用户"+user.getUsername()+"离开了房间");

        noticeThatUserWin(user);
    }

    private void noticeThatUserWin(User user) throws IOException {
        //根据user找到玩家所在的房间
        Room room = roomManager.getRoomFromUserId(user.getUserId());
        if (room==null){
            log.info("房间已被释放无序通知对手");
            return;
        }
        //找到对手
        User thatUser = (user.equals(room.getUser1()))?room.getUser1():room.getUser2();
        WebSocketSession webSocketSession = onlineUserManager.getFromGameRoom(thatUser.getUserId());
        if(webSocketSession==null){
            log.info("对手已经退出！");
            return;
        }
        //构造响应告诉对手是获胜方
        GameResponse resp = new GameResponse();
        resp.setWinner(thatUser.getUserId());
        resp.setUserId(thatUser.getUserId());
        resp.setMessage("putChess");
        webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(resp)));

        //更新玩家分数
        userMapper.userWin(thatUser.getUserId());
        userMapper.userLose(user.getUserId());

        //释放房间对象
        roomManager.remove(room.getRoomId(),room.getUser1().getUserId(),room.getUser2().getUserId());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        GameReadyResponse response = new GameReadyResponse();
        User user = (User) session.getAttributes().get("user");
        //判断用户是否登录
        if(user == null) {
            response.setOk(false);
            response.setReason("用户尚未登录！");
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            return;
        }

        Room room = roomManager.getRoomFromUserId(user.getUserId());
        if(room == null) {
            response.setOk(false);
            response.setReason("创建房间失败！");
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            return;
        }

        //判断当前用户是否多开
        if(onlineUserManager.getFromGameRoom(user.getUserId()) != null ||
            onlineUserManager.getFromGameHall(user.getUserId()) != null){
            response.setOk(false);
            response.setMessage("repeatConnection");
            response.setReason("禁止多开！");
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            return;
        }

        //设置当前玩家上线
        onlineUserManager.enterGameRoom(user.getUserId(),session);

        //将玩家设置进房间
        synchronized (room){
            if (room.getUser1() == null){
                room.setUser1(user);
                room.setWhiteUser(user.getUserId());
                log.info("先手："+user.getUserId());
                log.info("user1设置完毕！");
                return;
            }

            if(room.getUser2() == null){
                room.setUser2(user);
                log.info("user2设置完毕！");
                log.info("先手："+room.getWhiteUser());
                noticeGameReady(room,room.getUser1(),room.getUser2());
                noticeGameReady(room,room.getUser2(),room.getUser1());
                return;
            }
        }

        response.setReason("房间已满！");
        response.setOk(false);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    private void noticeGameReady(Room room, User thisUser,User thatUser) throws IOException {
        GameReadyResponse response = new GameReadyResponse();
        response.setThisUserId(thisUser.getUserId());
        response.setThatUserId(thatUser.getUserId());
        response.setMessage("gameReady");
        response.setOk(true);
        response.setWhiteUser(room.getWhiteUser());
        response.setReason("");
        response.setRoomId(room.getRoomId());

        WebSocketSession session = onlineUserManager.getFromGameRoom(thisUser.getUserId());
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }
}
