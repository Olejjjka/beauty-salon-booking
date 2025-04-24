package com.example.beauty_salon_booking.controllers;

import com.example.beauty_salon_booking.dto.BeautyServiceDTO;
import com.example.beauty_salon_booking.dto.MasterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.example.beauty_salon_booking.entities.BeautyService;
import com.example.beauty_salon_booking.services.BeautyServiceService;

@RestController
@RequestMapping("/api/beauty-services")
public class BeautyServiceController {

    private final BeautyServiceService beautyServiceService;

    @Autowired
    public BeautyServiceController(BeautyServiceService beautyServiceService) {
        this.beautyServiceService = beautyServiceService;
    }

    // для всех клиентов и мастеров
    @GetMapping
    public List<BeautyServiceDTO> getAllBeautyServices() {
        return beautyServiceService.getAllBeautyServices();
    }

    // для всех клиентов и мастеров
    @GetMapping("/{beautyServiceId}")
    public ResponseEntity<BeautyServiceDTO> getBeautyServiceById(@PathVariable Long beautyServiceId) {
        return beautyServiceService.getBeautyServiceById(beautyServiceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для всех клиентов и мастеров
    @GetMapping("/by-name")
    public ResponseEntity<BeautyServiceDTO> getBeautyServiceByName(@RequestParam String name) {
        return beautyServiceService.getBeautyServiceByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для всех клиентов и мастеров
    @GetMapping("/price-range")
    public ResponseEntity<List<BeautyServiceDTO>> getBeautyServiceByPriceRange(@RequestParam double minPrice, @RequestParam double maxPrice) {
        return ResponseEntity.ok(beautyServiceService.getBeautyServicesByPriceRange(minPrice, maxPrice));
    }

    // для всех клиентов и мастеров
    @GetMapping("/{beautyServiceId}/masters")
    public ResponseEntity<List<MasterDTO>> getMastersByBeautyServiceId(@PathVariable Long beautyServiceId) {
        return ResponseEntity.ok(beautyServiceService.getMastersByBeautyServiceId(beautyServiceId));
    }

    // для всех мастеров
    @PostMapping("/create")
    public ResponseEntity<BeautyServiceDTO> createBeautyService(@RequestBody BeautyService beautyService) {
        return ResponseEntity.status(HttpStatus.CREATED).body(beautyServiceService.saveBeautyService(beautyService));
    }

    // для всех мастеров
    @PutMapping("/{beautyServiceId}")
    public ResponseEntity<BeautyServiceDTO> replaceBeautyService(@PathVariable Long beautyServiceId, @RequestBody BeautyService newBeautyService) {
        return beautyServiceService.replaceBeautyService(beautyServiceId, newBeautyService)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для всех мастеров
    @PatchMapping("/{beautyServiceId}")
    public ResponseEntity<BeautyServiceDTO> updateBeautyService(@PathVariable Long beautyServiceId, @RequestBody Map<String, Object> updates) {
        return beautyServiceService.updateBeautyService(beautyServiceId, updates)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // для всех мастеров
    @DeleteMapping("/{beautyServiceId}")
    public ResponseEntity<Void> deleteBeautyService(@PathVariable Long beautyServiceId) {
        beautyServiceService.deleteBeautyService(beautyServiceId);
        return ResponseEntity.noContent().build();
    }



    // не надо
    @PostMapping("/{beautyServiceId}/masters/{masterId}")
    public ResponseEntity<BeautyServiceDTO> addMasterToBeautyService(@PathVariable Long beautyServiceId, @PathVariable Long masterId) {
        BeautyServiceDTO beautyServiceDTO = beautyServiceService.addMasterToBeautyService(beautyServiceId, masterId);
        return ResponseEntity.ok(beautyServiceDTO);
    }

    // не надо
    @DeleteMapping("/{beautyServiceId}/masters/{masterId}")
    public ResponseEntity<Void> removeMasterFromBeautyService(@PathVariable Long beautyServiceId, @PathVariable Long masterId) {
        beautyServiceService.removeMasterFromBeautyService(beautyServiceId, masterId);
        return ResponseEntity.noContent().build();
    }
}