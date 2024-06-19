package org.example.gobang.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.gobang.mapper.UserMapper;
import org.example.gobang.model.Result;
import org.example.gobang.model.User;
import org.example.gobang.utils.JwtUtils;
import org.example.gobang.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User getUser(String username) {
        return userMapper.selectByUsername(username);
    }
    public boolean login(String username, String password,User user) {
        String sqlPassword = user.getPassword();
        if(!SecurityUtils.verifyPassword(password,sqlPassword)){
            return false;
        }
        return true;
    }

    public boolean register(String username, String password) {
        String SecurityPassword = SecurityUtils.encryptPassword(password);
        Integer result = userMapper.insertUser(username, SecurityPassword);
        if(result < 1){
            log.error("注册失败！！！");
            return false;
        }
        return true;
    }

    public User getUserInfo(HttpServletRequest request) {
        String token = JwtUtils.getTokenFromRequest(request);
        if(token==null){
            return null;
        }
        String username = JwtUtils.getUsernameFromToken(token);
        log.info("username:{}",username);
        User user = userMapper.selectByUsername(username);
        user.setPassword("");
        return user;
    }
}
