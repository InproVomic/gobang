package org.example.gobang.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.gobang.GobangApplication;
import org.example.gobang.mapper.UserMapper;
import org.example.gobang.model.GameRequest;
import org.example.gobang.model.GameResponse;
import org.example.gobang.model.OnlineUserManager;
import org.example.gobang.model.User;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Data
public class Room {
    private String roomId;
    private User user1;
    private User user2;
    private Integer whiteUser;
    private static final Integer MAX_ROW = 15;
    private static final Integer MAX_COL = 15;
    private int[][] board = new int[MAX_ROW][MAX_COL];

    private ObjectMapper objectMapper;
    private OnlineUserManager onlineUserManager;
    private RoomManager roomManager;
    private UserMapper userMapper;

    Room(){
        roomId = UUID.randomUUID().toString();
        objectMapper = GobangApplication.applicationContext.getBean(ObjectMapper.class);
        onlineUserManager = GobangApplication.applicationContext.getBean(OnlineUserManager.class);
        roomManager = GobangApplication.applicationContext.getBean(RoomManager.class);
        userMapper = GobangApplication.applicationContext.getBean(UserMapper.class);
    }

    public void putChess(String reqJson) throws IOException {
        GameRequest request = objectMapper.readValue(reqJson,GameRequest.class);
        GameResponse response = new GameResponse();
        //记录落子位置
        Integer chess = request.getUserId().equals(user1.getUserId()) ? 1 : 2;
        Integer row = request.getRow();
        Integer col = request.getCol();

        //检查是否符合要求
        if(board[row][col] != 0){
            log.info("当前位置已经有子了！");
            return;
        }
        board[row][col] = chess;

        //判断胜负
        Integer winner = checkWinner(row,col,chess);

        response.setMessage("putChess");
        response.setCol(col);
        response.setRow(row);
        response.setUserId(request.getUserId());
        response.setWinner(winner);

        WebSocketSession session1 = onlineUserManager.getFromGameRoom(user1.getUserId());
        WebSocketSession session2 = onlineUserManager.getFromGameRoom(user2.getUserId());

        //掉线处理
        if(session1 == null){
            //这里说明玩家1下线，就让玩家2获胜
            response.setWinner(user2.getUserId());
            log.info("user1掉线！");
        }

        if(session2 == null){
            //这里说明玩家2下线，就让玩家1获胜
            response.setWinner(user1.getUserId());
            log.info("user2掉线！");
        }

        if (session1!=null){
            session1.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        }
        if (session2!=null){
            session2.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        }

        if(response.getWinner()!=0){
            //胜负已分
            Integer winnerId = response.getWinner();
            Integer loserId = response.getWinner().equals(user1.getUserId())? user2.getUserId() : user1.getUserId();

            userMapper.userWin(winnerId);
            userMapper.userLose(loserId);

            log.info("winner:"+winnerId);

            roomManager.remove(roomId,user1.getUserId(),user2.getUserId());
        }
    }

    Integer checkWinner(Integer row,Integer col,Integer chess){
        //检查所有行
        int count = 1;
        for (int i = 1; i <= 4; i++) {
            try{
                if(board[row][col-i] == chess){
                    ++count;
                }else{
                    break;
                }
            }catch (ArrayIndexOutOfBoundsException e){
                break;
            }
        }
        for(int i = 1; i <= 4; ++i){
            try{
                if(board[row][col+i] == chess){
                    ++count;
                }else{
                    break;
                }
            }catch (ArrayIndexOutOfBoundsException e){
                break;
            }
        }
        if(count >= 5){
            return chess == 1 ? user1.getUserId() : user2.getUserId();
        }

        //检查所以列
        count = 1;
        for (int i = 1; i <= 4; i++) {
            try{
                if(board[row-i][col] == chess){
                    ++count;
                }else{
                    break;
                }
            }catch (ArrayIndexOutOfBoundsException e){
                break;
            }
        }

        for(int i = 1; i <= 4; ++i){
            try{
                if(board[row+i][col] == chess){
                    ++count;
                }else{
                    break;
                }
            }catch (ArrayIndexOutOfBoundsException e){
                break;
            }
        }
        if(count >= 5){
            return chess == 1 ? user1.getUserId() : user2.getUserId();
        }

        //检查所有左对角线
        count = 1;
        for (int i = 1; i <= 4; i++) {
            try {
                if(board[row-i][col-i] == chess){
                    ++count;
                }else{
                    break;
                }
            }catch (ArrayIndexOutOfBoundsException e){
                break;
            }
        }

        for(int i = 1; i <= 4; ++i){
            try {
                if(board[row+i][col+i] == chess){
                    ++count;
                }else{
                    break;
                }
            }catch (ArrayIndexOutOfBoundsException e){
                break;
            }
        }
        if(count >= 5){
            return chess == 1 ? user1.getUserId() : user2.getUserId();
        }

        //判断所有右对角线
        count = 1;
        for (int i = 1; i <= 4; i++) {
            try{
                if(board[row+i][col-i] == chess){
                    ++count;
                }else{
                    break;
                }
            }catch (ArrayIndexOutOfBoundsException e){
                break;
            }
        }

        for(int i = 1; i <= 4; ++i){
            try{
                if (board[row-i][col+i] == chess){
                    ++count;
                }else{
                    break;
                }
            }catch (ArrayIndexOutOfBoundsException e){
                break;
            }
        }
        if(count >= 5){
            return chess == 1 ? user1.getUserId() : user2.getUserId();
        }

        //返回0代表还没分出胜负
        return 0;
    }
}

