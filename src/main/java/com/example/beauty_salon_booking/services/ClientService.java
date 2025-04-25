package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.dto.AppointmentDTO;
import com.example.beauty_salon_booking.dto.ClientDTO;
import com.example.beauty_salon_booking.dto.DTOConverter;
import com.example.beauty_salon_booking.entities.Client;
import com.example.beauty_salon_booking.repositories.AppointmentRepository;
import com.example.beauty_salon_booking.repositories.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

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
    private final RevokedTokenService revokedTokenService;
    private final UserValidationService userValidationService;

    public ClientService(ClientRepository clientRepository,
                         AppointmentRepository appointmentRepository,
                         PasswordEncoder passwordEncoder,
                         DTOConverter dtoConverter,
                         AuthService authService,
                         RevokedTokenService revokedTokenService,
                         UserValidationService userValidationService) {
        this.clientRepository = clientRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.dtoConverter = dtoConverter;
        this.authService = authService;
        this.revokedTokenService = revokedTokenService;
        this.userValidationService = userValidationService;
    }

    // без ограничений
    @Transactional
    public ClientDTO saveClient(Client client) {
        userValidationService.validateLoginAndPhoneUniqueness(client.getLogin(), client.getPhone());
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        return dtoConverter.convertToClientDTO(clientRepository.save(client));
    }

    // для причастного клиента
    public Optional<ClientDTO> getClientById(Long clientId) {
        if (!authService.isCurrentUserMaster()) {
            authService.checkAccessToClient(clientId);
        }
        return clientRepository.findById(clientId).map(dtoConverter::convertToClientDTO);
    }

    // для причастного клиента
    public List<AppointmentDTO> getAppointmentsByClientId(Long clientId) {
        authService.checkAccessToClient(clientId);
        return appointmentRepository.findByClientId(clientId).stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
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
            return dtoConverter.convertToClientDTO(clientRepository.save(existingClient));
        });
    }

    // для причастного клиента
    @Transactional
    public void changePassword(Long clientId, String oldPassword, String newPassword) {
        authService.checkAccessToClient(clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        if (!passwordEncoder.matches(oldPassword, client.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        client.setPassword(passwordEncoder.encode(newPassword));
        clientRepository.save(client);

        // Отзыв токена после смены пароля
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            revokedTokenService.revokeToken(token);
        }
    }

    // для причастного клиента
    @Transactional
    public void deleteClient(Long clientId) {
        authService.checkAccessToClient(clientId);
        clientRepository.deleteById(clientId);
    }



    // не нужен (можно удалить)
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(dtoConverter::convertToClientDTO)
                .toList();
    }

    // не нужен (можно удалить)
    public Optional<ClientDTO> getClientByPhone(String phone) {
        return clientRepository.findByPhone(phone).map(dtoConverter::convertToClientDTO);
    }

    // не нужен (можно удалить)
    public Optional<ClientDTO> getClientByLogin(String login) {
        return clientRepository.findByLogin(login).map(dtoConverter::convertToClientDTO);
    }
}