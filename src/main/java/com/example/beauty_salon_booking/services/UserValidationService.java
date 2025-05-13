package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.exceptions.ValidationException;
import com.example.beauty_salon_booking.repositories.ClientRepository;
import com.example.beauty_salon_booking.repositories.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserValidationService {

    private final ClientRepository clientRepository;
    private final MasterRepository masterRepository;

    @Autowired
    public UserValidationService(ClientRepository clientRepository, MasterRepository masterRepository) {
        this.clientRepository = clientRepository;
        this.masterRepository = masterRepository;
    }

    private void validateRequired(String field, String fieldName) {
        if (!StringUtils.hasText(field)) {
            throw new ValidationException(fieldName + " must not be empty or just whitespace");
        }
    }

    public void validateLogin(String login) {
        validateRequired(login, "Login");

        boolean loginTaken = clientRepository.findByLogin(login).isPresent()
                || masterRepository.findByLogin(login).isPresent();

        if (loginTaken) {
            throw new ValidationException("The user with this login has already been registered");
        }
    }

    public void validatePhone(String phone) {
        if (!phone.matches("^\\+79\\d{9}$")) {
            throw new ValidationException("The phone number must be in the format: +79*********");
        }

        boolean phoneTaken = clientRepository.findByPhone(phone).isPresent()
                || masterRepository.findByPhone(phone).isPresent();

        if (phoneTaken) {
            throw new ValidationException("The user with this phone has already been registered");
        }
    }

    public void validatePassword(String password) {
        validateRequired(password, "Password");
    }

    public void validateName(String name) {
        validateRequired(name, "Name");
    }

    public void validateLoginAndPhone(String login, String phone) {
        validateLogin(login);
        validatePhone(phone);
    }

    public void validateAll(String login, String password, String name, String phone) {
        validateLogin(login);
        validatePassword(password);
        validateName(name);
        validatePhone(phone);
    }
}