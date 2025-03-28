package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.dto.AppointmentDTO;
import com.example.beauty_salon_booking.dto.DTOConverter;
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
    private final DTOConverter dtoConverter;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            ClientRepository clientRepository,
            MasterRepository masterRepository,
            BeautyServiceRepository beautyServiceRepository,
            DTOConverter dtoConverter) {
        this.appointmentRepository = appointmentRepository;
        this.clientRepository = clientRepository;
        this.masterRepository = masterRepository;
        this.beautyServiceRepository = beautyServiceRepository;
        this.dtoConverter = dtoConverter;
    }

    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    public Optional<AppointmentDTO> getAppointmentById(Long id) {
        return appointmentRepository.findById(id).map(dtoConverter::convertToAppointmentDTO);
    }

    public List<AppointmentDTO> getAppointmentsByClientId(Long clientId) {
        return appointmentRepository.findByClientId(clientId).stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    public List<AppointmentDTO> getAppointmentsByMasterId(Long masterId) {
        return appointmentRepository.findByMasterId(masterId).stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    public List<AppointmentDTO> getAppointmentsByBeautyServiceId(Long beautyServiceId) {
        return appointmentRepository.findByBeautyServiceId(beautyServiceId).stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    public List<AppointmentDTO> getAppointmentsByDateAndTime(LocalDate date, LocalTime time) {
        return appointmentRepository.findByDateAndTime(date, time).stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    public List<AppointmentDTO> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status).stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    @Transactional
    public AppointmentDTO createAppointment(Map<String, Object> payload) {
        Long clientId = ((Number) payload.get("clientId")).longValue();
        Long masterId = ((Number) payload.get("masterId")).longValue();
        Long beautyServiceId = ((Number) payload.get("beautyServiceId")).longValue();
        LocalDate date = LocalDate.parse((String) payload.get("date"));
        LocalTime time = LocalTime.parse((String) payload.get("time"));
        AppointmentStatus status = AppointmentStatus.valueOf((String) payload.get("status"));

        Appointment appointment = new Appointment();
        appointment.setClient(clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found")));
        appointment.setMaster(masterRepository.findById(masterId)
                .orElseThrow(() -> new IllegalArgumentException("Master not found")));
        appointment.setBeautyService(beautyServiceRepository.findById(beautyServiceId)
                .orElseThrow(() -> new IllegalArgumentException("Beauty Service not found")));
        appointment.setDate(date);
        appointment.setTime(time);
        appointment.setStatus(status);

        return dtoConverter.convertToAppointmentDTO(appointmentRepository.save(appointment));
    }


    @Transactional
    public AppointmentDTO saveAppointment(Appointment appointment) {
        return dtoConverter.convertToAppointmentDTO(appointmentRepository.save(appointment));
    }

    @Transactional
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    @Transactional
    public Optional<AppointmentDTO> replaceAppointment(Long id, Appointment newAppointment) {
        return appointmentRepository.findById(id).map(existingAppointment -> {
            existingAppointment.setClient(newAppointment.getClient());
            existingAppointment.setMaster(newAppointment.getMaster());
            existingAppointment.setBeautyService(newAppointment.getBeautyService());
            existingAppointment.setDate(newAppointment.getDate());
            existingAppointment.setTime(newAppointment.getTime());
            existingAppointment.setStatus(newAppointment.getStatus());
            return dtoConverter.convertToAppointmentDTO(appointmentRepository.save(existingAppointment));
        });
    }

    @Transactional
    public Optional<AppointmentDTO> updateAppointment(Long id, Map<String, Object> updates) {
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
            return dtoConverter.convertToAppointmentDTO(appointmentRepository.save(existingAppointment));
        });
    }
}