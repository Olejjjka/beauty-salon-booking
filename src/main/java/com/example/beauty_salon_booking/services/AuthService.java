package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.entities.Appointment;
import com.example.beauty_salon_booking.entities.Client;
import com.example.beauty_salon_booking.entities.Master;
import com.example.beauty_salon_booking.enums.Role;
import com.example.beauty_salon_booking.repositories.AppointmentRepository;
import com.example.beauty_salon_booking.repositories.ClientRepository;
import com.example.beauty_salon_booking.repositories.MasterRepository;
import com.example.beauty_salon_booking.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final ClientRepository clientRepository;
    private final MasterRepository masterRepository;
    private final AppointmentRepository appointmentRepository;

    public AuthService(ClientRepository clientRepository,
                       MasterRepository masterRepository,
                       AppointmentRepository appointmentRepository) {
        this.clientRepository = clientRepository;
        this.masterRepository = masterRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // Получить текущего пользователя
    public UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return (UserPrincipal) authentication.getPrincipal();
        }
        throw new IllegalStateException("Current user is not authenticated.");
    }

    // Получить ID текущего пользователя
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    // Проверка роли
    public boolean isCurrentUserClient() {
        return Role.CLIENT.equals(getCurrentUser().getRole());
    }

    public boolean isCurrentUserMaster() {
        return Role.MASTER.equals(getCurrentUser().getRole());
    }

    // Проверка причастности
    private boolean isUserRelatedToAppointment(Appointment appointment) {
        Long currentUserId = getCurrentUserId();
        return (isCurrentUserClient() && appointment.getClient().getId().equals(currentUserId)) ||
               (isCurrentUserMaster() && appointment.getMaster().getId().equals(currentUserId));
    }

    private boolean isClientRelatedToAppointment(Appointment appointment) {
        Long currentUserId = getCurrentUserId();
        return (isCurrentUserClient() && appointment.getClient().getId().equals(currentUserId));
    }

    private boolean isMasterRelatedToAppointment(Appointment appointment) {
        Long currentUserId = getCurrentUserId();
        return (isCurrentUserMaster() && appointment.getMaster().getId().equals(currentUserId));
    }

    // Проверка доступа к данным клиента
    public void checkAccessToClient(Long clientId) {
        if (!isCurrentUserClient() || !getCurrentUserId().equals(clientId)) {
            throw new SecurityException("Access denied: not the authorized client.");
        }
    }

    // Проверка доступа к данным мастера
    public void checkAccessToMaster(Long masterId) {
        if (!isCurrentUserMaster() || !getCurrentUserId().equals(masterId)) {
            throw new SecurityException("Access denied: not the authorized master.");
        }
    }

    // Проверка причастности
    public void checkAccessFromUserToAppointment(Appointment appointment) {
        if (!isUserRelatedToAppointment(appointment)) {
            throw new SecurityException("Access denied: not related to this appointment.");
        }
    }

    public void checkAccessFromClientToAppointment(Appointment appointment) {
        if (!isClientRelatedToAppointment(appointment)) {
            throw new SecurityException("Access denied: not related to this appointment.");
        }
    }

    public void checkAccessFromMasterToAppointment(Appointment appointment) {
        if (!isMasterRelatedToAppointment(appointment)) {
            throw new SecurityException("Access denied: not related to this appointment.");
        }
    }



    // Универсальная проверка доступа (мастер или клиент)
    public boolean hasAccessToUser(Role role, Long userId) {
        return getCurrentUser().getRole().equals(role) && getCurrentUserId().equals(userId);
    }

    // Проверка, является ли текущий пользователь причастным к пользователю по ID
    public void checkAccessToUser(Role role, Long userId) {
        if (!hasAccessToUser(role, userId)) {
            throw new SecurityException("Access denied: not the authorized " + role.toString().toLowerCase() + ".");
        }
    }

    // Получение текущего клиента
    public Client getCurrentClient() {
        if (!isCurrentUserClient()) {
            throw new SecurityException("Current user is not a client.");
        }
        return clientRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
    }

    // Получение текущего мастера
    public Master getCurrentMaster() {
        if (!isCurrentUserMaster()) {
            throw new SecurityException("Current user is not a master.");
        }
        return masterRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new EntityNotFoundException("Master not found"));
    }

    // Получение текущей записи
    public Appointment getCurrentAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
    }
}