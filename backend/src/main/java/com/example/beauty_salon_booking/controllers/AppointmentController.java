package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.dto.AppointmentDTO;
import com.example.beauty_salon_booking.dto.ClientDTO;
import com.example.beauty_salon_booking.dto.MasterDTO;
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

    // для причастных клиента и мастера, которые связаны с конкретной записью
    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Long appointmentId) {
        return appointmentService.getAppointmentById(appointmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для причастных клиента и мастера, которые связаны с конкретной записью
    @GetMapping("/{appointmentId}/client")
    public ResponseEntity<ClientDTO> getClientFromAppointment(@PathVariable Long appointmentId) {
        return appointmentService.getClientByAppointmentId(appointmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для причастных клиента и мастера, которые связаны с конкретной записью
    @GetMapping("/{appointmentId}/master")
    public ResponseEntity<MasterDTO> getMasterFromAppointment(@PathVariable Long appointmentId) {
        return appointmentService.getMasterByAppointmentId(appointmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для причастного клиента, который связан с конкретной записью
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByClientId(clientId));
    }

    // для причастного мастера, который связан с конкретной записью
    @GetMapping("/master/{masterId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByMaster(@PathVariable Long masterId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByMasterId(masterId));
    }

    // для причастных клиента и мастера, которые связаны с конкретной записью
    @GetMapping("/beautyService/{beautyServiceId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByBeautyServiceId(@PathVariable Long beautyServiceId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByBeautyServiceId(beautyServiceId));
    }

    // для причастных клиента и мастера, которые связаны с конкретной записью
    @GetMapping("/by-dateAndTime")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByDateAndTime(@RequestParam LocalDate date,
                                                                             @RequestParam LocalTime time) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDateAndTime(date, time));
    }

    // для причастных клиента и мастера, которые связаны с конкретной записью
    @GetMapping("/by-status")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByStatus(@RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByStatus(status));
    }

    // для всех клиентов
    @PostMapping("/create")
    public ResponseEntity<AppointmentDTO> createAppointment(@RequestBody Map<String, String> payload) {
        Long beautyServiceId = Long.parseLong(payload.get("beautyServiceId"));
        Long masterId = Long.parseLong(payload.get("masterId"));
        LocalDate date = LocalDate.parse(payload.get("date"));
        LocalTime time = LocalTime.parse(payload.get("time"));
        AppointmentDTO appointmentDTO = appointmentService.createAppointment(beautyServiceId, masterId, date, time);
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentDTO);
    }

    // для причастного мастера, который связан с конкретной записью
    @PutMapping("/{appointmentId}")
    public ResponseEntity<AppointmentDTO> replaceAppointment(@PathVariable Long appointmentId, @RequestBody Appointment newAppointment) {
        return appointmentService.replaceAppointment(appointmentId, newAppointment)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для причастного мастера, который связан с конкретной записью
    @PatchMapping("/{appointmentId}")
    public ResponseEntity<AppointmentDTO> updateAppointment(@PathVariable Long appointmentId, @RequestBody Map<String, Object> updates) {
        return appointmentService.updateAppointment(appointmentId, updates)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для причастного мастера, который связан с конкретной записью
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
        return ResponseEntity.noContent().build();
    }



    // не надо
    @GetMapping
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }
}