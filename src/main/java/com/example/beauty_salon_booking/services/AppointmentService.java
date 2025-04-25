package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.dto.AppointmentDTO;
import com.example.beauty_salon_booking.dto.ClientDTO;
import com.example.beauty_salon_booking.dto.DTOConverter;
import com.example.beauty_salon_booking.dto.MasterDTO;
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
    private final AuthService authService;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            ClientRepository clientRepository,
            MasterRepository masterRepository,
            BeautyServiceRepository beautyServiceRepository,
            DTOConverter dtoConverter,
            AuthService authService) {
        this.appointmentRepository = appointmentRepository;
        this.clientRepository = clientRepository;
        this.masterRepository = masterRepository;
        this.beautyServiceRepository = beautyServiceRepository;
        this.dtoConverter = dtoConverter;
        this.authService = authService;
    }

    // для причастных клиента и мастера, которые связаны с конкретной записью
    public Optional<AppointmentDTO> getAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .map(appointment -> {
                    authService.checkAccessFromUserToAppointment(appointment);
                    return dtoConverter.convertToAppointmentDTO(appointment);
                });
    }

    // для причастных клиента и мастера, которые связаны с конкретной записью
    public Optional<ClientDTO> getClientByAppointmentId(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .map(appointment -> {
                    authService.checkAccessFromUserToAppointment(appointment); // Проверка доступа
                    return dtoConverter.convertToClientDTO(appointment.getClient());
                });
    }

    // для причастных клиента и мастера, которые связаны с конкретной записью
    public Optional<MasterDTO> getMasterByAppointmentId(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .map(appointment -> {
                    authService.checkAccessFromUserToAppointment(appointment); // Проверка доступа
                    return dtoConverter.convertToMasterDTO(appointment.getMaster());
                });
    }

    // для причастных клиента и мастера, которые связаны с конкретной записью
    public List<AppointmentDTO> getAppointmentsByBeautyServiceId(Long beautyServiceId) {
        return appointmentRepository.findByBeautyServiceId(beautyServiceId).stream()
                .peek(authService::checkAccessFromUserToAppointment) // Проверка доступа для каждого объекта
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    // для причастных клиента и мастера, которые связаны с конкретной записью
    public List<AppointmentDTO> getAppointmentsByDateAndTime(LocalDate date, LocalTime time) {
        return appointmentRepository.findByDateAndTime(date, time).stream()
                .peek(authService::checkAccessFromUserToAppointment) // Проверка доступа для каждого объекта
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    // для причастных клиента и мастера, которые связаны с конкретной записью
    public List<AppointmentDTO> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status).stream()
                .peek(authService::checkAccessFromUserToAppointment) // Проверка доступа для каждого объекта
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    // для всех клиентов
    @Transactional
    public AppointmentDTO createAppointment(Map<String, Object> payload) {
        Long clientId = authService.getCurrentUserId();
        authService.checkAccessToClient(clientId);

        Long masterId = ((Number) payload.get("masterId")).longValue();
        Long beautyServiceId = ((Number) payload.get("beautyServiceId")).longValue();
        LocalDate date = LocalDate.parse((String) payload.get("date"));
        LocalTime time = LocalTime.parse((String) payload.get("time"));
        AppointmentStatus status = AppointmentStatus.PENDING;

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

    // для причастного мастера, который связан с конкретной записью
    @Transactional
    public Optional<AppointmentDTO> replaceAppointment(Long appointmentId, Appointment newAppointment) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        authService.checkAccessFromMasterToAppointment(appointment);

        appointment.setClient(newAppointment.getClient());
        appointment.setMaster(newAppointment.getMaster());
        appointment.setBeautyService(newAppointment.getBeautyService());
        appointment.setDate(newAppointment.getDate());
        appointment.setTime(newAppointment.getTime());
        appointment.setStatus(newAppointment.getStatus());

        return Optional.of(dtoConverter.convertToAppointmentDTO(appointmentRepository.save(appointment)));
    }

    // для причастного мастера, который связан с конкретной записью
    @Transactional
    public Optional<AppointmentDTO> updateAppointment(Long appointmentId, Map<String, Object> updates) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        authService.checkAccessFromMasterToAppointment(appointment);

        if (updates.containsKey("clientId")) {
            Long clientId = ((Number) updates.get("clientId")).longValue();
            clientRepository.findById(clientId).ifPresent(appointment::setClient);
        }
        if (updates.containsKey("masterId")) {
            Long masterId = ((Number) updates.get("masterId")).longValue();
            masterRepository.findById(masterId).ifPresent(appointment::setMaster);
        }
        if (updates.containsKey("beautyServiceId")) {
            Long serviceId = ((Number) updates.get("beautyServiceId")).longValue();
            beautyServiceRepository.findById(serviceId).ifPresent(appointment::setBeautyService);
        }
        if (updates.containsKey("date")) {
            appointment.setDate(LocalDate.parse((String) updates.get("date")));
        }
        if (updates.containsKey("time")) {
            appointment.setTime(LocalTime.parse((String) updates.get("time")));
        }
        if (updates.containsKey("status")) {
            appointment.setStatus(AppointmentStatus.valueOf((String) updates.get("status")));
        }
        return Optional.of(dtoConverter.convertToAppointmentDTO(appointmentRepository.save(appointment)));
    }

    // для причастного мастера, который связан с конкретной записью
    @Transactional
    public void deleteAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        authService.checkAccessFromMasterToAppointment(appointment);
        appointmentRepository.deleteById(appointmentId);
    }



    // не надо (для причастного клиента, который связан с конкретной записью)
    public List<AppointmentDTO> getAppointmentsByClientId(Long clientId) {
        return appointmentRepository.findByClientId(clientId).stream()
                .peek(authService::checkAccessFromClientToAppointment) // Проверка доступа для каждого объекта
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    // не надо (для причастного мастера, который связан с конкретной записью)
    public List<AppointmentDTO> getAppointmentsByMasterId(Long masterId) {
        return appointmentRepository.findByMasterId(masterId).stream()
                .peek(authService::checkAccessFromMasterToAppointment) // Проверка доступа для каждого объекта
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    // не надо
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    // не надо
    @Transactional
    public AppointmentDTO saveAppointment(Appointment appointment) {
        return dtoConverter.convertToAppointmentDTO(appointmentRepository.save(appointment));
    }
}