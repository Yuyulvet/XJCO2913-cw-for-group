package com.scooter.repository;

import com.scooter.entity.PricingConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PricingConfigRepository extends JpaRepository<PricingConfig, Long> {
    @Query("SELECT p FROM PricingConfig p WHERE p.isActive = true AND " +
           "(p.effectiveFrom IS NULL OR p.effectiveFrom <= CURRENT_TIMESTAMP) AND " +
           "(p.effectiveTo IS NULL OR p.effectiveTo >= CURRENT_TIMESTAMP)")
    Optional<PricingConfig> findCurrentActiveConfig();
    
    boolean existsByNameAndIdNot(String name, Long id);
    
    @Query("SELECT p FROM PricingConfig p WHERE " +
           "p.effectiveFrom <= :dateTime AND " +
           "(p.effectiveTo IS NULL OR p.effectiveTo >= :dateTime) AND " +
           "p.isActive = true")
    Optional<PricingConfig> findConfigForDate(LocalDateTime dateTime);
} 