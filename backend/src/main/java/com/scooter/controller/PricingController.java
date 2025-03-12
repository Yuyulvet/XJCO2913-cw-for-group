package com.scooter.controller;

import com.scooter.dto.PricingConfigDTO;
import com.scooter.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
public class PricingController {
    private final PricingService pricingService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PricingConfigDTO> createConfig(@Valid @RequestBody PricingConfigDTO configDTO) {
        return ResponseEntity.ok(pricingService.createConfig(configDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PricingConfigDTO> getConfig(@PathVariable Long id) {
        return ResponseEntity.ok(pricingService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<PricingConfigDTO>> getAllConfigs() {
        return ResponseEntity.ok(pricingService.findAll());
    }

    @GetMapping("/current")
    public ResponseEntity<PricingConfigDTO> getCurrentConfig() {
        return ResponseEntity.ok(pricingService.getCurrentActiveConfig());
    }

    @GetMapping("/date")
    public ResponseEntity<PricingConfigDTO> getConfigForDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        return ResponseEntity.ok(pricingService.getConfigForDate(dateTime));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PricingConfigDTO> updateConfig(
            @PathVariable Long id,
            @Valid @RequestBody PricingConfigDTO configDTO) {
        return ResponseEntity.ok(pricingService.update(id, configDTO));
    }

    @GetMapping("/calculate")
    public ResponseEntity<BigDecimal> calculateCost(
            @RequestParam Long userId,
            @RequestParam int minutes) {
        return ResponseEntity.ok(pricingService.calculateCost(userId, minutes));
    }

    @GetMapping("/discount-eligibility/{userId}")
    public ResponseEntity<Boolean> checkDiscountEligibility(@PathVariable Long userId) {
        return ResponseEntity.ok(pricingService.isEligibleForDiscount(userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteConfig(@PathVariable Long id) {
        pricingService.delete(id);
        return ResponseEntity.ok().build();
    }
} 