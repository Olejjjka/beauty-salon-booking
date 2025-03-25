package com.example.beauty_salon_booking.dto;

import com.example.beauty_salon_booking.entities.Master;

import java.util.List;

public class BeautyServiceDTO {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private List<MasterDTO> masters;

    public BeautyServiceDTO(Long id, String name, Double price, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<MasterDTO> getMasters() { return masters; }
}