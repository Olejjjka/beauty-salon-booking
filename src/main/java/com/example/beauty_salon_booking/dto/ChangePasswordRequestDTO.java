package com.example.beauty_salon_booking.dto;

import jakarta.validation.constraints.NotBlank;

public class ChangePasswordRequestDTO {

    @NotBlank(message = "Старый пароль обязателен для заполнения")
    private String oldPassword;
    @NotBlank(message = "Новый пароль обязателен для заполнения")
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}