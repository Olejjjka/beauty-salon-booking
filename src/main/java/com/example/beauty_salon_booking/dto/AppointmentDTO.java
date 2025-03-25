package com.example.beauty_salon_booking.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentDTO {
    private Long id;
    private ClientDTO client;
    private MasterDTO master;
    private BeautyServiceDTO beautyService;
    private LocalDate date;
    private LocalTime time;
    private String status;

    public AppointmentDTO(Long id, ClientDTO client, MasterDTO master, BeautyServiceDTO beautyService, LocalDate date, LocalTime time, String status) {
        this.id = id;
        this.client = client;
        this.master = master;
        this.beautyService = beautyService;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ClientDTO getClient() { return client; }
    public void setClient(ClientDTO client) { this.client = client; }

    public MasterDTO getMaster() { return master; }
    public void setMaster(MasterDTO master) { this.master = master; }

    public BeautyServiceDTO getBeautyService() { return beautyService; }
    public void setBeautyService(BeautyServiceDTO beautyService) { this.beautyService = beautyService; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}