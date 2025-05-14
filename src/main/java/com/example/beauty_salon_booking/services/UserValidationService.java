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
            throw new ValidationException("Поле " + fieldName + " не должно быть пустым или содержать только пробелы");
        }
    }

    public void validateLogin(String login) {
        validateRequired(login, "Login");

        boolean loginTaken = clientRepository.findByLogin(login).isPresent()
                || masterRepository.findByLogin(login).isPresent();

        if (loginTaken) {
            throw new ValidationException("Пользователь с таким логином уже зарегистрирован");
        }
    }

    public void validatePhone(String phone) {
        if (!phone.matches("^\\+7\\d{10}$")) {
            throw new ValidationException("Номер телефона должен быть в формате: +7(XXX)XXX-XX-XX");
        }

        boolean phoneTaken = clientRepository.findByPhone(phone).isPresent()
                || masterRepository.findByPhone(phone).isPresent();

        if (phoneTaken) {
            throw new ValidationException("Пользователь с таким телефоном уже зарегистрирован");
        }
    }

    public void validatePassword(String password) {
        validateRequired(password, "Password");

        if (password.length() < 6) {
            throw new ValidationException("Длина пароля должна составлять не менее 6 символов");
        }
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