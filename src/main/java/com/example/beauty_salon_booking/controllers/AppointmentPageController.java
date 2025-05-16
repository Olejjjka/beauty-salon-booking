package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.dto.AppointmentDTO;
import com.example.beauty_salon_booking.dto.AvailableTimeSlotDTO;
import com.example.beauty_salon_booking.dto.BeautyServiceDTO;
import com.example.beauty_salon_booking.services.AppointmentService;
import com.example.beauty_salon_booking.services.BeautyServiceService;
import com.example.beauty_salon_booking.services.MasterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/appointments")
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



    @GetMapping("/masters/{id}/available-slots")
    public String getAvailableTimeSlots(@PathVariable("id") Long masterId, Model model) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(14);

        Map<LocalDate, List<AvailableTimeSlotDTO>> availableDates =
                masterService.getAvailableTimeSlots(masterId, today, endDate);

        model.addAttribute("availableDates", availableDates);
        model.addAttribute("selectedDate", today);
        return "appointments/choose_time";
    }

    @PostMapping("/select")
    public String processSelection(@RequestParam Long beautyServiceId,
                                   @RequestParam(required = false) Long masterId,
                                   RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("beautyServiceId", beautyServiceId);
        if (masterId != null) {
            redirectAttributes.addAttribute("masterId", masterId);
        }
        return "redirect:/appointments/select";
    }

    @GetMapping("/select")
    public String showSelectionPage(@RequestParam(required = false) Long beautyServiceId,
                                    @RequestParam(required = false) Long masterId,
                                    Model model) {
        model.addAttribute("services", beautyServiceService.getAllBeautyServices());

        if (beautyServiceId != null) {
            model.addAttribute("selectedServiceId", beautyServiceId);
            model.addAttribute("masters", beautyServiceService.getMastersByBeautyServiceId(beautyServiceId));
        }

        if (masterId != null) {
            model.addAttribute("selectedMasterId", masterId);
        }

        return "appointments";
    }

    @PostMapping("/confirm")
    public String confirmAppointment(@RequestParam Map<String, String> params,
                                     RedirectAttributes redirectAttributes) {
        try {
            // Передаем параметры в сервис как Map<String, Object>
            Map<String, Object> payload = new HashMap<>(params);

            AppointmentDTO appointmentDTO = appointmentService.createAppointment(payload);

            redirectAttributes.addFlashAttribute("successMessage", "Запись успешно создана на " +
                    appointmentDTO.getDate() + " в " + appointmentDTO.getTime());
            return "redirect:/appointments/select?beautyServiceId=" + appointmentDTO.getBeautyService().getId() +
                    "&masterId=" + appointmentDTO.getMaster().getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при создании записи: " + e.getMessage());
            // Возвращаем на страницу выбора, можно вернуть параметры для восстановления выбора
            return "redirect:/appointments/select";
        }
    }


}
