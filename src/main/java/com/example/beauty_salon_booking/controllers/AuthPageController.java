package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.dto.ErrorResponseDTO;
import com.example.beauty_salon_booking.dto.LoginRequestDTO;
import com.example.beauty_salon_booking.dto.RegisterRequestDTO;
import com.example.beauty_salon_booking.dto.TokenResponseDTO;
import com.example.beauty_salon_booking.exceptions.MessageExtractor;
import com.example.beauty_salon_booking.services.RevokedTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthPageController {

    private static final Logger logger = LoggerFactory.getLogger(AuthPageController.class);
    private final RestTemplate restTemplate;
    private final RevokedTokenService revokedTokenService;

    public AuthPageController(RestTemplate restTemplate,
                              RevokedTokenService revokedTokenService) {
        this.restTemplate = restTemplate;
        this.revokedTokenService = revokedTokenService;
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

            if (apiResponse.getStatusCode().is2xxSuccessful() && apiResponse.getBody() != null) {
                String jwtToken = apiResponse.getBody().getToken();

                Cookie cookie = new Cookie("jwt", jwtToken);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(60 * 60);
                response.addCookie(cookie);

                return "redirect:/homepage";
            } else {
                redirectAttributes.addFlashAttribute("error", "Ошибка авторизации");
                return "redirect:/login";
            }
        } catch (Exception e) {
            logger.error("Login failed", e);
            redirectAttributes.addFlashAttribute("error", "Неверный логин или пароль");
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
            ResponseEntity<ErrorResponseDTO> response = restTemplate.postForEntity(
                    "http://localhost:8080/api/auth/register/client",
                    registerRequest,
                    ErrorResponseDTO.class
            );

            // Если регистрация успешна, редирект на страницу логина
            if (response.getStatusCode().is2xxSuccessful()) {
                return "redirect:/login";
            } else {
                // Ошибка с регистрацией (например, с API)
                redirectAttributes.addFlashAttribute("error", "Ошибка при регистрации");
                return "redirect:/register";
            }
        } catch (Exception e) {
            logger.error("Registration failed", e);
            redirectAttributes.addFlashAttribute("error", MessageExtractor.extractMessage(e.getMessage()));
            return "redirect:/register";
        }
    }

    @GetMapping("/homepage")
    public String showHomepage() {
        return "homepage";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
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
            revokedTokenService.revokeToken(token);
        }

        Cookie cookie = new Cookie("jwt", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/login";
    }
}