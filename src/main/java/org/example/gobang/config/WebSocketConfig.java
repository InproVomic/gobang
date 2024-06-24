package org.example.gobang.config;

import org.example.gobang.api.GameAPI;
import org.example.gobang.api.MatchAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private MatchAPI matchAPI;

    @Autowired
    private GameAPI gameAPI;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler((WebSocketHandler) matchAPI,"/findMatch")
                .addInterceptors(new HttpSessionHandshakeInterceptor());
        registry.addHandler((WebSocketHandler) gameAPI,"/game")
                .addInterceptors(new HttpSessionHandshakeInterceptor());
    }
}
