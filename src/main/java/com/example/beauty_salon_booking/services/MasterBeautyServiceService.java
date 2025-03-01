package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.entities.MasterBeautyService;
import com.example.beauty_salon_booking.repositories.MasterBeautyServiceRepository;
import com.example.beauty_salon_booking.repositories.MasterRepository;
import com.example.beauty_salon_booking.repositories.BeautyServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MasterBeautyServiceService {
    private final MasterBeautyServiceRepository masterBeautyServiceRepository;
    private final MasterRepository masterRepository;
    private final BeautyServiceRepository beautyServiceRepository;

    public MasterBeautyServiceService(
            MasterBeautyServiceRepository masterBeautyServiceRepository,
            MasterRepository masterRepository,
            BeautyServiceRepository beautyServiceRepository
    ) {
        this.masterBeautyServiceRepository = masterBeautyServiceRepository;
        this.masterRepository = masterRepository;
        this.beautyServiceRepository = beautyServiceRepository;
    }

    public List<MasterBeautyService> getAllMasterBeautyServices() {
        return masterBeautyServiceRepository.findAll();
    }

    public List<MasterBeautyService> getBeautyServicesByMasterId(Long masterId) {
        return masterBeautyServiceRepository.findByMasterId(masterId);
    }

    public List<MasterBeautyService> getMastersByBeautyServiceId(Long beautyServiceId) {
        return masterBeautyServiceRepository.findByBeautyServiceId(beautyServiceId);
    }

    public Optional<MasterBeautyService> getMasterBeautyService(Long masterId, Long beautyServiceId) {
        return masterBeautyServiceRepository.findByMasterIdAndBeautyServiceId(masterId, beautyServiceId)
                .stream().findFirst();
    }

    public MasterBeautyService saveMasterBeautyService(MasterBeautyService masterBeautyService) {
        return masterBeautyServiceRepository.save(masterBeautyService);
    }

    public void deleteMasterBeautyService(Long id) {
        masterBeautyServiceRepository.deleteById(id);
    }

    public Optional<MasterBeautyService> replaceMasterBeautyService(Long id, MasterBeautyService newMasterBeautyService) {
        return masterBeautyServiceRepository.findById(id).map(existingMasterBeautyService -> {
            existingMasterBeautyService.setMaster(newMasterBeautyService.getMaster());
            existingMasterBeautyService.setBeautyService(newMasterBeautyService.getBeautyService());
            return masterBeautyServiceRepository.save(existingMasterBeautyService);
        });
    }

    public Optional<MasterBeautyService> updateMasterBeautyService(Long id, Map<String, Object> updates) {
        return masterBeautyServiceRepository.findById(id).map(existingMasterBeautyService -> {
            if (updates.containsKey(("masterId"))) {
                Long masterId = ((Number) updates.get("masterId")).longValue();
                masterRepository.findById(masterId).ifPresent(existingMasterBeautyService::setMaster);
            }
            if (updates.containsKey("beautyServiceId")) {
                Long serviceId = ((Number) updates.get("beautyServiceId")).longValue();
                beautyServiceRepository.findById(serviceId).ifPresent(existingMasterBeautyService::setBeautyService);
            }
            return masterBeautyServiceRepository.save(existingMasterBeautyService);
        });
    }
}