package com.example.beauty_salon_booking.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String JWT_SECRET = "your_secret_key"; // желательно вынести в application.properties
    private final long JWT_EXPIRATION = 86400000; // 1 день

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    // Генерация токена
    public String generateToken(Authentication authentication) {
        String username = ((UserPrincipal) authentication.getPrincipal()).getUsername();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    // Валидация токена
    public boolean validateToken(String token) {
        try {
            Jwts.parser() // Используем старый метод parser()
                    .setSigningKey(JWT_SECRET) // Устанавливаем ключ для проверки подписи
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
        UserPrincipal userPrincipal = (UserPrincipal) myUserDetailsService.loadUserByUsername(getLoginFromToken(token));
        return new UsernamePasswordAuthenticationToken(userPrincipal, "", userPrincipal.getAuthorities());
    }

    // Извлечь логин из токена
    public String getLoginFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody(); // Получаем тело токена
        return claims.getSubject(); // Извлекаем логин (subject) из токена
    }
}
