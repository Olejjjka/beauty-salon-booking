package com.example.beauty_salon_booking.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
public class JwtTokenProvider {

    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final long JWT_EXPIRATION = 86400000; // 1 день

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    // Генерация токена
    public String generateToken(Authentication authentication) {
        // Получаем пользователя
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // Получаем роль из объекта UserPrincipal
        String role = userPrincipal.getRole().name();  // "CLIENT" или "MASTER"

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername()) // Имя пользователя
                .claim("role", role)  // Добавляем роль в токен
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(key)
                .compact();
    }

    // Валидация токена
    public boolean validateToken(String token) {
        try {
            Jwts.parser() // Используем старый метод parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token); // Парсим JWS токен
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException |
                 UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Получить аутентификацию по токену
    public Authentication getAuthentication(String token) {
        // Загружаем UserPrincipal по логину
        UserPrincipal userPrincipal = (UserPrincipal) myUserDetailsService.loadUserByUsername(getLoginFromToken(token));

        // Извлекаем роль из токена
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody(); // Получаем тело токена

        // Получаем роль из claim и создаем authorities
        String role = claims.get("role", String.class);
        Collection<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        return new UsernamePasswordAuthenticationToken(userPrincipal, "", authorities);
    }

    // Извлечь логин из токена
    public String getLoginFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody(); // Получаем тело токена
        return claims.getSubject(); // Извлекаем логин (subject) из токена
    }
}
