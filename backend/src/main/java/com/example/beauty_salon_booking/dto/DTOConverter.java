package com.example.beauty_salon_booking.dto;

import com.example.beauty_salon_booking.entities.Appointment;
import com.example.beauty_salon_booking.entities.BeautyService;
import com.example.beauty_salon_booking.entities.Client;
import com.example.beauty_salon_booking.entities.Master;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DTOConverter {

    public ClientDTO convertToClientDTO(Client client) {
        return new ClientDTO(
                client.getId(),
                client.getName(),
                client.getPhone());
    }

    public MasterDTO convertToMasterDTO(Master master) {
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

    public BeautyServiceDTO convertToBeautyServiceDTO(BeautyService beautyService) {
        List<MasterDTO> masterDTOs = beautyService.getMasters().stream()
                .map(master -> new MasterDTO(
                        master.getId(),
                        master.getName(),
                        master.getPhone()
                ))
                .toList();

        return new BeautyServiceDTO(
                beautyService.getId(),
                beautyService.getName(),
                beautyService.getPrice(),
                beautyService.getDescription(),
                masterDTOs);
    }

    public AppointmentDTO convertToAppointmentDTO(Appointment appointment) {
        ClientDTO clientDTO = new ClientDTO(
                appointment.getClient().getId(),
                appointment.getClient().getName(),
                appointment.getClient().getPhone());

        MasterDTO masterDTO = new MasterDTO(
                appointment.getMaster().getId(),
                appointment.getMaster().getName(),
                appointment.getMaster().getPhone());

        BeautyServiceDTO beautyServiceDTO = new BeautyServiceDTO(
                appointment.getBeautyService().getId(),
                appointment.getBeautyService().getName(),
                appointment.getBeautyService().getPrice(),
                appointment.getBeautyService().getDescription());

        return new AppointmentDTO(
                appointment.getId(),
                clientDTO,
                masterDTO,
                beautyServiceDTO,
                appointment.getDate(),
                appointment.getTime(),
                appointment.getStatus());
    }
}