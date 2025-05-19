package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.dto.ResponseDTO;
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
    public ResponseEntity<ResponseDTO> registerClient(@RequestBody RegisterRequestDTO registerRequest) {
        try {
            clientService.saveClient(registerRequest.toClient());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO(
                    LocalDateTime.now().toString(),
                    HttpStatus.CREATED.value(),
                    "Client registered successfully",
                    "Success"
            ));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(new ResponseDTO(
                    LocalDateTime.now().toString(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Validation error",
                    e.getMessage()
            ));
        }
    }

    // Регистрация мастера
    @PostMapping("/register/master")
    public ResponseEntity<ResponseDTO> registerMaster(@RequestBody RegisterRequestDTO registerRequest) {
        try {
            masterService.saveMaster(registerRequest.toMaster());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO(
                    LocalDateTime.now().toString(),
                    HttpStatus.CREATED.value(),
                    "Master registered successfully",
                    "Success"
            ));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(new ResponseDTO(
                    LocalDateTime.now().toString(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Validation error",
                    e.getMessage()
            ));
        }
    }

    // Вход и получение JWT
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getLogin(),
                            loginRequest.getPassword()
                    )
            );
            String token = jwtTokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new TokenResponseDTO(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO(
                    LocalDateTime.now().toString(),
                    HttpStatus.UNAUTHORIZED.value(),
                    "Authentication failed",
                    "Invalid credentials"
            ));
        }
    }

    // Выход из системы и отзыв токена
    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO> logout(@RequestBody String token) {
        try {
            revokedTokenService.revokeToken(token);
            return ResponseEntity.ok(new ResponseDTO(
                    LocalDateTime.now().toString(),
                    HttpStatus.OK.value(),
                    "Logout successful",
                    "Success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO(
                    LocalDateTime.now().toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Logout failed",
                    e.getMessage()
            ));
        }
    }

}