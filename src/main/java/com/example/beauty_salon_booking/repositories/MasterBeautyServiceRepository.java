package com.example.beauty_salon_booking.repositories;

import com.example.beauty_salon_booking.entities.MasterBeautyService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterBeautyServiceRepository extends JpaRepository<MasterBeautyService, Long> {
    List<MasterBeautyService> findByMasterId(Long masterId);

    List<MasterBeautyService> findByBeautyServiceId(Long serviceId);

    List<MasterBeautyService> findByMasterIdAndBeautyServiceId(Long masterId, Long beautyServiceId);
}
