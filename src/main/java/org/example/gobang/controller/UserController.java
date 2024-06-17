package org.example.gobang.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.bcel.Const;
import org.example.gobang.constants.Constants;
import org.example.gobang.model.Result;
import org.example.gobang.model.User;
import org.example.gobang.service.UserService;
import org.example.gobang.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/login")
    public Result login(String username, String password, HttpServletResponse response) {
        User user = new User();
        boolean result = userService.login(username, password,user);
        if(result){
            Map<String,Object> map = new HashMap<>();
            map.put(Constants.USER_CLAIM_ID,user.getUserId());
            map.put(Constants.USER_CLAIM_NAME,user.getUsername());
            Cookie cookie = new Cookie(Constants.USER_TOKEN, JwtUtils.genToken(map));
            cookie.setHttpOnly(true); //防止客户端脚本（如JavaScript）访问Cookie。这有助于防止跨站脚本（XSS）攻击
            //cookie.setSecure(true); 如果设置了这个就表示只能用https协议访问！
            cookie.setPath("/");//表示对网站的所有路径生效
            cookie.setMaxAge(60 * 60 * 24);//设置过期时间为一天
            response.addCookie(cookie);
            return Result.success("");
        }
        return Result.error("密码或用户名错误！");
    }

    @RequestMapping("/register")
    public boolean register(String username, String password) {
        return userService.register(username, password);
    }
}
