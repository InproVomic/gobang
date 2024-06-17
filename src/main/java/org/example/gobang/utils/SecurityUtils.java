package org.example.gobang.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

import java.util.UUID;

@Slf4j
public class SecurityUtils {

    public static String encryptPassword(String password) {
        String salt = UUID.randomUUID().toString().replace("-", "");
        String securityPassword = DigestUtils.md5DigestAsHex((salt+password).getBytes());
        return salt+securityPassword;
    }

    public static boolean verifyPassword(String password, String sqlPassword) {
        if(password==null||password.isEmpty()){
            log.error("密码为空！");
            return false;
        }
        String salt = sqlPassword.substring(0,32);
        String securityPassword = DigestUtils.md5DigestAsHex((salt+password).getBytes());
        return (salt+securityPassword).equals(sqlPassword);
    }
}
