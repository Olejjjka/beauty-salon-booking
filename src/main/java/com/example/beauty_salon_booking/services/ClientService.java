package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.dto.AppointmentDTO;
import com.example.beauty_salon_booking.dto.ClientDTO;
import com.example.beauty_salon_booking.dto.DTOConverter;
import com.example.beauty_salon_booking.entities.Client;
import com.example.beauty_salon_booking.repositories.AppointmentRepository;
import com.example.beauty_salon_booking.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final DTOConverter dtoConverter;

    public ClientService(ClientRepository clientRepository,
                         AppointmentRepository appointmentRepository,
                         PasswordEncoder passwordEncoder,
                         DTOConverter dtoConverter) {
        this.clientRepository = clientRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.dtoConverter = dtoConverter;
    }

    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(dtoConverter::convertToClientDTO)
                .toList();
    }

    public Optional<ClientDTO> getClientById(Long id) {
        return clientRepository.findById(id).map(dtoConverter::convertToClientDTO);
    }

    public Optional<ClientDTO> getClientByPhone(String phone) {
        return clientRepository.findByPhone(phone).map(dtoConverter::convertToClientDTO);
    }

    public Optional<ClientDTO> getClientByLogin(String login) {
        return clientRepository.findByLogin(login).map(dtoConverter::convertToClientDTO);
    }

    public List<AppointmentDTO> getAppointmentsByClientId(Long clientId) {
        return appointmentRepository.findByClientId(clientId).stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    @Transactional
    public ClientDTO saveClient(Client client) {
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        return dtoConverter.convertToClientDTO(clientRepository.save(client));
    }

    @Transactional
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    @Transactional
    public Optional<ClientDTO> replaceClient(Long id, Client newClient) {
        return clientRepository.findById(id).map(existingClient -> {
            existingClient.setName(newClient.getName());
            existingClient.setPhone(newClient.getPhone());
            existingClient.setLogin(newClient.getLogin());
            existingClient.setPassword(newClient.getPassword());
            return dtoConverter.convertToClientDTO(clientRepository.save(existingClient));
        });
    }

    @Transactional
    public Optional<ClientDTO> updateClient(Long id, Map<String, Object> updates) {
        return clientRepository.findById(id).map(existingClient -> {
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
}