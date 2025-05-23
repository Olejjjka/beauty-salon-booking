package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.dto.AppointmentDTO;
import com.example.beauty_salon_booking.dto.ChangePasswordRequestDTO;
import com.example.beauty_salon_booking.dto.ClientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

import com.example.beauty_salon_booking.entities.Client;
import com.example.beauty_salon_booking.services.ClientService;


@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // для причастного клиента
    @GetMapping("/{clientId}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long clientId) {
        return clientService.getClientById(clientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для причастного клиента
    @GetMapping("/{clientId}/appointments")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByClientId(@PathVariable Long clientId) {
        List<AppointmentDTO> appointments = clientService.getAppointmentsByClientId(clientId);
        if (appointments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(appointments);
    }

    // для причастного клиента
    @PatchMapping("/{clientId}")
    public ResponseEntity<?> updateClient(@PathVariable Long clientId, @RequestBody Map<String, Object> updates) {

        clientService.updateClient(clientId, updates);
        return ResponseEntity.ok("Profile changed successfully. Please log in again.");
    }

    // для причастного клиента
    @PostMapping("/{clientId}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long clientId,
            @RequestBody ChangePasswordRequestDTO requestDto
    ) {
        clientService.changePassword(clientId, requestDto.getOldPassword(), requestDto.getNewPassword());
        return ResponseEntity.ok("Password changed successfully. Please log in again.");
    }

    // для причастного клиента
    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long clientId) {
        clientService.deleteClient(clientId);
        return ResponseEntity.noContent().build();
    }



    // не нужен (можно удалить)
    @GetMapping
    public List<ClientDTO> getAllClients() {
        return clientService.getAllClients();
    }

    // не нужен (можно удалить)
    @GetMapping("/by-phone")
    public ResponseEntity<ClientDTO> getClientByPhone(@RequestParam String phone) {
        return clientService.getClientByPhone(phone)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // не нужен (можно удалить)
    @GetMapping("/by-login")
    public ResponseEntity<ClientDTO> getClientByLogin(@RequestParam String login) {
        return clientService.getClientByLogin(login)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // не надо (для причастного клиента)
    @PutMapping("/{clientId}")
    public ResponseEntity<ClientDTO> replaceClient(@PathVariable Long clientId, @RequestBody Client newClient) {
        return clientService.replaceClient(clientId, newClient)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}