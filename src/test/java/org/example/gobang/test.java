package org.example.gobang;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;


public class test {
    @Test
   public void test1(){
       SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
       String encode = Encoders.BASE64.encode(secretKey.getEncoded());
       System.out.println(encode);
   }
}
