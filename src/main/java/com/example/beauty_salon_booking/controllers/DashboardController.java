package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.dto.BeautyServiceDTO;
import com.example.beauty_salon_booking.enums.AppointmentStatus;
import com.example.beauty_salon_booking.security.UserPrincipal;
import com.example.beauty_salon_booking.services.BeautyServiceService;
import com.example.beauty_salon_booking.services.ClientService;
import com.example.beauty_salon_booking.services.MasterService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
public class DashboardController {

    private final MasterService masterService;
    private final ClientService clientService;
    private final BeautyServiceService beautyServiceService;

    public DashboardController(MasterService masterService, ClientService clientService, BeautyServiceService beautyServiceService) {
        this.masterService = masterService;
        this.clientService = clientService;
        this.beautyServiceService = beautyServiceService;
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

        String username = user.getName();
        String login = user.getUsername();
        String phone = user.getPhone();
        model.addAttribute("username", username);
        model.addAttribute("login", login);
        model.addAttribute("phone", phone);

        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MASTER"))) {
            Long masterId = user.getId();
            model.addAttribute("masterId", masterId);
            model.addAttribute("services", masterService.getBeautyServicesByMasterId(masterId));
            model.addAttribute("records", masterService.getAppointmentsByMasterId(masterId));
            model.addAttribute("statuses", Arrays.asList(AppointmentStatus.values()));

            // Получаем все доступные услуги
            List<BeautyServiceDTO> assigned = masterService.getBeautyServicesByMasterId(masterId);
            List<BeautyServiceDTO> all = beautyServiceService.getAllBeautyServices();

            Set<Long> assignedIds = new HashSet<>();
            for (BeautyServiceDTO service : assigned) {
                if (service.getId() != null) {
                    assignedIds.add(service.getId());
                }
            }

            List<BeautyServiceDTO> available = new ArrayList<>();
            for (BeautyServiceDTO service : all) {
                Long id = service.getId();
                if (id != null && !assignedIds.contains(id)) {
                    available.add(service);
                }
            }

            model.addAttribute("allServices", available);
            model.addAttribute("allAssigned", available.isEmpty());

            return "dashboardMaster";
        } else {
            Long clientId = user.getId();
            model.addAttribute("clientId", clientId);
            model.addAttribute("records", clientService.getAppointmentsByClientId(clientId));
            return "dashboardClient";
        }
    }

    @PostMapping("/dashboard/master/add-service")
    public String addServiceToMaster(@RequestParam Long masterId,
                                     @RequestParam Long beautyServiceId) {
        masterService.addBeautyServiceToMaster(masterId, beautyServiceId);
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/master/remove-service")
    public String removeServiceFromMaster(@RequestParam Long masterId,
                                          @RequestParam Long serviceId) {
        masterService.removeBeautyServiceFromMaster(masterId, serviceId);
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/master/update-status")
    public String updateAppointmentStatus(@RequestParam Long appointmentId,
                                          @RequestParam String status) {
        masterService.updateAppointmentStatus(appointmentId, status);
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/master/update-profile")
    public String updateMasterProfile(
            @RequestParam Long masterId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String login,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> updates = new HashMap<>();

        if (name != null && !name.trim().isEmpty()) {
            updates.put("username", name.trim());
        }
        if (phone != null && !phone.trim().isEmpty()) {
            updates.put("phone", phone.trim());
        }
        if (login != null && !login.trim().isEmpty()) {
            updates.put("login", login.trim());
        }

        if (updates.isEmpty()) {
            redirectAttributes.addFlashAttribute("warning", "Нужно заполнить хотя бы одно поле для обновления профиля.");
            return "redirect:/dashboard";
        }

        masterService.updateMaster(masterId, updates);
        return "redirect:/logout";
    }


    @PostMapping("/dashboard/master/change-password")
    public String changeMasterPassword(@RequestParam Long masterId,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 RedirectAttributes redirectAttributes) {
        try {
            masterService.changePassword(masterId, oldPassword, newPassword);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Указан неверный текущий пароль");
            return "redirect:/dashboard";
        }
        return "redirect:/logout"; // после смены пароля — выход
    }

    @PostMapping("/dashboard/client/update-profile")
    public String updateClientProfile(
            @RequestParam Long clientId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String login,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> updates = new HashMap<>();

        if (name != null && !name.trim().isEmpty()) {
            updates.put("username", name.trim());
        }
        if (phone != null && !phone.trim().isEmpty()) {
            updates.put("phone", phone.trim());
        }
        if (login != null && !login.trim().isEmpty()) {
            updates.put("login", login.trim());
        }

        if (updates.isEmpty()) {
            redirectAttributes.addFlashAttribute("warning", "Нужно заполнить хотя бы одно поле для обновления профиля.");
            return "redirect:/dashboard";
        }

        clientService.updateClient(clientId, updates);
        return "redirect:/logout";
    }


    @PostMapping("/dashboard/client/change-password")
    public String changeClientPassword(@RequestParam Long clientId,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 RedirectAttributes redirectAttributes) {
        try {
            clientService.changePassword(clientId, oldPassword, newPassword);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Указан неверный текущий пароль");
            return "redirect:/dashboard";
        }
        return "redirect:/logout"; // после смены пароля — выход
    }
}