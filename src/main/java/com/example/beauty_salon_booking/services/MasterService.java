package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.dto.*;
import com.example.beauty_salon_booking.entities.Master;
import com.example.beauty_salon_booking.entities.BeautyService;
import com.example.beauty_salon_booking.repositories.AppointmentRepository;
import com.example.beauty_salon_booking.repositories.MasterRepository;
import com.example.beauty_salon_booking.repositories.BeautyServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MasterService {

    private final MasterRepository masterRepository;
    private final BeautyServiceRepository beautyServiceRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final DTOConverter dtoConverter;
    private final AuthService authService;

    @Autowired
    public MasterService(MasterRepository masterRepository,
                         BeautyServiceRepository beautyServiceRepository,
                         AppointmentRepository appointmentRepository,
                         PasswordEncoder passwordEncoder,
                         DTOConverter dtoConverter,
                         AuthService authService) {
        this.masterRepository = masterRepository;
        this.beautyServiceRepository = beautyServiceRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.dtoConverter = dtoConverter;
        this.authService = authService;
    }

    // для всех клиентов и мастеров
    public List<MasterDTO> getAllMasters() {
        return masterRepository.findAll().stream()
                .map(dtoConverter::convertToMasterDTO)
                .toList();
    }

    // для всех клиентов и мастеров
    public Optional<MasterDTO> getMasterById(Long masterId) {
        return masterRepository.findById(masterId).map(dtoConverter::convertToMasterDTO);
    }

    // для всех клиентов и мастеров
    public Optional<MasterDTO> getMasterByName(String name) {
        return masterRepository.findByName(name).map(dtoConverter::convertToMasterDTO);
    }

    // не надо
    public Optional<MasterDTO> getMasterByPhone(String phone) {
        return masterRepository.findByPhone(phone).map(dtoConverter::convertToMasterDTO);
    }

    // не надо
    public Optional<MasterDTO> getMasterByLogin(String login) {
        return masterRepository.findByLogin(login).map(dtoConverter::convertToMasterDTO);
    }

    // без ограничений
    @Transactional
    public MasterDTO saveMaster(Master master) {
        master.setPassword(passwordEncoder.encode(master.getPassword()));
        return dtoConverter.convertToMasterDTO(masterRepository.save(master));
    }

    // для причастного мастера
    @Transactional
    public void deleteMaster(Long masterId) {
        authService.checkAccessToMaster(masterId);
        masterRepository.deleteById(masterId);
    }

    // для причастного мастера
    @Transactional
    public Optional<MasterDTO> replaceMaster(Long masterId, Master newMaster) {
        authService.checkAccessToMaster(masterId);
        return masterRepository.findById(masterId).map(existingMaster -> {
            existingMaster.setName(newMaster.getName());
            existingMaster.setPhone(newMaster.getPhone());
            existingMaster.setLogin(newMaster.getLogin());
            existingMaster.setPassword(newMaster.getPassword());
            return dtoConverter.convertToMasterDTO(masterRepository.save(existingMaster));
        });
    }

    // для причастного мастера
    @Transactional
    public Optional<MasterDTO> updateMaster(Long masterId, Map<String, Object> updates) {
        authService.checkAccessToMaster(masterId);
        return masterRepository.findById(masterId).map(existingMaster -> {
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
            return dtoConverter.convertToMasterDTO(masterRepository.save(existingMaster));
        });
    }

    // для всех клиентов и мастеров
    public List<BeautyServiceDTO> getBeautyServicesByMasterId(Long masterId) {
        return masterRepository.findById(masterId)
                .map(master -> master.getBeautyServices().stream()
                        .map(dtoConverter::convertToBeautyServiceDTO)
                        .toList()
                )
                .orElse(Collections.emptyList());
    }

    // для причастного мастера
    public List<AppointmentDTO> getAppointmentsByMasterId(Long masterId) {
        authService.checkAccessToMaster(masterId);
        return appointmentRepository.findByMasterId(masterId).stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    // для причастного мастера
    @Transactional
    public MasterDTO addBeautyServiceToMaster(Long masterId, Long beautyServiceId) {
        authService.checkAccessToMaster(masterId);
        Master master = masterRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException("Master not found"));
        BeautyService beautyService = beautyServiceRepository.findById(beautyServiceId)
                .orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));

        master.addBeautyService(beautyService);
        return dtoConverter.convertToMasterDTO(masterRepository.save(master));
    }

    // для причастного мастера
    @Transactional
    public MasterDTO removeBeautyServiceFromMaster(Long masterId, Long beautyServiceId) {
        authService.checkAccessToMaster(masterId);
        Master master = masterRepository.findById(masterId).
                orElseThrow(() -> new EntityNotFoundException("Master not found"));
        BeautyService beautyService = beautyServiceRepository.findById(beautyServiceId).
                orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));

        master.removeBeautyService(beautyService);
        return dtoConverter.convertToMasterDTO(masterRepository.save(master));
    }

    private List<AvailableTimeSlotDTO> calculateAvailableSlots(List<AppointmentDTO> appointments) {
        LocalTime startOfDay = LocalTime.of(9, 0);
        LocalTime endOfDay = LocalTime.of(19, 0);
        Duration serviceDuration = Duration.ofHours(2);
        Duration breakDuration = Duration.ofMinutes(30);

        List<AvailableTimeSlotDTO> availableTimeSlots = new ArrayList<>();

        if (appointments.isEmpty()) {
            LocalTime currentStart = startOfDay;
            while (currentStart.plus(serviceDuration).isBefore(endOfDay) || currentStart.plus(serviceDuration).equals(endOfDay)) {
                LocalTime currentEnd = currentStart.plus(serviceDuration);
                availableTimeSlots.add(new AvailableTimeSlotDTO(currentStart, currentEnd));
                currentStart = currentEnd.plus(breakDuration);
            }
            return availableTimeSlots;
        }

        List<AvailableTimeSlotDTO> busyIntervals = appointments.stream()
                .map(appointment -> new AvailableTimeSlotDTO(appointment.getTime(), appointment.getTime().plusHours(2)))
                .sorted(Comparator.comparing(AvailableTimeSlotDTO::getStart))
                .collect(Collectors.toList());

        LocalTime currentStart = startOfDay;

        for (AvailableTimeSlotDTO busyInterval : busyIntervals) {
            while (currentStart.plus(serviceDuration).isBefore(busyInterval.getStart()) || currentStart.plus(serviceDuration).equals(busyInterval.getStart())) {
                LocalTime currentEnd = currentStart.plus(serviceDuration);
                availableTimeSlots.add(new AvailableTimeSlotDTO(currentStart, currentEnd));
                currentStart = currentEnd.plus(breakDuration);
            }

            currentStart = busyInterval.getEnd().plus(breakDuration);
        }

        while (currentStart.plus(serviceDuration).isBefore(endOfDay) || currentStart.plus(serviceDuration).equals(endOfDay)) {
            LocalTime currentEnd = currentStart.plus(serviceDuration);
            availableTimeSlots.add(new AvailableTimeSlotDTO(currentStart, currentEnd));
            currentStart = currentEnd.plus(breakDuration);
        }

        return availableTimeSlots;
    }

    // для всех клиентов и мастеров
    public Map<LocalDate, List<AvailableTimeSlotDTO>> getAvailableTimeSlots(Long masterId, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, List<AvailableTimeSlotDTO>> availableSlotsByDate = new LinkedHashMap<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            LocalDate finalDate = currentDate;
            List<AppointmentDTO> appointments = appointmentRepository.findByMasterId(masterId)
                    .stream()
                    .map(dtoConverter::convertToAppointmentDTO)
                    .filter(appointment -> appointment.getDate().isEqual(finalDate))
                    .collect(Collectors.toList());

            List<AvailableTimeSlotDTO> availableSlots = calculateAvailableSlots(appointments);
            availableSlotsByDate.put(currentDate, availableSlots);

            currentDate = currentDate.plusDays(1);
        }

        return availableSlotsByDate;
    }
}