package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.entities.Client;
import com.example.beauty_salon_booking.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {

        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    public Optional<Client> getClientByPhone(String phone) {
        return clientRepository.findByPhone(phone);
    }

    public Optional<Client> getClientByLogin(String login) {
        return clientRepository.findByLogin(login);
    }

    ///////////////////
    public Client saveClient(Client client) {

        client.setPassword(passwordEncoder.encode(client.getPassword()));
        return clientRepository.save(client);
    }
    //////////////////

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    public Optional<Client> replaceClient(Long id, Client newClient) {
        return clientRepository.findById(id).map(existingClient -> {
            existingClient.setName(newClient.getName());
            existingClient.setPhone(newClient.getPhone());
            existingClient.setLogin(newClient.getLogin());
            existingClient.setPassword(newClient.getPassword());
            return clientRepository.save(existingClient);
        });
    }

    public Optional<Client> updateClient(Long id, Map<String, Object> updates) {
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
                existingClient.setPassword((String) updates.get("password"));
            }
            return clientRepository.save(existingClient);
        });
    }
}
