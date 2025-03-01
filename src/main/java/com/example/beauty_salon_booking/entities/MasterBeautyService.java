package com.example.beauty_salon_booking.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "master_beauty_services")
public class MasterBeautyService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "master_id", nullable = false)
    private Master master;

    @ManyToOne
    @JoinColumn(name = "beauty_service_id", nullable = false)
    private BeautyService beautyService;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Master getMaster() {
        return master;
    }

    public void setMaster(Master master) {
        this.master = master;
    }

    public BeautyService getBeautyService() {
        return beautyService;
    }

    public void setBeautyService(BeautyService beautyService) {
        this.beautyService = beautyService;
    }
}
