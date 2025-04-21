package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.dto.AppointmentDTO;
import com.example.beauty_salon_booking.dto.AvailableTimeSlotDTO;
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

    // не нужен
    @PostMapping("/register")
    public ResponseEntity<MasterDTO> createMaster(@RequestBody Master master) {
        return ResponseEntity.status(HttpStatus.CREATED).body(masterService.saveMaster(master));
    }

    // для причастного мастера
    @PostMapping("/{masterId}/beauty-services/{beautyServiceId}")
    public ResponseEntity<MasterDTO> addBeautyServiceToMaster(@PathVariable Long masterId, @PathVariable Long beautyServiceId) {
        MasterDTO masterDTO = masterService.addBeautyServiceToMaster(masterId, beautyServiceId);
        return ResponseEntity.ok(masterDTO);
    }


    // для всех клиентов и мастеров
    @GetMapping
    public List<MasterDTO> getAllMasters() {
        return masterService.getAllMasters();
    }

    // для всех клиентов и мастеров
    @GetMapping("/{id}")
    public ResponseEntity<MasterDTO> getMasterById(@PathVariable Long id) {
        return masterService.getMasterById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для всех клиентов и мастеров
    @GetMapping("/by-name/{name}")
    public ResponseEntity<MasterDTO> getMasterByName(@PathVariable String name) {
        return masterService.getMasterByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // не надо
    @GetMapping("/by-phone/{phone}")
    public ResponseEntity<MasterDTO> getMasterByPhone(@PathVariable String phone) {
        return masterService.getMasterByPhone(phone)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // не надо
    @GetMapping("/by-login/{login}")
    public ResponseEntity<MasterDTO> getMasterByLogin(@PathVariable String login) {
        return masterService.getMasterByLogin(login)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для всех клиентов и мастеров
    @GetMapping("/{masterId}/beauty-services")
    public ResponseEntity<List<BeautyServiceDTO>> getBeautyServicesByMasterId(@PathVariable Long masterId) {
        return ResponseEntity.ok(masterService.getBeautyServicesByMasterId(masterId));
    }

    // для причастного мастера
    @GetMapping("/{masterId}/appointments")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByMasterId(@PathVariable Long masterId) {
        return ResponseEntity.ok(masterService.getAppointmentsByMasterId(masterId));
    }

    // для всех клиентов и мастеров
    @GetMapping("/{masterId}/available-time-slots")
    public ResponseEntity<Map<LocalDate, List<AvailableTimeSlotDTO>>> getAvailableTimeSlots(
            @PathVariable Long masterId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        if (endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest().body(null);
        }

        Map<LocalDate, List<AvailableTimeSlotDTO>> availableSlots =
                masterService.getAvailableTimeSlots(masterId, startDate, endDate);

        return ResponseEntity.ok(availableSlots);
    }

    // для причастного мастера
    @PutMapping("/{id}")
    public ResponseEntity<MasterDTO> replaceMaster(@PathVariable Long id, @RequestBody Master newMaster) {
        return masterService.replaceMaster(id, newMaster)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для причастного мастера
    @PatchMapping("/{id}")
    public ResponseEntity<MasterDTO> updateMaster(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return masterService.updateMaster(id, updates)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для причастного мастера
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaster(@PathVariable Long id) {
        masterService.deleteMaster(id);
        return ResponseEntity.noContent().build();
    }

    // для причастного мастера
    @DeleteMapping("/{masterId}/beauty-services/{beautyServiceId}")
    public ResponseEntity<Void> removeBeautyServiceFromMaster(@PathVariable Long masterId, @PathVariable Long beautyServiceId) {
        masterService.removeBeautyServiceFromMaster(masterId, beautyServiceId);
        return ResponseEntity.noContent().build();
    }
}