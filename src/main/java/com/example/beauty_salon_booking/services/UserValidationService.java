package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.repositories.ClientRepository;
import com.example.beauty_salon_booking.repositories.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserValidationService {

    private final ClientRepository clientRepository;
    private final MasterRepository masterRepository;

    @Autowired
    public UserValidationService(ClientRepository clientRepository, MasterRepository masterRepository) {
        this.clientRepository = clientRepository;
        this.masterRepository = masterRepository;
    }

    public void validateLoginAndPhoneUniqueness(String login, String phone) {
        // Проверка формата телефона
        if (!phone.matches("^\\+79\\d{9}$")) {
            throw new IllegalArgumentException("The phone number must be in the format: +79*********");
        }

        boolean loginTaken = clientRepository.findByLogin(login).isPresent()
                || masterRepository.findByLogin(login).isPresent();
        boolean phoneTaken = clientRepository.findByPhone(phone).isPresent()
                || masterRepository.findByPhone(phone).isPresent();

        if (loginTaken) {
            throw new IllegalArgumentException("The user with this login has already been registered");
        }
        if (phoneTaken) {
            throw new IllegalArgumentException("The user with this phone has already been registered");
        }
    }
}