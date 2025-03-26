package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.dto.BeautyServiceDTO;
import com.example.beauty_salon_booking.dto.ClientDTO;
import com.example.beauty_salon_booking.dto.MasterDTO;
import com.example.beauty_salon_booking.entities.Client;
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
import java.util.stream.Collectors;

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

    public List<MasterDTO> getAllMasters() {
        return masterRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Optional<MasterDTO> getMasterById(Long id) {
        return masterRepository.findById(id).map(this::convertToDTO);
    }

    public Optional<MasterDTO> getMasterByName(String name) {
        return masterRepository.findByName(name).map(this::convertToDTO);
    }

    public Optional<MasterDTO> getMasterByPhone(String phone) {
        return masterRepository.findByPhone(phone).map(this::convertToDTO);
    }

    public Optional<MasterDTO> getMasterByLogin(String login) {
        return masterRepository.findByLogin(login).map(this::convertToDTO);
    }

    @Transactional
    public MasterDTO saveMaster(Master master) {
        master.setPassword(passwordEncoder.encode(master.getPassword()));
        return convertToDTO(masterRepository.save(master));
    }

    @Transactional
    public void deleteMaster(Long id) {
        masterRepository.deleteById(id);
    }

    @Transactional
    public Optional<MasterDTO> replaceMaster(Long id, Master newMaster) {
        return masterRepository.findById(id).map(existingMaster -> {
            existingMaster.setName(newMaster.getName());
            existingMaster.setPhone(newMaster.getPhone());
            existingMaster.setLogin(newMaster.getLogin());
            existingMaster.setPassword(newMaster.getPassword());
            return convertToDTO(masterRepository.save(existingMaster));
        });
    }

    @Transactional
    public Optional<MasterDTO> updateMaster(Long id, Map<String, Object> updates) {
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
            return convertToDTO(masterRepository.save(existingMaster));
        });
    }

    ///
    public List<BeautyService> getBeautyServicesByMasterId(Long masterId) {
        return masterRepository.findById(masterId)
                .map(Master::getBeautyServices)
                .orElse(Collections.emptyList());
    }

    @Transactional
    public MasterDTO addBeautyServiceToMaster(Long masterId, Long beautyServiceId) {
        Master master = masterRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException("Master not found"));
        BeautyService beautyService = beautyServiceRepository.findById(beautyServiceId)
                .orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));

        master.addBeautyService(beautyService);
        return convertToDTO(masterRepository.save(master));
    }

    @Transactional
    public MasterDTO removeBeautyServiceFromMaster(Long masterId, Long beautyServiceId) {
        Master master = masterRepository.findById(masterId).
                orElseThrow(() -> new EntityNotFoundException("Master not found"));
        BeautyService beautyService = beautyServiceRepository.findById(beautyServiceId).
                orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));

        master.removeBeautyService(beautyService);
        return convertToDTO(masterRepository.save(master));
    }

    private MasterDTO convertToDTO(Master master) {
        List<BeautyServiceDTO> beautyServiceDTOs = master.getBeautyServices().stream()
                .map(beautyService -> new BeautyServiceDTO(
                        beautyService.getId(),
                        beautyService.getName(),
                        beautyService.getPrice(),
                        beautyService.getDescription()
                ))
                .toList();

        return new MasterDTO(
                master.getId(),
                master.getName(),
                master.getPhone(),
                beautyServiceDTOs);
    }

    private Client convertToEntity(ClientDTO dto) {
        Client client = new Client();
        client.setName(dto.getName());
        client.setPhone(dto.getPhone());
        return client;
    }
}