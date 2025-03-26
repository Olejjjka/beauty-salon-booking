package com.example.beauty_salon_booking.dto;

import java.util.List;

public class MasterDTO {
    private Long id;
    private String name;
    private String phone;
    private List<BeautyServiceDTO> beautyServices;

    public MasterDTO(Long id, String name, String phone, List<BeautyServiceDTO> beautyServices) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.beautyServices = beautyServices;
    }

    public MasterDTO(Long id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.beautyServices = null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<BeautyServiceDTO> getBeautyServices() {
        return beautyServices;
    }
}