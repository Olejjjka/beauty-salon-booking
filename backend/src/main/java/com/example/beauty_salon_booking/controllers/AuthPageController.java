package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.dto.ResponseDTO;
import com.example.beauty_salon_booking.dto.LoginRequestDTO;
import com.example.beauty_salon_booking.dto.RegisterRequestDTO;
import com.example.beauty_salon_booking.dto.TokenResponseDTO;
import com.example.beauty_salon_booking.exceptions.MessageExtractor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
public class AuthPageController {
    private static final Logger logger = LoggerFactory.getLogger(AuthPageController.class);
    private final RestTemplate restTemplate;

    public AuthPageController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping({"", "/"})
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequestDTO());
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@ModelAttribute("loginRequest") @Valid LoginRequestDTO loginRequest,
                              HttpServletResponse response,
                              RedirectAttributes redirectAttributes) {
        try {
            ResponseEntity<TokenResponseDTO> apiResponse = restTemplate.postForEntity(
                    "http://localhost:8080/api/auth/login",
                    loginRequest,
                    TokenResponseDTO.class
            );

            if (apiResponse.getStatusCode().is2xxSuccessful() &&
                    apiResponse.getBody() != null &&
                    apiResponse.getBody().getToken() != null) {

                String jwtToken = apiResponse.getBody().getToken();

                Cookie cookie = new Cookie("jwt", jwtToken);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(60 * 60);
                response.addCookie(cookie);

                return "redirect:/homepage";
            } else {
                redirectAttributes.addFlashAttribute("errorLogin", "Ошибка при аутентификации");
                return "redirect:/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorLogin", "Неверный логин или пароль");
            return "redirect:/login";
        }
    }


    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequestDTO());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute("registerRequest") @Valid RegisterRequestDTO registerRequest,
                                 RedirectAttributes redirectAttributes) {
        try {
            ResponseEntity<ResponseDTO> response = restTemplate.postForEntity(
                    "http://localhost:8080/api/auth/register/client",
                    registerRequest,
                    ResponseDTO.class
            );

            // Если регистрация успешна, редирект на страницу логина
            if (response.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addFlashAttribute("successRegister", "Регистрация прошла успешно");
                return "redirect:/login";
            } else {
                // Ошибка с регистрацией (например, с API)
                redirectAttributes.addFlashAttribute("errorRegister", "Ошибка при регистрации");
                return "redirect:/register";
            }
        } catch (Exception e) {
            logger.error("Register failed", e);
            redirectAttributes.addFlashAttribute("errorRegister", MessageExtractor.extractMessage(e.getMessage()));
            return "redirect:/register";
        }
    }

    @GetMapping("/homepage")
    public String showHomepage() {
        return "homepage";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null) {
            try {
                ResponseEntity<ResponseDTO> apiResponse = restTemplate.postForEntity(
                        "http://localhost:8080/api/auth/logout",
                        token,
                        ResponseDTO.class
                );

                if (apiResponse.getStatusCode().is2xxSuccessful()) {
                    redirectAttributes.addFlashAttribute("successLogout", "Успешный выход из системы");
                } else {
                    redirectAttributes.addFlashAttribute("errorLogout", "Ошибка при выходе из системы");
                }
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorLogout", "Ошибка при выходе: " + e.getMessage());
            }
        }

        // Удаляем токен из cookie
        Cookie cookie = new Cookie("jwt", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/login";
    }
}