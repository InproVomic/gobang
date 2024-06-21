package org.example.gobang.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.gobang.model.OnlineUserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

@Component
public class MatchAPI {
    @Autowired
    private OnlineUserManager onlineUserManager;

    @Autowired
    private ObjectMapper objectMapper;

    private
}
