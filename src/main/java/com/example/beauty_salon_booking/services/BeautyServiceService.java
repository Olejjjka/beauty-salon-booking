package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.entities.BeautyService;
import com.example.beauty_salon_booking.entities.Master;
import com.example.beauty_salon_booking.repositories.BeautyServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.Collections;

@Service
public class BeautyServiceService {
    private final BeautyServiceRepository beautyServiceRepository;

    public BeautyServiceService(BeautyServiceRepository beautyServiceRepository) {
        this.beautyServiceRepository = beautyServiceRepository;
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

    public BeautyService saveBeautyService(BeautyService beautyService) {
        return beautyServiceRepository.save(beautyService);
    }

    public void deleteBeautyService(Long id) {
        beautyServiceRepository.deleteById(id);
    }

    public Optional<BeautyService> replaceService(Long id, BeautyService newService) {
        return beautyServiceRepository.findById(id).map(existingService -> {
            existingService.setName(newService.getName());
            existingService.setPrice(newService.getPrice());
            return beautyServiceRepository.save(existingService);
        });
    }

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

    public BeautyService addMasterToBeautyService(Long serviceId, Master master) {
        return beautyServiceRepository.findById(serviceId).map(service -> {
            service.getMasters().add(master);
            return beautyServiceRepository.save(service);
        }).orElseThrow(() -> new RuntimeException("Beauty Service not found"));
    }

    public BeautyService removeMasterFromBeautyService(Long serviceId, Master master) {
        return beautyServiceRepository.findById(serviceId).map(service -> {
            service.getMasters().remove(master);
            return beautyServiceRepository.save(service);
        }).orElseThrow(() -> new RuntimeException("Beauty Service not found"));
    }
}