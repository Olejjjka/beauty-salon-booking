package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.entities.Master;
import com.example.beauty_salon_booking.entities.BeautyService;
import com.example.beauty_salon_booking.repositories.MasterRepository;
import com.example.beauty_salon_booking.repositories.BeautyServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
public class MasterService {
    private final MasterRepository masterRepository;
    private final BeautyServiceRepository beautyServiceRepository;
    private final PasswordEncoder passwordEncoder;

    public MasterService(MasterRepository masterRepository, BeautyServiceRepository beautyServiceRepository, PasswordEncoder passwordEncoder) {
        this.masterRepository = masterRepository;
        this.beautyServiceRepository = beautyServiceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Master> getAllMasters() {
        return masterRepository.findAll();
    }

    public Optional<Master> getMasterById(Long id) {
        return masterRepository.findById(id);
    }

    public Optional<Master> getMasterByName(String name) { return masterRepository.findByName(name); }

    public Optional<Master> getMasterByPhone(String phone) {
        return masterRepository.findByPhone(phone);
    }

    public Optional<Master> getMasterByLogin(String login) {
        return masterRepository.findByLogin(login);
    }

    @Transactional
    public Master saveMaster(Master master) {
        master.setPassword(passwordEncoder.encode(master.getPassword()));
        return masterRepository.save(master);
    }

    @Transactional
    public void deleteMaster(Long id) {
        masterRepository.deleteById(id);
    }

    @Transactional
    public Optional<Master> replaceMaster(Long id, Master newMaster) {
        return masterRepository.findById(id).map(existingMaster -> {
            existingMaster.setName(newMaster.getName());
            existingMaster.setPhone(newMaster.getPhone());
            existingMaster.setLogin(newMaster.getLogin());
            existingMaster.setPassword(newMaster.getPassword());
            return masterRepository.save(existingMaster);
        });
    }

    @Transactional
    public Optional<Master> updateMaster(Long id, Map<String, Object> updates) {
        return masterRepository.findById(id).map(existingMaster -> {
            if (updates.containsKey("name")) {
                existingMaster.setName((String) updates.get("name"));
            }
            if (updates.containsKey("phone")) {
                existingMaster.setPhone((String) updates.get("phone"));
            }
            if (updates.containsKey("login")) {
                existingMaster.setLogin((String) updates.get("login"));
            }
            if (updates.containsKey("password")) {
                existingMaster.setPassword(passwordEncoder.encode((String) updates.get("password")));
            }
            return masterRepository.save(existingMaster);
        });
    }

    public List<BeautyService> getBeautyServicesByMasterId(Long masterId) {
        return masterRepository.findById(masterId)
                .map(Master::getBeautyServices)
                .orElse(Collections.emptyList());
    }

    @Transactional
    public Master addBeautyServiceToMaster(Long masterId, Long beautyServiceId) {
        Master master = masterRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException("Master not found"));
        BeautyService beautyService = beautyServiceRepository.findById(beautyServiceId)
                .orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));

        master.addBeautyService(beautyService);
        return masterRepository.save(master);
    }

    @Transactional
    public Master removeBeautyServiceFromMaster(Long masterId, Long beautyServiceId) {
        Master master = masterRepository.findById(masterId).
                orElseThrow(() -> new EntityNotFoundException("Master not found"));
        BeautyService beautyService = beautyServiceRepository.findById(beautyServiceId).
                orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));

        master.removeBeautyService(beautyService);
        return masterRepository.save(master);
    }
}