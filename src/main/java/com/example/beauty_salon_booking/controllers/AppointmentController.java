package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.dto.AppointmentDTO;
import com.example.beauty_salon_booking.enums.AppointmentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.example.beauty_salon_booking.entities.Appointment;
import com.example.beauty_salon_booking.services.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/create")
    public ResponseEntity<AppointmentDTO> createAppointment(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createAppointment(payload));
    }

    @GetMapping
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByClientId(clientId));
    }

    @GetMapping("/master/{masterId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByMaster(@PathVariable Long masterId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByMasterId(masterId));
    }

    @GetMapping("/beautyService/{beautyServiceId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByBeautyServiceId(@PathVariable Long beautyServiceId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByBeautyServiceId(beautyServiceId));
    }

    @GetMapping("/dateAndTime/{dateAndTime}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByDateAndTime(@PathVariable LocalDate date, LocalTime time) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDateAndTime(date, time));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByStatus(@PathVariable AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> replaceAppointment(@PathVariable Long id, @RequestBody Appointment newAppointment) {
        return appointmentService.replaceAppointment(id, newAppointment)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AppointmentDTO> updateAppointment(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return appointmentService.updateAppointment(id, updates)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}