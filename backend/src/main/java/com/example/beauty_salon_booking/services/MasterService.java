package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.dto.*;
import com.example.beauty_salon_booking.entities.Appointment;
import com.example.beauty_salon_booking.entities.Master;
import com.example.beauty_salon_booking.entities.BeautyService;
import com.example.beauty_salon_booking.enums.AppointmentStatus;
import com.example.beauty_salon_booking.repositories.AppointmentRepository;
import com.example.beauty_salon_booking.repositories.MasterRepository;
import com.example.beauty_salon_booking.repositories.BeautyServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

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
    private final RevokedTokenService revokedTokenService;
    private final UserValidationService userValidationService;

    @Autowired
    public MasterService(MasterRepository masterRepository,
                         BeautyServiceRepository beautyServiceRepository,
                         AppointmentRepository appointmentRepository,
                         PasswordEncoder passwordEncoder,
                         DTOConverter dtoConverter,
                         AuthService authService,
                         RevokedTokenService revokedTokenService,
                         UserValidationService userValidationService) {
        this.masterRepository = masterRepository;
        this.beautyServiceRepository = beautyServiceRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.dtoConverter = dtoConverter;
        this.authService = authService;
        this.revokedTokenService = revokedTokenService;
        this.userValidationService = userValidationService;
    }

    // без ограничений
    @Transactional
    public MasterDTO saveMaster(Master master) {
        userValidationService.validateAll(master.getLogin(), master.getPassword(), master.getName(), master.getPhone());
        master.setPassword(passwordEncoder.encode(master.getPassword()));
        return dtoConverter.convertToMasterDTO(masterRepository.save(master));
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

    // для всех клиентов и мастеров
    public List<BeautyServiceDTO> getBeautyServicesByMasterId(Long masterId) {
        return masterRepository.findById(masterId)
                .map(master -> master.getBeautyServices().stream()
                        .map(dtoConverter::convertToBeautyServiceDTO)
                        .toList()
                )
                .orElseThrow(() -> new EntityNotFoundException("Master not found"));
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
                .toList();

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

        // Получаем мастера
        Master master = masterRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException("Master not found"));

        // Получаем услугу
        BeautyService beautyService = beautyServiceRepository.findById(beautyServiceId)
                .orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));

        // Проверяем, привязана ли уже услуга к данному мастеру
        if (master.getBeautyServices().contains(beautyService)) {
            throw new IllegalStateException("This beauty service is already assigned to this master.");
        }

        // Добавляем услугу мастеру
        master.addBeautyService(beautyService);

        // Сохраняем изменения и возвращаем обновленного мастера
        return dtoConverter.convertToMasterDTO(masterRepository.save(master));
    }


    // для причастного мастера
    @Transactional
    public MasterDTO removeBeautyServiceFromMaster(Long masterId, Long beautyServiceId) {
        authService.checkAccessToMaster(masterId);

        // Получаем мастера
        Master master = masterRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException("Master not found"));

        // Получаем услугу
        BeautyService beautyService = beautyServiceRepository.findById(beautyServiceId)
                .orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));

        // Проверяем, привязана ли услуга к данному мастеру
        if (!master.getBeautyServices().contains(beautyService)) {
            throw new IllegalStateException("This beauty service is not assigned to this master.");
        }

        master.removeBeautyService(beautyService);

        return dtoConverter.convertToMasterDTO(masterRepository.save(master));
    }

    @Transactional
    public void updateAppointmentStatus(Long appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));

        authService.checkAccessFromMasterToAppointment(appointment);

        AppointmentStatus newStatus = AppointmentStatus.valueOf(status);
        appointment.setStatus(newStatus);
        appointmentRepository.save(appointment);
    }

    // для причастного мастера
    @Transactional
    public void updateMaster(Long masterId, Map<String, Object> updates) {
        authService.checkAccessToMaster(masterId);

        Master master = masterRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException("Мастер не найден"));

        if (updates.containsKey("name")) {
            String username = (String) updates.get("name");
            userValidationService.validateName(username);
            master.setName(username);
        }
        if (updates.containsKey("phone")) {
            String phone = (String) updates.get("phone");
            userValidationService.validatePhone(phone);
            master.setPhone(phone);
        }
        if (updates.containsKey("login")) {
            String login = (String) updates.get("login");
            userValidationService.validateLogin(login);
            master.setLogin(login);
        }

        masterRepository.save(master);
    }

    // для причастного мастера
    @Transactional
    public void changePassword(Long masterId, String oldPassword, String newPassword) {
        authService.checkAccessToMaster(masterId);

        Master master = masterRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException ("Master not found"));

        if (!passwordEncoder.matches(oldPassword, master.getPassword())) {
            throw new RuntimeException("Указан неверный текущий пароль");
        }

        userValidationService.validatePassword(newPassword);
        master.setPassword(passwordEncoder.encode(newPassword));
        masterRepository.save(master);

        // Отзыв токена после смены пароля
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            revokedTokenService.revokeToken(token);
        }
    }

    // для причастного мастера
    @Transactional
    public void deleteMaster(Long masterId) {
        authService.checkAccessToMaster(masterId);
        masterRepository.deleteById(masterId);
    }



    // не надо
    public Optional<MasterDTO> getMasterByPhone(String phone) {
        return masterRepository.findByPhone(phone).map(dtoConverter::convertToMasterDTO);
    }

    // не надо
    public Optional<MasterDTO> getMasterByLogin(String login) {
        return masterRepository.findByLogin(login).map(dtoConverter::convertToMasterDTO);
    }

    // не надо (для причастного мастера)
    @Transactional
    public Optional<MasterDTO> replaceMaster(Long masterId, Master newMaster) {
        authService.checkAccessToMaster(masterId);
        userValidationService.validateAll(newMaster.getLogin(), newMaster.getPassword(), newMaster.getName(), newMaster.getPhone());
        return masterRepository.findById(masterId).map(existingMaster -> {
            existingMaster.setName(newMaster.getName());
            existingMaster.setPhone(newMaster.getPhone());
            existingMaster.setLogin(newMaster.getLogin());
            existingMaster.setPassword(newMaster.getPassword());
            return dtoConverter.convertToMasterDTO(masterRepository.save(existingMaster));
        });
    }
}