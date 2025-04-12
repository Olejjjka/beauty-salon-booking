package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.entities.Client;
import com.example.beauty_salon_booking.entities.Master;
import com.example.beauty_salon_booking.repositories.ClientRepository;
import com.example.beauty_salon_booking.repositories.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final ClientRepository clientRepository;
    private final MasterRepository masterRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(ClientRepository clientRepository, MasterRepository masterRepository, BCryptPasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.masterRepository = masterRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean changePassword(String username, String currentPassword, String newPassword) {
        // Сначала ищем клиента
        Client client = clientRepository.findByLogin(username).orElse(null);
        if (client != null && passwordEncoder.matches(currentPassword, client.getPassword())) {
            // Если пароль совпадает, обновляем
            client.setPassword(passwordEncoder.encode(newPassword));
            clientRepository.save(client);
            return true;
        }

        // Ищем мастера
        Master master = masterRepository.findByLogin(username).orElse(null);
        if (master != null && passwordEncoder.matches(currentPassword, master.getPassword())) {
            // Если пароль совпадает, обновляем
            master.setPassword(passwordEncoder.encode(newPassword));
            masterRepository.save(master);
            return true;
        }

        return false; // Если не найдено или пароль не совпал
    }
}
