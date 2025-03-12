package com.scooter.dto;

import com.scooter.entity.PricingConfig;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PricingConfigDTO {
    private Long id;
    
    @NotBlank(message = "配置名称不能为空")
    private String name;
    
    @NotNull(message = "基础价格不能为空")
    @DecimalMin(value = "0.0", message = "基础价格不能小于0")
    private BigDecimal basePrice;
    
    @NotNull(message = "每分钟价格不能为空")
    @DecimalMin(value = "0.0", message = "每分钟价格不能小于0")
    private BigDecimal pricePerMinute;
    
    private BigDecimal discountPercentage;
    private Integer weeklyUsageThreshold;
    private Boolean isActive;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    
    public static PricingConfigDTO fromEntity(PricingConfig config) {
        PricingConfigDTO dto = new PricingConfigDTO();
        dto.setId(config.getId());
        dto.setName(config.getName());
        dto.setBasePrice(config.getBasePrice());
        dto.setPricePerMinute(config.getPricePerMinute());
        dto.setDiscountPercentage(config.getDiscountPercentage());
        dto.setWeeklyUsageThreshold(config.getWeeklyUsageThreshold());
        dto.setIsActive(config.getIsActive());
        dto.setEffectiveFrom(config.getEffectiveFrom());
        dto.setEffectiveTo(config.getEffectiveTo());
        return dto;
    }
} 