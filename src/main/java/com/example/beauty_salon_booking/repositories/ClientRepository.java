package com.example.beauty_salon_booking.repositories;

import com.example.beauty_salon_booking.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByName(String name);

    Optional<Client> findByPhone(String phone);

    Optional<Client> findByLogin(String login);
}