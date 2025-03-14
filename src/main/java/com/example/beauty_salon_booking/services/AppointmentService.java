package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.entities.Appointment;
import com.example.beauty_salon_booking.enums.AppointmentStatus;
import com.example.beauty_salon_booking.repositories.AppointmentRepository;
import com.example.beauty_salon_booking.repositories.ClientRepository;
import com.example.beauty_salon_booking.repositories.MasterRepository;
import com.example.beauty_salon_booking.repositories.BeautyServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;
    private final MasterRepository masterRepository;
    private final BeautyServiceRepository beautyServiceRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            ClientRepository clientRepository,
            MasterRepository masterRepository,
            BeautyServiceRepository beautyServiceRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.clientRepository = clientRepository;
        this.masterRepository = masterRepository;
        this.beautyServiceRepository = beautyServiceRepository;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> getAppointmentsByClientId(Long clientId) {
        return appointmentRepository.findByClientId(clientId);
    }

    public List<Appointment> getAppointmentsByMasterId(Long masterId) {
        return appointmentRepository.findByMasterId(masterId);
    }

    public List<Appointment> getAppointmentsByBeautyServiceId(Long serviceId) {
        return appointmentRepository.findByBeautyServiceId(serviceId);
    }

    public List<Appointment> getAppointmentsByDateAndTime(LocalDate date, LocalTime time) {
        return appointmentRepository.findByDateAndTime(date, time);
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status);
    }

    @Transactional
    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    @Transactional
    public Optional<Appointment> replaceAppointment(Long id, Appointment newAppointment) {
        return appointmentRepository.findById(id).map(existingAppointment -> {
            existingAppointment.setClient(newAppointment.getClient());
            existingAppointment.setMaster(newAppointment.getMaster());
            existingAppointment.setBeautyService(newAppointment.getBeautyService());
            existingAppointment.setDate(newAppointment.getDate());
            existingAppointment.setTime(newAppointment.getTime());
            existingAppointment.setStatus(newAppointment.getStatus());
            return appointmentRepository.save(existingAppointment);
        });
    }

    @Transactional
    public Optional<Appointment> updateAppointment(Long id, Map<String, Object> updates) {
        return appointmentRepository.findById(id).map(existingAppointment -> {
            if (updates.containsKey("clientId")) {
                Long clientId = ((Number) updates.get("clientId")).longValue();
                clientRepository.findById(clientId).ifPresent(existingAppointment::setClient);
            }
            if (updates.containsKey("masterId")) {
                Long masterId = ((Number) updates.get("masterId")).longValue();
                masterRepository.findById(masterId).ifPresent(existingAppointment::setMaster);
            }
            if (updates.containsKey("beautyServiceId")) {
                Long serviceId = ((Number) updates.get("beautyServiceId")).longValue();
                beautyServiceRepository.findById(serviceId).ifPresent(existingAppointment::setBeautyService);
            }
            if (updates.containsKey("date")) {
                existingAppointment.setDate(LocalDate.parse((String) updates.get("date")));
            }
            if (updates.containsKey("time")) {
                existingAppointment.setTime(LocalTime.parse((String) updates.get("time")));
            }
            if (updates.containsKey("status")) {
                existingAppointment.setStatus(AppointmentStatus.valueOf((String) updates.get("status")));
            }
            return appointmentRepository.save(existingAppointment);
        });
    }
}