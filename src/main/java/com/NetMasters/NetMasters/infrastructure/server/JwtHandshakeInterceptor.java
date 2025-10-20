package com.NetMasters.NetMasters.infrastructure.server;

import com.NetMasters.NetMasters.infrastructure.persistence.config.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);

    private final JwtUtil jwtUtil;

    public JwtHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) throws Exception {
        String token = null;

        // Intenta obtener el token del parámetro de query primero (para SockJS)
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            token = servletRequest.getServletRequest().getParameter("token");

            if (token != null && !token.isEmpty()) {
                logger.debug("Token obtenido del parámetro de query");
            }
        }

        // Si no está en query, intenta obtenerlo del header Authorization (fallback)
        if (token == null || token.isEmpty()) {
            var headers = request.getHeaders();
            var auth = headers.getFirst("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                token = auth.substring(7);
                logger.debug("Token obtenido del header Authorization");
            }
        }

        // Valida el token si existe
        if (token != null && !token.isEmpty()) {
            try {
                String username = jwtUtil.extractUsername(token);
                if (username != null && jwtUtil.validateToken(token, username)) {
                    // Almacena userId para acceso posterior si es necesario
                    Long userId = jwtUtil.extractUserId(token);
                    attributes.put("userId", userId);
                    attributes.put("username", username);
                    logger.info("WebSocket handshake exitoso para usuario: {} (ID: {})", username, userId);
                    return true;
                } else {
                    logger.warn("Token JWT inválido para usuario: {}", username);
                    return false;
                }
            } catch (Exception ex) {
                logger.warn("JWT inválido en websocket handshake: {}", ex.getMessage());
                return false;
            }
        }

        logger.warn("Missing or invalid Authorization header for websocket handshake");
        return false;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler, @Nullable Exception exception) {
        // no-op
    }
}