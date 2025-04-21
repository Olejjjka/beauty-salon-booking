package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.dto.AppointmentDTO;
import com.example.beauty_salon_booking.dto.ClientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    // не нужен (можно удалить)
    @PostMapping("/register")
    public ResponseEntity<ClientDTO> createClient(@RequestBody Client client) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.saveClient(client));
    }

    // не нужен (можно удалить)
    @GetMapping
    public List<ClientDTO> getAllClients() {
        return clientService.getAllClients();
    }

    // для причастных клиента и мастера
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        return clientService.getClientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // не нужен (можно удалить)
    @GetMapping("/by-phone/{phone}")
    public ResponseEntity<ClientDTO> getClientByPhone(@PathVariable String phone) {
        return clientService.getClientByPhone(phone)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // не нужен (можно удалить)
    @GetMapping("/by-login/{login}")
    public ResponseEntity<ClientDTO> getClientByLogin(@PathVariable String login) {
        return clientService.getClientByLogin(login)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для причастного клиента
    @GetMapping("/{id}/appointments")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByClientId(@PathVariable Long id) {
        List<AppointmentDTO> appointments = clientService.getAppointmentsByClientId(id);
        if (appointments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(appointments);
    }

    // для причастного клиента
    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> replaceClient(@PathVariable Long id, @RequestBody Client newClient) {
        return clientService.replaceClient(id, newClient)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для причастного клиента
    @PatchMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return clientService.updateClient(id, updates)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для причастного клиента
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}