package com.example.beauty_salon_booking.security;

import com.example.beauty_salon_booking.dto.ResponseDTO;
import com.example.beauty_salon_booking.services.RevokedTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RevokedTokenService revokedTokenService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, RevokedTokenService revokedTokenService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.revokedTokenService = revokedTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Пропускаем проверку для эндпоинтов авторизации
        if (request.getRequestURI().equals("/") ||
            request.getRequestURI().startsWith("/api/auth") ||
            request.getRequestURI().startsWith("/register") ||
            request.getRequestURI().startsWith("/login") ||
            request.getRequestURI().startsWith("/css/") ||
            request.getRequestURI().startsWith("/images/") ||
            request.getRequestURI().startsWith("/favicon.ico"))  {

            filterChain.doFilter(request, response);
            return;
        }

        String token = getTokenFromRequest(request);

        // Если токен отсутствует, возвращаем 401
        if (token == null) {
            sendErrorResponse(response, "Unauthorized", "Authorization token is missing.", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Если токен есть, проверяем его валидность
        if (jwtTokenProvider.validateToken(token)) {
            if (revokedTokenService.isTokenRevoked(token)) {
                sendErrorResponse(response, "Unauthorized", "Token has been revoked.", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, String error, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Создаем ErrorResponseDTO для стандартизированного ответа
        ResponseDTO errorResponse = new ResponseDTO(
                LocalDateTime.now().toString(),
                status,
                message,
                error
        );

        // Преобразуем в JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(errorResponse);

        // Отправка ответа
        response.getWriter().write(json);
    }
}