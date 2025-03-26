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
import com.example.beauty_salon_booking.entities.Master;
import com.example.beauty_salon_booking.services.MasterService;

@RestController
@RequestMapping("/beauty-services")
public class BeautyServiceController {
    private final BeautyServiceService beautyServiceService;

    @Autowired
    public BeautyServiceController(BeautyServiceService beautyServiceService) {
        this.beautyServiceService = beautyServiceService;
    }

    @PostMapping("/create")
    public ResponseEntity<BeautyServiceDTO> createBeautyService(@RequestBody BeautyService beautyService) {
        return ResponseEntity.status(HttpStatus.CREATED).body(beautyServiceService.saveBeautyService(beautyService));
    }

    @PostMapping("/{beautyServiceId}/masters/{masterId}")
    public ResponseEntity<BeautyServiceDTO> addMasterToBeautyService(@PathVariable Long beautyServiceId, @PathVariable Long masterId) {
        BeautyServiceDTO beautyServiceDTO = beautyServiceService.addMasterToBeautyService(beautyServiceId, masterId);
        return ResponseEntity.ok(beautyServiceDTO);
    }

    @GetMapping
    public List<BeautyServiceDTO> getAllBeautyServices() { return beautyServiceService.getAllBeautyServices(); }

    @GetMapping("/{id}")
    public ResponseEntity<BeautyServiceDTO> getBeautyServiceById(@PathVariable Long id) {
        return beautyServiceService.getBeautyServiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<BeautyServiceDTO> getBeautyServiceByName(@PathVariable String name) {
        return beautyServiceService.getBeautyServiceByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<BeautyServiceDTO>> getBeautyServiceByPriceRange(@RequestParam double minPrice, @RequestParam double maxPrice) {
        return ResponseEntity.ok(beautyServiceService.getBeautyServicesByPriceRange(minPrice, maxPrice));
    }

    ///
    @GetMapping("/{beautyServiceId}/masters")
    public ResponseEntity<List<MasterDTO>> getMastersByBeautyServiceId(@PathVariable Long beautyServiceId) {
        return ResponseEntity.ok(beautyServiceService.getMastersByBeautyServiceId(beautyServiceId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeautyServiceDTO> replaceBeautyService(@PathVariable Long id, @RequestBody BeautyService newBeautyService) {
        return beautyServiceService.replaceService(id, newBeautyService)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BeautyServiceDTO> updateBeautyService(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return beautyServiceService.updateService(id, updates)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeautyService(@PathVariable Long id) {
        beautyServiceService.deleteBeautyService(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{beautyServiceId}/masters/{masterId}")
    public ResponseEntity<Void> removeMasterFromBeautyService(@PathVariable Long beautyServiceId, @PathVariable Long masterId) {
        beautyServiceService.removeMasterFromBeautyService(beautyServiceId, masterId);
        return ResponseEntity.noContent().build();
    }
}