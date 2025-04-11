package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.dto.LoginRequestDTO;
import com.example.beauty_salon_booking.dto.RegisterRequestDTO;
import com.example.beauty_salon_booking.dto.TokenResponseDTO;
import com.example.beauty_salon_booking.security.JwtTokenProvider;
import com.example.beauty_salon_booking.services.ClientService;
import com.example.beauty_salon_booking.services.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ClientService clientService;
    private final MasterService masterService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(ClientService clientService, MasterService masterService, JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        this.clientService = clientService;
        this.masterService = masterService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    // Регистрация клиента
    @PostMapping("/register/client")
    public ResponseEntity<?> registerClient(@RequestBody RegisterRequestDTO registerRequest) {
        clientService.saveClient(registerRequest.toClient());
        return ResponseEntity.status(HttpStatus.CREATED).body("Client registered successfully");
    }

    // Регистрация мастера
    @PostMapping("/register/master")
    public ResponseEntity<?> registerMaster(@RequestBody RegisterRequestDTO registerRequest) {
        masterService.saveMaster(registerRequest.toMaster());
        return ResponseEntity.status(HttpStatus.CREATED).body("Master registered successfully");
    }

    // Вход и получение JWT
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> authenticate(@RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword())
        );
        String token = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new TokenResponseDTO(token));
    }
}
