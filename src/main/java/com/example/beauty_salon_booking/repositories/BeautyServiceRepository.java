package com.example.beauty_salon_booking.repositories;

import com.example.beauty_salon_booking.entities.BeautyService;
import com.example.beauty_salon_booking.entities.Master;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface BeautyServiceRepository extends JpaRepository<BeautyService, Long> {

    Optional<BeautyService> findByName(String name);

    List<BeautyService> findByPriceBetween(double minPrice, double maxPrice);
}