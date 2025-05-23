package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.example.beauty_salon_booking.entities.Master;
import com.example.beauty_salon_booking.services.MasterService;

@RestController
@RequestMapping("/api/masters")
public class MasterController {

    private final MasterService masterService;

    @Autowired
    public MasterController(MasterService masterService) {
        this.masterService = masterService;
    }

    // для всех клиентов и мастеров
    @GetMapping
    public List<MasterDTO> getAllMasters() {
        return masterService.getAllMasters();
    }

    // для всех клиентов и мастеров
    @GetMapping("/{masterId}")
    public ResponseEntity<MasterDTO> getMasterById(@PathVariable Long masterId) {
        return masterService.getMasterById(masterId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для всех клиентов и мастеров
    @GetMapping("/by-name")
    public ResponseEntity<MasterDTO> getMasterByName(@RequestParam String name) {
        return masterService.getMasterByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для всех клиентов и мастеров
    @GetMapping("/{masterId}/beauty-services")
    public ResponseEntity<List<BeautyServiceDTO>> getBeautyServicesByMasterId(@PathVariable Long masterId) {
        return ResponseEntity.ok(masterService.getBeautyServicesByMasterId(masterId));
    }

    // для всех клиентов и мастеров
    @GetMapping("/{masterId}/available-time-slots")
    public ResponseEntity<Map<LocalDate, List<AvailableTimeSlotDTO>>> getAvailableTimeSlots(
            @PathVariable Long masterId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        if (endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest().body(Collections.emptyMap());
        }

        Map<LocalDate, List<AvailableTimeSlotDTO>> availableSlots =
                masterService.getAvailableTimeSlots(masterId, startDate, endDate);

        return ResponseEntity.ok(availableSlots);
    }

    // для причастного мастера
    @GetMapping("/{masterId}/appointments")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByMasterId(@PathVariable Long masterId) {
        return ResponseEntity.ok(masterService.getAppointmentsByMasterId(masterId));
    }

    // для причастного мастера
    @PostMapping("/{masterId}/beauty-services/{beautyServiceId}")
    public ResponseEntity<MasterDTO> addBeautyServiceToMaster(@PathVariable Long masterId, @PathVariable Long beautyServiceId) {
        MasterDTO masterDTO = masterService.addBeautyServiceToMaster(masterId, beautyServiceId);
        return ResponseEntity.ok(masterDTO);
    }

    // для причастного мастера
    @DeleteMapping("/{masterId}/beauty-services/{beautyServiceId}")
    public ResponseEntity<Void> removeBeautyServiceFromMaster(@PathVariable Long masterId, @PathVariable Long beautyServiceId) {
        masterService.removeBeautyServiceFromMaster(masterId, beautyServiceId);
        return ResponseEntity.noContent().build();
    }

    // для причастного мастера
    @PatchMapping("/{masterId}")
    public ResponseEntity<?> updateMaster(@PathVariable Long masterId, @RequestBody Map<String, Object> updates) {
        masterService.updateMaster(masterId, updates);
        return ResponseEntity.ok("Profile changed successfully. Please log in again.");
    }

    // для причастного мастера
    @PostMapping("/{masterId}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long masterId,
            @RequestBody ChangePasswordRequestDTO requestDto
    ) {
        masterService.changePassword(masterId, requestDto.getOldPassword(), requestDto.getNewPassword());
        return ResponseEntity.ok("Password changed successfully. Please log in again.");
    }

    // для причастного мастера
    @DeleteMapping("/{masterId}")
    public ResponseEntity<Void> deleteMaster(@PathVariable Long masterId) {
        masterService.deleteMaster(masterId);
        return ResponseEntity.noContent().build();
    }



    // не надо
    @GetMapping("/by-phone")
    public ResponseEntity<MasterDTO> getMasterByPhone(@RequestParam String phone) {
        return masterService.getMasterByPhone(phone)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // не надо
    @GetMapping("/by-login")
    public ResponseEntity<MasterDTO> getMasterByLogin(@RequestParam String login) {
        return masterService.getMasterByLogin(login)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // не надо (для причастного мастера)
    @PutMapping("/{masterId}")
    public ResponseEntity<MasterDTO> replaceMaster(@PathVariable Long masterId, @RequestBody Master newMaster) {
        return masterService.replaceMaster(masterId, newMaster)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}