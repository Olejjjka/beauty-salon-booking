package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.dto.BeautyServiceDTO;
import com.example.beauty_salon_booking.dto.DTOConverter;
import com.example.beauty_salon_booking.dto.MasterDTO;
import com.example.beauty_salon_booking.entities.BeautyService;
import com.example.beauty_salon_booking.entities.Master;
import com.example.beauty_salon_booking.repositories.BeautyServiceRepository;
import com.example.beauty_salon_booking.repositories.MasterRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.Collections;

@Service
public class BeautyServiceService {

    private final BeautyServiceRepository beautyServiceRepository;
    private final MasterRepository masterRepository;
    private final DTOConverter dtoConverter;
    private final AuthService authService;

    public BeautyServiceService(BeautyServiceRepository beautyServiceRepository,
                                MasterRepository masterRepository,
                                DTOConverter dtoConverter,
                                AuthService authService) {
        this.beautyServiceRepository = beautyServiceRepository;
        this.masterRepository = masterRepository;
        this.dtoConverter = dtoConverter;
        this.authService = authService;
    }

    // для всех клиентов и мастеров
    public List<BeautyServiceDTO> getAllBeautyServices() {
        return beautyServiceRepository.findAll().stream()
                .map(dtoConverter::convertToBeautyServiceDTO)
                .toList();
    }

    // для всех клиентов и мастеров
    public Optional<BeautyServiceDTO> getBeautyServiceById(Long beautyServiceId) {
        return beautyServiceRepository.findById(beautyServiceId).map(dtoConverter::convertToBeautyServiceDTO);
    }

    // для всех клиентов и мастеров
    public Optional<BeautyServiceDTO> getBeautyServiceByName(String name) {
        return beautyServiceRepository.findByName(name).map(dtoConverter::convertToBeautyServiceDTO);
    }

    // для всех клиентов и мастеров
    public List<BeautyServiceDTO> getBeautyServicesByPriceRange(double min, double max) {
        return beautyServiceRepository.findByPriceBetween(min, max).stream()
                .map(dtoConverter::convertToBeautyServiceDTO)
                .toList();
    }

    // для всех клиентов и мастеров
    public List<MasterDTO> getMastersByBeautyServiceId(Long beautyServiceId) {
        return beautyServiceRepository.findById(beautyServiceId)
                .map(beautyService -> beautyService.getMasters().stream()
                        .map(dtoConverter::convertToMasterDTO)
                        .toList()
                )
                .orElse(Collections.emptyList());
    }

    // для всех мастеров
    @Transactional
    public BeautyServiceDTO saveBeautyService(BeautyService beautyService) {
        if (!authService.isCurrentUserMaster()) {
            throw new SecurityException("Access denied: not the authorized master.");
        }
        return dtoConverter.convertToBeautyServiceDTO(beautyServiceRepository.save(beautyService));
    }

    // для всех мастеров
    @Transactional
    public Optional<BeautyServiceDTO> replaceBeautyService(Long beautyServiceId, BeautyService newService) {
        if (!authService.isCurrentUserMaster()) {
            throw new SecurityException("Access denied: not the authorized master.");
        }
        return beautyServiceRepository.findById(beautyServiceId).map(existingService -> {
            existingService.setName(newService.getName());
            existingService.setPrice(newService.getPrice());
            existingService.setDescription(newService.getDescription());
            return dtoConverter.convertToBeautyServiceDTO(beautyServiceRepository.save(existingService));
        });
    }

    // для всех мастеров
    @Transactional
    public Optional<BeautyServiceDTO> updateBeautyService(Long beautyServiceId, Map<String, Object> updates) {
        if (!authService.isCurrentUserMaster()) {
            throw new SecurityException("Access denied: not the authorized master.");
        }
        return beautyServiceRepository.findById(beautyServiceId).map(existingService -> {
            if (updates.containsKey("name")) {
                existingService.setName((String) updates.get("name"));
            }
            if (updates.containsKey("price")) {
                existingService.setPrice((Double) updates.get("price"));
            }
            if (updates.containsKey("description")) {
                existingService.setDescription((String) updates.get("description"));
            }
            return dtoConverter.convertToBeautyServiceDTO(beautyServiceRepository.save(existingService));
        });
    }

    // для всех мастеров
    @Transactional
    public void deleteBeautyService(Long beautyServiceId) {
        if (!authService.isCurrentUserMaster()) {
            throw new SecurityException("Access denied: not the authorized master.");
        }
        beautyServiceRepository.deleteById(beautyServiceId);
    }



    // не надо
    @Transactional
    public BeautyServiceDTO addMasterToBeautyService(Long beautyServiceId, Long masterId) {
        BeautyService beautyService = beautyServiceRepository.findById(beautyServiceId)
                .orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));
        Master master = masterRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException("Master not found"));

        beautyService.addMaster(master);
        return dtoConverter.convertToBeautyServiceDTO(beautyServiceRepository.save(beautyService));
    }

    // не надо
    @Transactional
    public BeautyServiceDTO removeMasterFromBeautyService(Long beautyServiceId, Long masterId) {
        BeautyService beautyService = beautyServiceRepository.findById(beautyServiceId)
                .orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));
        Master master = masterRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException("Master not found"));

        beautyService.removeMaster(master);
        return dtoConverter.convertToBeautyServiceDTO(beautyServiceRepository.save(beautyService));
    }
}