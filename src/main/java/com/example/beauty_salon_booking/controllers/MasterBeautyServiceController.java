package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.entities.MasterBeautyService;
import com.example.beauty_salon_booking.services.MasterBeautyServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/master-beauty-services")
public class MasterBeautyServiceController {
    private final MasterBeautyServiceService masterBeautyServiceService;

    @Autowired
    public MasterBeautyServiceController(MasterBeautyServiceService masterBeautyServiceService) {
        this.masterBeautyServiceService = masterBeautyServiceService;
    }

    @PostMapping
    public ResponseEntity<MasterBeautyService> createMasterBeautyService(@RequestBody MasterBeautyService masterBeautyService) {
        return ResponseEntity.status(HttpStatus.CREATED).body(masterBeautyServiceService.saveMasterBeautyService(masterBeautyService));
    }

    @GetMapping
    public List<MasterBeautyService> getAllMasterBeautyServices() {
        return masterBeautyServiceService.getAllMasterBeautyServices();
    }

    @GetMapping("/master/{masterId}")
    public List<MasterBeautyService> getBeautyServicesByMasterId(@PathVariable Long masterId) {
        return masterBeautyServiceService.getBeautyServicesByMasterId(masterId);
    }

    @GetMapping("/service/{beautyServiceId}")
    public List<MasterBeautyService> getMastersByBeautyServiceId(@PathVariable Long beautyServiceId) {
        return masterBeautyServiceService.getMastersByBeautyServiceId(beautyServiceId);
    }

    @GetMapping("/{masterId}/{beautyServiceId}")
    public ResponseEntity<MasterBeautyService> getMasterBeautyService(@PathVariable Long masterId, @PathVariable Long beautyServiceId) {
        return masterBeautyServiceService.getMasterBeautyService(masterId, beautyServiceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MasterBeautyService> replaceMasterBeautyService(@PathVariable Long id, @RequestBody MasterBeautyService newMasterBeautyService) {
        return masterBeautyServiceService.replaceMasterBeautyService(id, newMasterBeautyService)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MasterBeautyService> updateMasterBeautyService(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return masterBeautyServiceService.updateMasterBeautyService(id, updates)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMasterBeautyService(@PathVariable Long id) {
        masterBeautyServiceService.deleteMasterBeautyService(id);
        return ResponseEntity.noContent().build();
    }
}
