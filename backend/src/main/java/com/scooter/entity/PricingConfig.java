package com.scooter.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pricing_configs")
public class PricingConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerMinute;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountPercentage;

    private Integer weeklyUsageThreshold; // 每周使用时长阈值（分钟）

    private Boolean isActive = true;

    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;

    @Version
    private Integer version;
} 