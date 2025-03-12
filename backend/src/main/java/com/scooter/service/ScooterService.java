package com.scooter.service;

import com.scooter.dto.ScooterDTO;
import com.scooter.entity.Scooter;

import java.util.List;

public interface ScooterService {
    ScooterDTO create(ScooterDTO scooterDTO);
    
    ScooterDTO findById(Long id);
    
    List<ScooterDTO> findAll();
    
    List<ScooterDTO> findByStatus(Scooter.ScooterStatus status);
    
    List<ScooterDTO> findAvailableScooters();
    
    List<ScooterDTO> findLowBatteryScooters();
    
    ScooterDTO update(Long id, ScooterDTO scooterDTO);
    
    void delete(Long id);
    
    ScooterDTO updateStatus(Long id, Scooter.ScooterStatus status);
    
    ScooterDTO updateLocation(Long id, double latitude, double longitude);
    
    ScooterDTO updateBatteryLevel(Long id, int batteryLevel);
    
    ScooterDTO markAsMaintained(Long id);
} 