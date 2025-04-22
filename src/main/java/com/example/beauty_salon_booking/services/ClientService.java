package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.dto.AppointmentDTO;
import com.example.beauty_salon_booking.dto.ClientDTO;
import com.example.beauty_salon_booking.dto.DTOConverter;
import com.example.beauty_salon_booking.entities.Client;
import com.example.beauty_salon_booking.enums.Role;
import com.example.beauty_salon_booking.repositories.AppointmentRepository;
import com.example.beauty_salon_booking.repositories.ClientRepository;
import com.example.beauty_salon_booking.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final DTOConverter dtoConverter;
    private final AuthService authService;

    public ClientService(ClientRepository clientRepository,
                         AppointmentRepository appointmentRepository,
                         PasswordEncoder passwordEncoder,
                         DTOConverter dtoConverter,
                         AuthService authService) {
        this.clientRepository = clientRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.dtoConverter = dtoConverter;
        this.authService = authService;
    }

    // не нужен (можно удалить)
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(dtoConverter::convertToClientDTO)
                .toList();
    }

    // для причастных клиента и мастера
    public Optional<ClientDTO> getClientById(Long clientId) {
        if (!authService.isCurrentUserMaster()) {
            authService.checkAccessToClient(clientId);
        }
        return clientRepository.findById(clientId).map(dtoConverter::convertToClientDTO);
    }

    // не нужен (можно удалить)
    public Optional<ClientDTO> getClientByPhone(String phone) {
        return clientRepository.findByPhone(phone).map(dtoConverter::convertToClientDTO);
    }

    // не нужен (можно удалить)
    public Optional<ClientDTO> getClientByLogin(String login) {
        return clientRepository.findByLogin(login).map(dtoConverter::convertToClientDTO);
    }

    // для причастного клиента
    public List<AppointmentDTO> getAppointmentsByClientId(Long clientId) {
        authService.checkAccessToClient(clientId);
        return appointmentRepository.findByClientId(clientId).stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    // без ограничений
    @Transactional
    public ClientDTO saveClient(Client client) {
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        return dtoConverter.convertToClientDTO(clientRepository.save(client));
    }

    // для причастного клиента
    @Transactional
    public void deleteClient(Long clientId) {
        authService.checkAccessToClient(clientId);
        clientRepository.deleteById(clientId);
    }

    // для причастного клиента
    @Transactional
    public Optional<ClientDTO> replaceClient(Long clientId, Client newClient) {
        authService.checkAccessToClient(clientId);
        return clientRepository.findById(clientId).map(existingClient -> {
            existingClient.setName(newClient.getName());
            existingClient.setPhone(newClient.getPhone());
            existingClient.setLogin(newClient.getLogin());
            existingClient.setPassword(passwordEncoder.encode(newClient.getPassword()));
            return dtoConverter.convertToClientDTO(clientRepository.save(existingClient));
        });
    }

    // для причастного клиента
    @Transactional
    public Optional<ClientDTO> updateClient(Long clientId, Map<String, Object> updates) {
        authService.checkAccessToClient(clientId);
        return clientRepository.findById(clientId).map(existingClient -> {
            if (updates.containsKey("name")) {
                existingClient.setName((String) updates.get("name"));
            }
            if (updates.containsKey("phone")) {
                existingClient.setPhone((String) updates.get("phone"));
            }
            if (updates.containsKey("login")) {
                existingClient.setLogin((String) updates.get("login"));
            }
            if (updates.containsKey("password")) {
                existingClient.setPassword(passwordEncoder.encode((String) updates.get("password")));
            }
            return dtoConverter.convertToClientDTO(clientRepository.save(existingClient));
        });
    }

    // Универсальный метод проверки: "а ты вообще этот клиент?"
    // Удалить
    private void checkClientAccess(Long clientId) {
        UserPrincipal user = getCurrentUser();
        if (user.getRole() != Role.CLIENT || !user.getId().equals(clientId)) {
            throw new AccessDeniedException("Доступ запрещён.");
        }
    }

    private UserPrincipal getCurrentUser() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}