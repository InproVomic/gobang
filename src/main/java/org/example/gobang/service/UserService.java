package org.example.gobang.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.gobang.mapper.UserMapper;
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

    public boolean login(String username, String password,User user) {
        user = userMapper.selectPassword(username);
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
}
