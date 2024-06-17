package org.example.gobang.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
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
}
