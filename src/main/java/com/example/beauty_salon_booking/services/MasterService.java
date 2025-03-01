package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.entities.Master;
import com.example.beauty_salon_booking.repositories.MasterRepository;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
public class MasterService {
    private final MasterRepository masterRepository;
    private final PasswordEncoder passwordEncoder;

    public MasterService(MasterRepository masterRepository, PasswordEncoder passwordEncoder) {
        this.masterRepository = masterRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Master> getAllMasters() {
        return masterRepository.findAll();
    }

    public Optional<Master> getMasterById(Long id) {
        return masterRepository.findById(id);
    }

    public Optional<Master> getMasterByPhone(String phone) {
        return masterRepository.findByPhone(phone);
    }

    public Optional<Master> getMasterByLogin(String login) {
        return masterRepository.findByLogin(login);
    }

    //////////////
    public Master saveMaster(Master master) {
        master.setPassword(passwordEncoder.encode(master.getPassword()));
        return masterRepository.save(master);
    }
    //////////////

    public void deleteMaster(Long id) {
        masterRepository.deleteById(id);
    }

    public Optional<Master> replaceMaster(Long id, Master newMaster) {
        return masterRepository.findById(id).map(existingMaster -> {
            existingMaster.setName(newMaster.getName());
            existingMaster.setPhone(newMaster.getPhone());
            existingMaster.setLogin(newMaster.getLogin());
            existingMaster.setPassword(newMaster.getPassword());
            return masterRepository.save(existingMaster);
        });
    }

    // PATCH method: Update only provided fields
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
                existingMaster.setPassword((String) updates.get("password"));
            }
            return masterRepository.save(existingMaster);
        });
    }
}
