package org.example.gobang;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.example.gobang.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;


public class test {
    @Test
   public void test1(){
        Map<String,Object> map = new HashMap<>();
        map.put("username","admin");
        map.put("password","123456");
        String tmp = JwtUtils.genToken(map);
        Claims claims = JwtUtils.parseToken(tmp);
        System.out.println(claims);
   }
}
