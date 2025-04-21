package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.entities.RevokedToken;
import com.example.beauty_salon_booking.repositories.RevokedTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RevokedTokenService {

    private final RevokedTokenRepository revokedTokenRepository;

    public RevokedTokenService(RevokedTokenRepository revokedTokenRepository) {
        this.revokedTokenRepository = revokedTokenRepository;
    }

    public void revokeToken(String token) {
        if (!revokedTokenRepository.existsByToken(token)) {
            RevokedToken revokedToken = new RevokedToken(token, LocalDateTime.now());
            revokedTokenRepository.save(revokedToken);
        }
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokenRepository.existsByToken(token);
    }

    public void clearExpiredTokens() {
        // опционально: можно добавить проверку на срок жизни и удалять просроченные
    }
}
