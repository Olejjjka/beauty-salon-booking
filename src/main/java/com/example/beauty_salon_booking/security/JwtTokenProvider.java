package com.example.beauty_salon_booking.security;

import com.example.beauty_salon_booking.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
public class JwtTokenProvider {

    byte[] SECRET = "1QxkT8o6Bz1gRGylRG3C/qhdL8FvjgIE3wJ3nSC9E2vO7aCZXtkxN03RJ1rU2kYUbA6UQ9e7T4XT8zJghPIuBA==".getBytes(); // Секретный ключ
    private final Key key = Keys.hmacShaKeyFor(SECRET);


    private final long JWT_EXPIRATION = 86400000; // 1 день

    // Генерация токена
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String role = userPrincipal.getRole().name();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())                  // login
                .claim("id", userPrincipal.getId())                    // id
                .claim("name", userPrincipal.getName())                // имя
                .claim("role", role)                                   // роль
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(key)
                .compact();
    }

    // Валидация токена
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Получить аутентификацию по токену
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long id = claims.get("id", Integer.class).longValue();
        String login = claims.getSubject();
        String name = claims.get("name", String.class);
        String roleStr = claims.get("role", String.class);
        Role role = Role.valueOf(roleStr);

        UserPrincipal userPrincipal = new UserPrincipal(id, login, null, name, role);

        Collection<SimpleGrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + roleStr));

        return new UsernamePasswordAuthenticationToken(userPrincipal, "", authorities);
    }
    
    // Получить логин из токена
    public String getLoginFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}