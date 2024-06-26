package org.example.gobang.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.gobang.model.MatchResponse;
import org.example.gobang.model.OnlineUserManager;
import org.example.gobang.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.LinkedList;
import java.util.Queue;

@Slf4j
@Component
public class Matcher {
    private Queue<User> normalQueue = new LinkedList<User>();
    private Queue<User> highQueue = new LinkedList<>();
    private Queue<User> veryHighQueue = new LinkedList<>();

    @Autowired
    private OnlineUserManager onlineUserManager;

    @Autowired
    private RoomManager roomManager;

    @Autowired
    private ObjectMapper objectMapper;

    public void add(User user) {
        if(user.getScore()<2000){
            synchronized (normalQueue) {
                normalQueue.offer(user);
                normalQueue.notify();
            }
            log.info("玩家"+user.getUsername()+"积分:"+user.getScore()+",进入normal队列");
        } else if (user.getScore() >= 2000 && user.getScore() < 3000) {
            synchronized (highQueue){
                highQueue.offer(user);
                highQueue.notify();
            }
            log.info("玩家"+user.getUsername()+"积分:"+user.getScore()+",进入normal队列");
        }else {
            synchronized (veryHighQueue) {
                veryHighQueue.offer(user);
                veryHighQueue.notify();
            }
            log.info("玩家"+user.getUsername()+"积分:"+user.getScore()+",进入normal队列");
        }
    }

    public void remove(User user) {
        if(user.getScore()<2000){
            synchronized (normalQueue) {
                normalQueue.remove(user);
            }
        } else if (user.getScore() >= 200 && user.getScore() < 3000) {
            synchronized (highQueue){
                highQueue.remove(user);
            }
        }else{
            synchronized (veryHighQueue) {
                veryHighQueue.remove(user);
            }
        }
    }

    Matcher(){
        Thread t1 = new Thread(){
            @Override
            public void run() {
                while(true){
                    handlerMatch(normalQueue);
                }
            }
        };
        t1.start();

        Thread t2 = new Thread(){
            @Override
            public void run() {
                while(true){
                    handlerMatch(highQueue);
                }
            }
        };
        t2.start();

        Thread t3 = new Thread(){
            @Override
            public void run() {
                handlerMatch(veryHighQueue);
            }
        };
        t3.start();
    }

    public void handlerMatch(Queue<User> matchQueue){
        synchronized (matchQueue){
            try {
                //1. 先检测元素数量是否足够
                log.info("当前matchQueue.size():"+matchQueue.size());
                while (matchQueue.size() < 2){
                    matchQueue.wait();
                }

                log.info("当前matchQueue.size():"+matchQueue.size());
                //2. 取出队列中的元素
                User user1 = matchQueue.poll();
                User user2 = matchQueue.poll();

                //3. 得到两个玩家的会话
                WebSocketSession webSocketSession1 = onlineUserManager.getFromGameHall(user1.getUserId());
                WebSocketSession webSocketSession2 = onlineUserManager.getFromGameHall(user2.getUserId());

                //4. 判断异常情况
                if(webSocketSession1 == null){
                    matchQueue.add(user2);
                    return;
                }

                if(webSocketSession2 == null){
                    matchQueue.add(user1);
                    return;
                }

                if(webSocketSession1 == webSocketSession2){
                    matchQueue.add(user1);
                    return;
                }

                //4. 把玩家放到同一个房间对局中
                Room room = new Room();
                roomManager.add(room, user1, user2);

                //6. 发送信息给前端
                MatchResponse response1 = new MatchResponse();
                response1.setOk(true);
                response1.setMessage("matchSuccess");
                String json1 = objectMapper.writeValueAsString(response1);
                webSocketSession1.sendMessage(new TextMessage(json1));

                MatchResponse response2 = new MatchResponse();
                response2.setOk(true);
                response2.setMessage("matchSuccess");
                String json2 = objectMapper.writeValueAsString(response2);
                webSocketSession2.sendMessage(new TextMessage(json2));

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
