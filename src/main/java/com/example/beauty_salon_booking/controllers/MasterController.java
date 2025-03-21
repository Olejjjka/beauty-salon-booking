package com.example.beauty_salon_booking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.example.beauty_salon_booking.entities.Master;
import com.example.beauty_salon_booking.services.MasterService;
import com.example.beauty_salon_booking.entities.BeautyService;
import com.example.beauty_salon_booking.services.BeautyServiceService;

@RestController
@RequestMapping("/masters")
public class MasterController {
    private final MasterService masterService;
    private final BeautyServiceService beautyServiceService;

    @Autowired
    public MasterController(MasterService masterService, BeautyServiceService beautyServiceService) {

        this.masterService = masterService;
        this.beautyServiceService = beautyServiceService;
    }

    @PostMapping("/register")
    public ResponseEntity<Master> createMaster(@RequestBody Master master) {
        return ResponseEntity.status(HttpStatus.CREATED).body(masterService.saveMaster(master));
    }

    @PostMapping("/{masterId}/beauty-services/{beautyServiceId}")
    public ResponseEntity<Master> addBeautyServiceToMaster(@PathVariable Long masterId, @PathVariable Long beautyServiceId) {
        Master master = masterService.getMasterById(masterId)
                .orElseThrow(() -> new RuntimeException("Master not found"));

        BeautyService beautyService = beautyServiceService.getBeautyServiceById(beautyServiceId)
                .orElseThrow(() -> new RuntimeException("Beauty Service not found"));

        master.getBeautyServices().add(beautyService);
        beautyService.getMasters().add(master);

        masterService.saveMaster(master);
        beautyServiceService.saveBeautyService(beautyService);

        return ResponseEntity.ok(master);
    }

    @GetMapping
    public List<Master> getAllMasters() {
        return masterService.getAllMasters();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Master> getMasterById(@PathVariable Long id) {
        return masterService.getMasterById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<Master> getMasterByName(@PathVariable String name) {
        return masterService.getMasterByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-phone/{phone}")
    public ResponseEntity<Master> getMasterByPhone(@PathVariable String phone) {
        return masterService.getMasterByPhone(phone)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-login/{login}")
    public ResponseEntity<Master> getMasterByLogin(@PathVariable String login) {
        return masterService.getMasterByLogin(login)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Master> replaceMaster(@PathVariable Long id, @RequestBody Master newMaster) {
        return masterService.replaceMaster(id, newMaster)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Master> updateMaster(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return masterService.updateMaster(id, updates)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaster(@PathVariable Long id) {
        masterService.deleteMaster(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{masterId}/services/{serviceId}")
    public ResponseEntity<Void> removeBeautyServiceFromMaster(@PathVariable Long masterId, @PathVariable Long serviceId) {
        Master master = masterService.getMasterById(masterId)
                .orElseThrow(() -> new RuntimeException("Master not found"));

        BeautyService beautyService = beautyServiceService.getBeautyServiceById(serviceId)
                .orElseThrow(() -> new RuntimeException("Beauty Service not found"));

        master.getBeautyServices().remove(beautyService);
        beautyService.getMasters().remove(master);

        masterService.saveMaster(master);
        beautyServiceService.saveBeautyService(beautyService);

        return ResponseEntity.noContent().build();
    }
}