package com.example.beauty_salon_booking.repositories;

import com.example.beauty_salon_booking.entities.Master;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterRepository extends JpaRepository<Master, Long> {
    Optional<Master> findByName(String name);

    Optional<Master> findByPhone(String phone);

    Optional<Master> findByLogin(String login);
}