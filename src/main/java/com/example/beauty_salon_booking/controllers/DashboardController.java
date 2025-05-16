package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.dto.AppointmentDTO;
import com.example.beauty_salon_booking.security.UserPrincipal;
import com.example.beauty_salon_booking.services.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private final AppointmentService appointmentService;

    public DashboardController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserPrincipal user)) {
            return "redirect:/login";
        }

        String name = user.getName();
        model.addAttribute("username", name);

        try {
            Long clientId = user.getId();
            List<AppointmentDTO> records = appointmentService.getAppointmentsByClientId(clientId);
            model.addAttribute("records", records);
        } catch (Exception e) {
            logger.warn("Failed to load appointments for user {}", name, e);
            model.addAttribute("records", List.of());
        }

        return "dashboard";
    }
}