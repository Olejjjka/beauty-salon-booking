package com.example.beauty_salon_booking.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

    @NotBlank(message = "Логин обязателен для заполнения")
    private String login;
    @NotBlank(message = "Пароль обязателен для заполнения")
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // не обязательно, но желательно для UI-связки
    @Override
    public String toString() {
        return "LoginRequestDTO{" +
                "login='" + login + '\'' +
                '}';
    }
}