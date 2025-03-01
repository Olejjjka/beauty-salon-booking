package com.example.beauty_salon_booking.repositories;

import com.example.beauty_salon_booking.entities.Appointment;
import com.example.beauty_salon_booking.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByClientId(Long clientId);

    List<Appointment> findByMasterId(Long masterId);

    List<Appointment> findByBeautyServiceId(Long beautyServiceId);

    List<Appointment> findByDateAndTime(LocalDate date, LocalTime time);

    List<Appointment> findByStatus(AppointmentStatus status);
}
