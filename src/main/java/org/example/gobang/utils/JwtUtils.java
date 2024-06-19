package org.example.gobang.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.gobang.constants.Constants;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtils {
    private final static String securityString = "OfxWUGEw2yYs0tnBcmc4LUDU3TLclJTWHSikdx6ip2E=";
    private final static long EXPIRATION_TIME = 60 * 60 * 1000;
    private final static Key key = Keys.hmacShaKeyFor(securityString.getBytes());

    //生成令牌
    public static String genToken(Map<String, Object> claim){
        return Jwts.builder()
                .setClaims(claim)
                .setIssuer("cbb123") // 设置发行者
                .setAudience("happy") // 设置受众
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    //解析令牌
    public static Claims parseToken(String token){
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        Claims body = null;
        try{
            body  = jwtParser.parseClaimsJws(token).getBody();
            if(!"cbb123".equals(body.getIssuer())){
                throw new Exception();
            }
            if(!"happy".equals(body.getAudience())){
                throw new Exception();
            }
        }catch (ExpiredJwtException e){
            log.error("Token已经过期！");
            return null;
        }catch (Exception e){
            log.error("Token校验失败！");
            return null;
        }

        return body;
    }
    //校验令牌
    public static boolean checkToken(String token){
        Claims body = parseToken(token);
        if(body==null){
            return false;
        }
        return true;
    }
    //从Token中获取UserId
    public static Integer getUserIdFromToken(String token){
        Claims body = parseToken(token);
        if(body!=null){
            return (Integer) body.get(Constants.USER_CLAIM_ID);
        }
        return null;
    }
    //从Token中获取Username
    public static String getUsernameFromToken(String token){
        Claims body = parseToken(token);
        if(body!=null){
            return (String) body.get(Constants.USER_CLAIM_NAME);
        }
        return null;
    }

    public static String getTokenFromRequest(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie : cookies){
                if(Constants.USER_TOKEN.equals(cookie.getName())){
                    if(cookie.getValue()!=null && !cookie.getValue().isEmpty()){
                        return cookie.getValue();
                    }
                }
            }
        }

        return null;
    }
}
