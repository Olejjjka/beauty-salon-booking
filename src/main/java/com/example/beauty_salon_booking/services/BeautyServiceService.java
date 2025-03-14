package com.example.beauty_salon_booking.services;

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

    public List<BeautyService> getAllBeautyServices() {
        return beautyServiceRepository.findAll();
    }

    public Optional<BeautyService> getBeautyServiceById(Long id) {
        return beautyServiceRepository.findById(id);
    }

    public Optional<BeautyService> getBeautyServiceByName(String name) {
        return beautyServiceRepository.findByName(name);
    }

    public List<BeautyService> getBeautyServicesByPriceRange(double min, double max) {
        return beautyServiceRepository.findByPriceBetween(min, max);
    }

    @Transactional
    public BeautyService saveBeautyService(BeautyService beautyService) {
        return beautyServiceRepository.save(beautyService);
    }

    @Transactional
    public void deleteBeautyService(Long id) {
        beautyServiceRepository.deleteById(id);
    }

    @Transactional
    public Optional<BeautyService> replaceService(Long id, BeautyService newService) {
        return beautyServiceRepository.findById(id).map(existingService -> {
            existingService.setName(newService.getName());
            existingService.setPrice(newService.getPrice());
            return beautyServiceRepository.save(existingService);
        });
    }

    @Transactional
    public Optional<BeautyService> updateService(Long id, Map<String, Object> updates) {
        return beautyServiceRepository.findById(id).map(existingService -> {
            if (updates.containsKey("name")) {
                existingService.setName((String) updates.get("name"));
            }
            if (updates.containsKey("price")) {
                existingService.setPrice((Double) updates.get("price"));
            }
            return beautyServiceRepository.save(existingService);
        });
    }

    public List<Master> getMastersByBeautyServiceId(Long serviceId) {
        return beautyServiceRepository.findById(serviceId)
                .map(BeautyService::getMasters)
                .orElse(Collections.emptyList());
    }

    @Transactional
    public BeautyService addMasterToBeautyService(Long beautyServiceId, Long masterId) {
        BeautyService beautyService = beautyServiceRepository.findById(beautyServiceId)
                .orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));
        Master master = masterRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException("Master not found"));

        beautyService.addMaster(master);
        return beautyServiceRepository.save(beautyService);
    }

    @Transactional
    public BeautyService removeMasterFromBeautyService(Long beautyServiceId, Long masterId) {
        BeautyService beautyService = beautyServiceRepository.findById(beautyServiceId)
                .orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));
        Master master = masterRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException("Master not found"));

        beautyService.removeMaster(master);
        return beautyServiceRepository.save(beautyService);
    }
}