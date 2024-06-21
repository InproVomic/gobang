package org.example.gobang.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.gobang.config.Matcher;
import org.example.gobang.model.MatchRequest;
import org.example.gobang.model.MatchResponse;
import org.example.gobang.model.OnlineUserManager;
import org.example.gobang.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class MatchAPI extends TextWebSocketHandler {
    @Autowired
    private OnlineUserManager onlineUserManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Matcher matcher;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        User user = (User) session.getAttributes().get("user");

        String payload = message.getPayload();
        MatchRequest request = objectMapper.readValue(payload, MatchRequest.class);
        MatchResponse response = new MatchResponse();
        if("startMatch".equals(request.getMessage())){
            matcher.add(user);
            response.setOk(true);
            response.setMessage("startMatch");
        }else if("stopMatch".equals(request.getMessage())){
            matcher.remove(user);
            response.setOk(true);
            response.setMessage("stopMatch");
        }else{
            response.setOk(false);
            response.setMessage("非法匹配请求！");
        }

        String json = objectMapper.writeValueAsString(response);
        session.sendMessage(new TextMessage(json));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            User user = (User) session.getAttributes().get("user");
            if(onlineUserManager.getFromGameHall(user.getUserId())!= null
                    || onlineUserManager.getFromGameRoom(user.getUserId()) != null){
                //有重复登录的情况
                MatchResponse response = new MatchResponse();
                response.setOk(true);
                response.setReason("禁止多开！");
                response.setMessage("repeatConnect");
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
                return;
            }
            //将用户设置为上线状态
            onlineUserManager.enterGameHall(user.getUserId(),session);
            log.info("玩家:"+user.getUsername()+"进入游戏大厅");
        }catch (NullPointerException e){
            log.error("有玩家处于未登录状态");
            MatchResponse response = new MatchResponse();
            response.setOk(false);
            response.setReason("您尚未登录，不能成功匹配！");
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        try{
            User user = (User) session.getAttributes().get("user");
            WebSocketSession tmpSession = onlineUserManager.getFromGameHall(user.getUserId());
            if(tmpSession == session){//判断这个是为了避免在多开情况下，确保remove的对象是正确的
                onlineUserManager.exitGameHall(user.getUserId());
            }
            matcher.remove(user);
        }catch (NullPointerException e){
            log.error("当前用户未登录！");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        try{
            User user = (User) session.getAttributes().get("user");
            WebSocketSession tmpSession = onlineUserManager.getFromGameHall(user.getUserId());
            if(tmpSession == session){
                onlineUserManager.exitGameHall(user.getUserId());
            }
            matcher.remove(user);
        }catch (NullPointerException e){
            log.error("当前用户未登录！");
        }
    }
}
