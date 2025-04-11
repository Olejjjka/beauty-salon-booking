package com.example.beauty_salon_booking.dto;

import com.example.beauty_salon_booking.enums.Role;

public record RegisterRequest(String login, String password, String name, String phone, Role role) {}