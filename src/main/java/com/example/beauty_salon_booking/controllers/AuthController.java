package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.dto.ErrorResponseDTO;
import com.example.beauty_salon_booking.dto.LoginRequestDTO;
import com.example.beauty_salon_booking.dto.RegisterRequestDTO;
import com.example.beauty_salon_booking.dto.TokenResponseDTO;
import com.example.beauty_salon_booking.exceptions.ValidationException;
import com.example.beauty_salon_booking.security.JwtTokenProvider;
import com.example.beauty_salon_booking.services.ClientService;
import com.example.beauty_salon_booking.services.MasterService;
import com.example.beauty_salon_booking.services.RevokedTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ClientService clientService;
    private final MasterService masterService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RevokedTokenService revokedTokenService;

    @Autowired
    public AuthController(ClientService clientService, MasterService masterService, JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager, RevokedTokenService revokedTokenService) {
        this.clientService = clientService;
        this.masterService = masterService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.revokedTokenService = revokedTokenService;
    }

    // Регистрация клиента
    @PostMapping("/register/client")
    public ResponseEntity<ErrorResponseDTO> registerClient(@RequestBody RegisterRequestDTO registerRequest) {
        try {
            clientService.saveClient(registerRequest.toClient());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ErrorResponseDTO(
                    LocalDateTime.now().toString(),
                    HttpStatus.CREATED.value(),
                    "Client registered successfully",
                    "Success"
            ));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(
                    LocalDateTime.now().toString(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Validation error",
                    e.getMessage()
            ));
        }
    }

    // Регистрация мастера
    @PostMapping("/register/master")
    public ResponseEntity<ErrorResponseDTO> registerMaster(@RequestBody RegisterRequestDTO registerRequest) {
        try {
            masterService.saveMaster(registerRequest.toMaster());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ErrorResponseDTO(
                    LocalDateTime.now().toString(),
                    HttpStatus.CREATED.value(),
                    "Master registered successfully",
                    "Success"
            ));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(
                    LocalDateTime.now().toString(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Validation error",
                    e.getMessage()
            ));
        }
    }

    // Вход и получение JWT
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> authenticate(@RequestBody LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword())
            );
            String token = jwtTokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new TokenResponseDTO(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenResponseDTO("Invalid credentials"));
        }
    }



    // Не нужен (Выход из системы и отзыв токена)
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // Убираем префикс "Bearer "

        revokedTokenService.revokeToken(token); // Отзываем токен

        return ResponseEntity.ok("Successfully logged out");
    }
}