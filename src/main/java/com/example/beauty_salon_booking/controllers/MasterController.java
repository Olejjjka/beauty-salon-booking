package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.dto.AppointmentDTO;
import com.example.beauty_salon_booking.dto.BeautyServiceDTO;
import com.example.beauty_salon_booking.dto.MasterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @PostMapping("/register")
    public ResponseEntity<MasterDTO> createMaster(@RequestBody Master master) {
        return ResponseEntity.status(HttpStatus.CREATED).body(masterService.saveMaster(master));
    }

    @PostMapping("/{masterId}/beauty-services/{beautyServiceId}")
    public ResponseEntity<MasterDTO> addBeautyServiceToMaster(@PathVariable Long masterId, @PathVariable Long beautyServiceId) {
        MasterDTO masterDTO = masterService.addBeautyServiceToMaster(masterId, beautyServiceId);
        return ResponseEntity.ok(masterDTO);
    }

    @GetMapping
    public List<MasterDTO> getAllMasters() {
        return masterService.getAllMasters();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MasterDTO> getMasterById(@PathVariable Long id) {
        return masterService.getMasterById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<MasterDTO> getMasterByName(@PathVariable String name) {
        return masterService.getMasterByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-phone/{phone}")
    public ResponseEntity<MasterDTO> getMasterByPhone(@PathVariable String phone) {
        return masterService.getMasterByPhone(phone)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-login/{login}")
    public ResponseEntity<MasterDTO> getMasterByLogin(@PathVariable String login) {
        return masterService.getMasterByLogin(login)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{masterId}/beauty-services")
    public ResponseEntity<List<BeautyServiceDTO>> getBeautyServicesByMasterId(@PathVariable Long masterId) {
        return ResponseEntity.ok(masterService.getBeautyServicesByMasterId(masterId));
    }

    @GetMapping("/{masterId}/appointments")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByMasterId(@PathVariable Long masterId) {
        return ResponseEntity.ok(masterService.getAppointmentsByMasterId(masterId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MasterDTO> replaceMaster(@PathVariable Long id, @RequestBody Master newMaster) {
        return masterService.replaceMaster(id, newMaster)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MasterDTO> updateMaster(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return masterService.updateMaster(id, updates)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaster(@PathVariable Long id) {
        masterService.deleteMaster(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{masterId}/beauty-services/{beautyServiceId}")
    public ResponseEntity<Void> removeBeautyServiceFromMaster(@PathVariable Long masterId, @PathVariable Long beautyServiceId) {
        masterService.removeBeautyServiceFromMaster(masterId, beautyServiceId);
        return ResponseEntity.noContent().build();
    }


    // Новый метод для получения доступных интервалов у мастера
    @GetMapping("/{masterId}/available-time-slots")
    public ResponseEntity<Map<LocalDate, List<MasterService.AvailableTimeSlotDTO>>> getAvailableTimeSlots(
            @PathVariable Long masterId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        if (endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest().body(null);
        }

        Map<LocalDate, List<MasterService.AvailableTimeSlotDTO>> availableSlots =
                masterService.getAvailableTimeSlots(masterId, startDate, endDate);

        return ResponseEntity.ok(availableSlots);
    }
}