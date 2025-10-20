package com.NetMasters.NetMasters.infrastructure.persistence.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.springframework.lang.NonNull;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;
        Long userId = null;

        if (authorizationHeader == null) {
            log.debug("No Authorization header present");
        } else if (!authorizationHeader.startsWith("Bearer ")) {
            log.debug("Authorization header present but does not start with Bearer: {}", authorizationHeader);
        } else {
            jwt = authorizationHeader.substring(7);
            if (jwt.isBlank()) {
                log.debug("Bearer token is empty");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Empty JWT token");
                return;
            }
            // simple structural check: should contain exactly 2 periods
            long dotCount = jwt.chars().filter(ch -> ch == '.').count();
            if (dotCount != 2) {
                log.debug("JWT has invalid format (dotCount={}), token='{}'", dotCount, jwt.length() > 20 ? jwt.substring(0,20) + "..." : jwt);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token format");
                return;
            }
            try {
                username = jwtUtil.extractUsername(jwt);
                userId = jwtUtil.extractUserId(jwt);
            } catch (JwtException | IllegalArgumentException e) {
                // Token malformed or invalid
                log.debug("JWT parsing failed: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            boolean valid;
            try {
                valid = jwtUtil.validateToken(jwt, username);
            } catch (JwtException | IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }

            if (valid) {
                // Crear authentication token con userId en los detalles
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, null);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Agregar userId como atributo adicional
                request.setAttribute("userId", userId);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}