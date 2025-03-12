package com.scooter.service.impl;

import com.scooter.dto.PricingConfigDTO;
import com.scooter.entity.PricingConfig;
import com.scooter.repository.BookingRepository;
import com.scooter.repository.PricingConfigRepository;
import com.scooter.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {
    private final PricingConfigRepository pricingConfigRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public PricingConfigDTO createConfig(PricingConfigDTO configDTO) {
        PricingConfig config = new PricingConfig();
        config.setName(configDTO.getName());
        config.setBasePrice(configDTO.getBasePrice());
        config.setPricePerMinute(configDTO.getPricePerMinute());
        config.setDiscountPercentage(configDTO.getDiscountPercentage());
        config.setWeeklyUsageThreshold(configDTO.getWeeklyUsageThreshold());
        config.setIsActive(configDTO.getIsActive());
        config.setEffectiveFrom(configDTO.getEffectiveFrom());
        config.setEffectiveTo(configDTO.getEffectiveTo());

        return PricingConfigDTO.fromEntity(pricingConfigRepository.save(config));
    }

    @Override
    public PricingConfigDTO findById(Long id) {
        return pricingConfigRepository.findById(id)
                .map(PricingConfigDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("价格配置不存在"));
    }

    @Override
    public List<PricingConfigDTO> findAll() {
        return pricingConfigRepository.findAll().stream()
                .map(PricingConfigDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PricingConfigDTO update(Long id, PricingConfigDTO configDTO) {
        PricingConfig config = pricingConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("价格配置不存在"));

        if (!config.getName().equals(configDTO.getName()) &&
            pricingConfigRepository.existsByNameAndIdNot(configDTO.getName(), id)) {
            throw new RuntimeException("配置名称已存在");
        }

        config.setName(configDTO.getName());
        config.setBasePrice(configDTO.getBasePrice());
        config.setPricePerMinute(configDTO.getPricePerMinute());
        config.setDiscountPercentage(configDTO.getDiscountPercentage());
        config.setWeeklyUsageThreshold(configDTO.getWeeklyUsageThreshold());
        config.setIsActive(configDTO.getIsActive());
        config.setEffectiveFrom(configDTO.getEffectiveFrom());
        config.setEffectiveTo(configDTO.getEffectiveTo());

        return PricingConfigDTO.fromEntity(pricingConfigRepository.save(config));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!pricingConfigRepository.existsById(id)) {
            throw new RuntimeException("价格配置不存在");
        }
        pricingConfigRepository.deleteById(id);
    }

    @Override
    public PricingConfigDTO getCurrentActiveConfig() {
        return pricingConfigRepository.findCurrentActiveConfig()
                .map(PricingConfigDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("没有当前有效的价格配置"));
    }

    @Override
    public PricingConfigDTO getConfigForDate(LocalDateTime dateTime) {
        return pricingConfigRepository.findConfigForDate(dateTime)
                .map(PricingConfigDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("指定日期没有有效的价格配置"));
    }

    @Override
    public BigDecimal calculateCost(Long userId, int minutes) {
        PricingConfig config = pricingConfigRepository.findCurrentActiveConfig()
                .orElseThrow(() -> new RuntimeException("没有当前有效的价格配置"));

        BigDecimal cost = config.getBasePrice()
                .add(config.getPricePerMinute().multiply(BigDecimal.valueOf(minutes)));

        // 检查是否符合折扣条件
        if (isEligibleForDiscount(userId) && config.getDiscountPercentage() != null) {
            BigDecimal discount = cost.multiply(config.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            cost = cost.subtract(discount);
        }

        return cost.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean isEligibleForDiscount(Long userId) {
        PricingConfig config = pricingConfigRepository.findCurrentActiveConfig()
                .orElseThrow(() -> new RuntimeException("没有当前有效的价格配置"));

        if (config.getWeeklyUsageThreshold() == null) {
            return false;
        }

        LocalDateTime weekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        Long weeklyUsageMinutes = bookingRepository.calculateUserUsageMinutes(
                userId, weekAgo, LocalDateTime.now());

        return weeklyUsageMinutes != null && 
               weeklyUsageMinutes >= config.getWeeklyUsageThreshold();
    }
} 