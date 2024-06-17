package org.example.gobang.LoginInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.gobang.constants.Constants;
import org.example.gobang.utils.JwtUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            response.setStatus(302);
            response.getWriter().write("cookies为空！");
            response.sendRedirect("/login.html");
            log.error("cookies为空！");
            return false;
        }

        String token = null;
        for (Cookie cookie : cookies) {
            if(Constants.USER_TOKEN.equals(cookie.getName())){
                token = cookie.getValue();
                break;
            }
        }

        if(token == null || token.isEmpty()){
            response.setStatus(302);
            response.getWriter().write("token为空！");
            response.sendRedirect("/login.html");
            log.error("token为空！");
            return false;
        }

        if(!JwtUtils.checkToken(token)){
            response.setStatus(302);
            response.getWriter().write("token校验失败！");
            response.sendRedirect("/login.html");
            return false;
        }

        return true;
    }
}
