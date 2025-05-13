package com.example.beauty_salon_booking.dto;

import com.example.beauty_salon_booking.entities.Client;
import com.example.beauty_salon_booking.entities.Master;

import jakarta.validation.constraints.NotBlank;

public class RegisterRequestDTO {

    @NotBlank(message = "Логин обязателен для заполнения")
    private String login;

    @NotBlank(message = "Пароль обязателен для заполнения")
    private String password;

    @NotBlank(message = "Имя обязательно для заполнения")
    private String name;

    @NotBlank(message = "Телефон обязателен для заполнения")
    private String phone;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Client toClient() {
        Client client = new Client();
        client.setLogin(this.login);
        client.setPassword(this.password);
        client.setName(this.name);
        client.setPhone(this.phone);
        return client;
    }

    public Master toMaster() {
        Master master = new Master();
        master.setLogin(this.login);
        master.setPassword(this.password);
        master.setName(this.name);
        master.setPhone(this.phone);
        return master;
    }

    // не обязательно, но желательно для UI-связки

    @Override
    public String toString() {
        return "RegisterRequestDTO{" +
                "login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}