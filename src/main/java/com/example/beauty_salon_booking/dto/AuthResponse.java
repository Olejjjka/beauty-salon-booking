package com.example.beauty_salon_booking.dto;

import com.example.beauty_salon_booking.enums.Role;

public record AuthResponse(String token, Role role) {}