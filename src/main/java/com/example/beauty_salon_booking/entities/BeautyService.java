package com.example.beauty_salon_booking.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "beauty_services")
public class BeautyService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "beauty_service_name", nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Double price;

    @ManyToMany(mappedBy = "beautyServices", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Master> masters;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<Master> getMasters() {
        return masters;
    }

    public void setMasters(List<Master> masters) {
        this.masters = masters;
    }

    public void addMaster(Master master) {
        if (!masters.contains(master)) {
            masters.add(master);
            master.getBeautyServices().add(this);
        }
    }

    public void removeMaster(Master master) {
        if (masters.contains(master)) {
            masters.remove(master);
            master.getBeautyServices().remove(this);
        }
    }
}