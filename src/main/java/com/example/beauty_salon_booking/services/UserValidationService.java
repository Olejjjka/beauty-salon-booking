package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.exceptions.ValidationException;
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

    public void validateLoginUniqueness(String login) {
        boolean loginTaken = clientRepository.findByLogin(login).isPresent()
                || masterRepository.findByLogin(login).isPresent();

        if (loginTaken) {
            throw new ValidationException("The user with this login has already been registered");
        }
    }

    public void validatePhoneUniqueness(String phone) {
        // Проверка формата телефона
        if (!phone.matches("^\\+79\\d{9}$")) {
            throw new ValidationException("The phone number must be in the format: +79*********");
        }

        boolean phoneTaken = clientRepository.findByPhone(phone).isPresent()
                || masterRepository.findByPhone(phone).isPresent();

        if (phoneTaken) {
            throw new ValidationException("The user with this phone has already been registered");
        }
    }

    public void validateLoginAndPhoneUniqueness(String login, String phone) {
        // Проверка формата телефона
        validateLoginUniqueness(login);

        validatePhoneUniqueness(phone);
    }
}