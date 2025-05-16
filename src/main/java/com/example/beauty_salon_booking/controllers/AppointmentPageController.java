package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.dto.AppointmentDTO;
import com.example.beauty_salon_booking.dto.AvailableTimeSlotDTO;
import com.example.beauty_salon_booking.services.AppointmentService;
import com.example.beauty_salon_booking.services.BeautyServiceService;
import com.example.beauty_salon_booking.services.MasterService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Controller
@RequestMapping("/appointment")
public class AppointmentPageController {

    private final BeautyServiceService beautyServiceService;
    private final MasterService masterService;
    private final AppointmentService appointmentService;

    public AppointmentPageController(
            BeautyServiceService beautyServiceService,
            MasterService masterService,
            AppointmentService appointmentService) {
        this.beautyServiceService = beautyServiceService;
        this.masterService = masterService;
        this.appointmentService = appointmentService;
    }

    @PostMapping("/select")
    public String processSelection(@RequestParam Long beautyServiceId,
                                   @RequestParam(required = false) Long masterId,
                                   @RequestParam(required = false) String selectedDate,
                                   RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("beautyServiceId", beautyServiceId);
        if (masterId != null) {
            redirectAttributes.addAttribute("masterId", masterId);
        }
        if (selectedDate != null) {
            redirectAttributes.addAttribute("selectedDate", selectedDate);
        }
        return "redirect:/appointment/select";
    }

    @GetMapping("/select")
    public String showAppointmentPage(@RequestParam(required = false) Long beautyServiceId,
                                      @RequestParam(required = false) Long masterId,
                                      @RequestParam(required = false) String selectedDate,
                                      Model model,
                                      RedirectAttributes redirectAttributes,
                                      @ModelAttribute("successMessage") String successMessage,
                                      @ModelAttribute("errorMessage") String errorMessage) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MASTER"))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Мастера не могут записываться на услуги.");
            return "redirect:/dashboard"; // Или любая подходящая страница
        }

        model.addAttribute("services", beautyServiceService.getAllBeautyServices());

        if (beautyServiceId != null) {
            model.addAttribute("selectedServiceId", beautyServiceId);
            model.addAttribute("masters", beautyServiceService.getMastersByBeautyServiceId(beautyServiceId));
        }

        if (masterId != null) {
            model.addAttribute("selectedMasterId", masterId);

            LocalDate today = LocalDate.now();
            LocalDate endDate = today.plusDays(14);
            Map<LocalDate, List<AvailableTimeSlotDTO>> availableDates =
                    masterService.getAvailableTimeSlots(masterId, today, endDate);
            model.addAttribute("availableDates", availableDates);

            if (selectedDate != null) {
                try {
                    LocalDate parsedDate = LocalDate.parse(selectedDate);
                    model.addAttribute("selectedDate", parsedDate);
                    List<AvailableTimeSlotDTO> timeSlots = availableDates.get(parsedDate);
                    model.addAttribute("timeSlots", timeSlots);
                } catch (Exception e) {
                    model.addAttribute("errorMessage", "Неверный формат даты");
                }
            }
        }

        model.addAttribute("successMessage", successMessage);
        model.addAttribute("errorMessage", errorMessage);

        return "appointment";
    }

    @PostMapping("/confirm")
    public String confirmAppointment(@RequestParam Map<String, String> params,
                                     RedirectAttributes redirectAttributes) {
        try {
            Long beautyServiceId = Long.parseLong(params.get("beautyServiceId"));
            Long masterId = Long.parseLong(params.get("masterId"));
            LocalDate date = LocalDate.parse(params.get("date"));
            LocalTime time = LocalTime.parse(params.get("time"));

            AppointmentDTO appointmentDTO = appointmentService.createAppointment(beautyServiceId, masterId, date, time);

            redirectAttributes.addFlashAttribute("successMessage", "Запись успешно создана на " +
                    appointmentDTO.getDate() + " в " + appointmentDTO.getTime());
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при создании записи: " + e.getMessage());
            return "redirect:/appointment/select";
        }
    }

}