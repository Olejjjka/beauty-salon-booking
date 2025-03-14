package com.example.beauty_salon_booking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.example.beauty_salon_booking.entities.BeautyService;
import com.example.beauty_salon_booking.services.BeautyServiceService;
import com.example.beauty_salon_booking.entities.Master;
import com.example.beauty_salon_booking.services.MasterService;

@RestController
@RequestMapping("/beauty-services")
public class BeautyServiceController {
    private final BeautyServiceService beautyServiceService;
    private final MasterService masterService;

    @Autowired
    public BeautyServiceController(BeautyServiceService beautyServiceService, MasterService masterService) {
        this.beautyServiceService = beautyServiceService;
        this.masterService = masterService;
    }

    @PostMapping
    public ResponseEntity<BeautyService> createBeautyService(@RequestBody BeautyService beautyService) {
        return ResponseEntity.status(HttpStatus.CREATED).body(beautyServiceService.saveBeautyService(beautyService));
    }

    @PostMapping("/{serviceId}/masters/{masterId}")
    public ResponseEntity<BeautyService> addMasterToBeautyService(@PathVariable Long beautyServiceId, @PathVariable Long masterId) {
        BeautyService beautyService = beautyServiceService.getBeautyServiceById(beautyServiceId)
                .orElseThrow(() -> new RuntimeException("Beauty Service not found"));

        Master master = masterService.getMasterById(masterId)
                .orElseThrow(() -> new RuntimeException("Master not found"));

        beautyService.getMasters().add(master);
        master.getBeautyServices().add(beautyService);

        beautyServiceService.saveBeautyService(beautyService);
        masterService.saveMaster(master);

        return ResponseEntity.ok(beautyService);
    }

    @GetMapping
    public List<BeautyService> getAllBeautyServices() { return beautyServiceService.getAllBeautyServices(); }

    @GetMapping("/{id}")
    public ResponseEntity<BeautyService> getBeautyServiceById(@PathVariable Long id) {
        return beautyServiceService.getBeautyServiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<BeautyService> getBeautyServiceByName(@PathVariable String name) {
        return beautyServiceService.getBeautyServiceByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<BeautyService>> getBeautyServiceByPriceRange(@RequestParam double minPrice, @RequestParam double maxPrice) {
        return ResponseEntity.ok(beautyServiceService.getBeautyServicesByPriceRange(minPrice, maxPrice));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeautyService> replaceBeautyService(@PathVariable Long id, @RequestBody BeautyService newBeautyService) {
        return beautyServiceService.replaceService(id, newBeautyService)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BeautyService> updateBeautyService(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return beautyServiceService.updateService(id, updates)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeautyService(@PathVariable Long id) {
        beautyServiceService.deleteBeautyService(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{serviceId}/masters/{masterId}")
    public ResponseEntity<Void> removeMasterFromBeautyService(@PathVariable Long beautyServiceId, @PathVariable Long masterId) {
        BeautyService beautyService = beautyServiceService.getBeautyServiceById(beautyServiceId)
                .orElseThrow(() -> new RuntimeException("Beauty Service not found"));

        Master master = masterService.getMasterById(masterId)
                .orElseThrow(() -> new RuntimeException("Master not found"));

        beautyService.getMasters().remove(master);
        master.getBeautyServices().remove(beautyService);

        beautyServiceService.saveBeautyService(beautyService);
        masterService.saveMaster(master);

        return ResponseEntity.noContent().build();
    }
}