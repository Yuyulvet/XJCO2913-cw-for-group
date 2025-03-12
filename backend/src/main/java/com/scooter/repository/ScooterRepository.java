package com.scooter.repository;

import com.scooter.entity.Scooter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScooterRepository extends JpaRepository<Scooter, Long> {
    List<Scooter> findByStatus(Scooter.ScooterStatus status);
    
    @Query("SELECT s FROM Scooter s WHERE s.batteryLevel <= 20")
    List<Scooter> findLowBatteryScooters();
    
    @Query("SELECT s FROM Scooter s WHERE s.status = 'AVAILABLE' AND " +
           "s.batteryLevel >= 20 ORDER BY s.lastMaintained DESC")
    List<Scooter> findAvailableScooters();
} 