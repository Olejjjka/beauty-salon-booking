package com.example.beauty_salon_booking.repositories;

import com.example.beauty_salon_booking.entities.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String> {
    boolean existsByToken(String token);

    void deleteByRevokedAtBefore(LocalDateTime expirationTime);
}