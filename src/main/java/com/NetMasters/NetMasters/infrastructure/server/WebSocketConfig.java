package com.NetMasters.NetMasters.infrastructure.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.lang.NonNull;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final com.NetMasters.NetMasters.infrastructure.persistence.config.JwtUtil jwtUtil;

    public WebSocketConfig(com.NetMasters.NetMasters.infrastructure.persistence.config.JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue", "/user");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
        .addInterceptors(new JwtHandshakeInterceptor(jwtUtil))
        .withSockJS();
        System.out.println("WebSocket endpoint registrado en: /ws");
    }
}
