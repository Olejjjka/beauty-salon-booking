package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.dto.AppointmentDTO;
import com.example.beauty_salon_booking.dto.LoginRequestDTO;
import com.example.beauty_salon_booking.dto.RegisterRequestDTO;
import com.example.beauty_salon_booking.dto.TokenResponseDTO;
import com.example.beauty_salon_booking.security.UserPrincipal;
import com.example.beauty_salon_booking.services.AppointmentService;
import com.example.beauty_salon_booking.services.ClientService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;


import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AuthPageController {

    private static final Logger logger = LoggerFactory.getLogger(AuthPageController.class);

    private final ClientService clientService;
    private final AppointmentService appointmentService;
    private final AuthenticationManager authenticationManager;
    private final RestTemplate restTemplate;

    public AuthPageController(ClientService clientService,
                              AppointmentService appointmentService,
                              AuthenticationManager authenticationManager,
                              RestTemplate restTemplate) {
        this.clientService = clientService;
        this.appointmentService = appointmentService;
        this.authenticationManager = authenticationManager;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequestDTO());
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@ModelAttribute LoginRequestDTO loginRequest,
                              HttpSession session,
                              HttpServletResponse response,
                              Model model) {
        try {
            // Обращаемся к AuthController через REST API
            ResponseEntity<TokenResponseDTO> apiResponse = restTemplate.postForEntity(
                    "http://localhost:8080/api/auth/login",
                    loginRequest,
                    TokenResponseDTO.class
            );

            if (apiResponse.getStatusCode().is2xxSuccessful() && apiResponse.getBody() != null) {
                String jwtToken = apiResponse.getBody().getToken();

                // Сохраняем токен в HttpOnly Cookie
                Cookie cookie = new Cookie("jwt", jwtToken);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(60 * 60); // 1 час
                response.addCookie(cookie);

                session.setAttribute("username", loginRequest.getLogin());
                return "redirect:/dashboard";
            } else {
                model.addAttribute("error", "Ошибка авторизации");
                return "login";
            }
        } catch (Exception e) {
            logger.error("Login failed", e);
            model.addAttribute("error", "Неверный логин или пароль");
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequestDTO());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute RegisterRequestDTO registerRequest, Model model) {
        try {
            // Отправляем POST-запрос на сервер для регистрации клиента
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://localhost:8080/api/auth/register/client", // Адрес вашего API
                    registerRequest,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return "redirect:/login"; // Переход на страницу логина после успешной регистрации
            } else {
                model.addAttribute("error", "Ошибка при регистрации: " + response.getBody());
                return "register";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при регистрации");
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        String username = user.getUsername(); // login из токена
        model.addAttribute("username", username);

        try {
            Long clientId = user.getId(); // можно взять напрямую из токена
            List<AppointmentDTO> records = appointmentService.getAppointmentsByClientId(clientId);
            model.addAttribute("records", records);
        } catch (Exception e) {
            model.addAttribute("records", List.of()); // если что-то пойдёт не так
        }

        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

