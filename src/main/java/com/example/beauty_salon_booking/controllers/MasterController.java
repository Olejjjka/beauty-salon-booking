package com.example.beauty_salon_booking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.example.beauty_salon_booking.entities.Master;
import com.example.beauty_salon_booking.services.MasterService;

@RestController
@RequestMapping("/masters")
public class MasterController {
    private final MasterService masterService;

    @Autowired
    public MasterController(MasterService masterService) {
        this.masterService = masterService;
    }

    @PostMapping("/register")
    public ResponseEntity<Master> createMaster(@RequestBody Master master) {
        return ResponseEntity.status(HttpStatus.CREATED).body(masterService.saveMaster(master));
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
}
