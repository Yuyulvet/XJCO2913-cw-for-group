package com.scooter.service;

import com.scooter.dto.PricingConfigDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PricingService {
    PricingConfigDTO createConfig(PricingConfigDTO configDTO);
    
    PricingConfigDTO findById(Long id);
    
    List<PricingConfigDTO> findAll();
    
    PricingConfigDTO update(Long id, PricingConfigDTO configDTO);
    
    void delete(Long id);
    
    PricingConfigDTO getCurrentActiveConfig();
    
    PricingConfigDTO getConfigForDate(LocalDateTime dateTime);
    
    BigDecimal calculateCost(Long userId, int minutes);
    
    boolean isEligibleForDiscount(Long userId);
} 