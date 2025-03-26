package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.dto.BeautyServiceDTO;
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

    public BeautyServiceService(BeautyServiceRepository beautyServiceRepository, MasterRepository masterRepository) {
        this.beautyServiceRepository = beautyServiceRepository;
        this.masterRepository = masterRepository;
    }

    public List<BeautyServiceDTO> getAllBeautyServices() {
        return beautyServiceRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Optional<BeautyServiceDTO> getBeautyServiceById(Long id) {
        return beautyServiceRepository.findById(id).map(this::convertToDTO);
    }

    public Optional<BeautyServiceDTO> getBeautyServiceByName(String name) {
        return beautyServiceRepository.findByName(name).map(this::convertToDTO);
    }

    public List<BeautyServiceDTO> getBeautyServicesByPriceRange(double min, double max) {
        return beautyServiceRepository.findByPriceBetween(min, max).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional
    public BeautyServiceDTO saveBeautyService(BeautyService beautyService) {
        return convertToDTO(beautyServiceRepository.save(beautyService));
    }

    @Transactional
    public void deleteBeautyService(Long id) {
        beautyServiceRepository.deleteById(id);
    }

    @Transactional
    public Optional<BeautyServiceDTO> replaceService(Long id, BeautyService newService) {
        return beautyServiceRepository.findById(id).map(existingService -> {
            existingService.setName(newService.getName());
            existingService.setPrice(newService.getPrice());
            existingService.setDescription(newService.getDescription());
            return convertToDTO(beautyServiceRepository.save(existingService));
        });
    }

    @Transactional
    public Optional<BeautyServiceDTO> updateService(Long id, Map<String, Object> updates) {
        return beautyServiceRepository.findById(id).map(existingService -> {
            if (updates.containsKey("name")) {
                existingService.setName((String) updates.get("name"));
            }
            if (updates.containsKey("price")) {
                existingService.setPrice((Double) updates.get("price"));
            }
            if (updates.containsKey("description")) {
                existingService.setDescription((String) updates.get("description"));
            }
            return convertToDTO(beautyServiceRepository.save(existingService));
        });
    }

    ///
    public List<Master> getMastersByBeautyServiceId(Long beautyServiceId) {
        return beautyServiceRepository.findById(beautyServiceId)
                .map(BeautyService::getMasters)
                .orElse(Collections.emptyList());
    }

    @Transactional
    public BeautyServiceDTO addMasterToBeautyService(Long beautyServiceId, Long masterId) {
        BeautyService beautyService = beautyServiceRepository.findById(beautyServiceId)
                .orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));
        Master master = masterRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException("Master not found"));

        beautyService.addMaster(master);
        return convertToDTO(beautyServiceRepository.save(beautyService));
    }

    @Transactional
    public BeautyServiceDTO removeMasterFromBeautyService(Long beautyServiceId, Long masterId) {
        BeautyService beautyService = beautyServiceRepository.findById(beautyServiceId)
                .orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));
        Master master = masterRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException("Master not found"));

        beautyService.removeMaster(master);
        return convertToDTO(beautyServiceRepository.save(beautyService));
    }

    private BeautyServiceDTO convertToDTO(BeautyService beautyService) {
        List<MasterDTO> masterDTOs = beautyService.getMasters().stream()
                .map(master -> new MasterDTO(
                        master.getId(),
                        master.getName(),
                        master.getPhone()
                        //master.getBeautyServices().stream() // Используем SimpleMasterDTO
                         //       .map(m -> new BeautyServiceDTO(m.getId(), m.getName(), m.getPrice(), m.getDescription()))
                        //        .toList()))
                ))
                .toList();

        return new BeautyServiceDTO(
                beautyService.getId(),
                beautyService.getName(),
                beautyService.getPrice(),
                beautyService.getDescription(),
                masterDTOs);
    }

}