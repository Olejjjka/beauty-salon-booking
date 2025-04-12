package com.example.beauty_salon_booking.repositories;

import com.example.beauty_salon_booking.entities.Client;
import com.example.beauty_salon_booking.entities.Master;
import com.example.beauty_salon_booking.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    // Все токены клиента
    List<Token> findAllByClient(Client client);

    // Все токены мастера
    List<Token> findAllByMaster(Master master);
}
