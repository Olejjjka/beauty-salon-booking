package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.entities.RevokedToken;
import com.example.beauty_salon_booking.repositories.RevokedTokenRepository;
import com.example.beauty_salon_booking.security.JwtTokenProvider;
import com.example.beauty_salon_booking.exceptions.InvalidTokenException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class RevokedTokenService {

    private final RevokedTokenRepository revokedTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public RevokedTokenService(RevokedTokenRepository revokedTokenRepository, JwtTokenProvider jwtTokenProvider) {
        this.revokedTokenRepository = revokedTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void revokeToken(String token) {
        if (!revokedTokenRepository.existsByToken(token) && jwtTokenProvider.validateToken(token)) {
            RevokedToken revokedToken = new RevokedToken(token, LocalDateTime.now());
            revokedTokenRepository.save(revokedToken);
        } else {
            throw new InvalidTokenException("Токен уже отозван или недействителен");
        }
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokenRepository.existsByToken(token);
    }

    // Метод для удаления устаревших токенов
    public void clearExpiredTokens() {
        LocalDateTime expirationThreshold = LocalDateTime.now().minus(7, ChronoUnit.DAYS); // Устаревшие токены через 7 дней

        // Удаление токенов, которые были отозваны более 7 дней назад
        revokedTokenRepository.deleteByRevokedAtBefore(expirationThreshold);
    }
}
