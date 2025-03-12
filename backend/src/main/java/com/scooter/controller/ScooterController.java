package com.scooter.controller;

import com.scooter.dto.ScooterDTO;
import com.scooter.entity.Scooter;
import com.scooter.service.ScooterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/scooters")
@RequiredArgsConstructor
public class ScooterController {
    private final ScooterService scooterService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScooterDTO> createScooter(@Valid @RequestBody ScooterDTO scooterDTO) {
        return ResponseEntity.ok(scooterService.create(scooterDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScooterDTO> getScooter(@PathVariable Long id) {
        return ResponseEntity.ok(scooterService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<ScooterDTO>> getAllScooters() {
        return ResponseEntity.ok(scooterService.findAll());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ScooterDTO>> getScootersByStatus(@PathVariable Scooter.ScooterStatus status) {
        return ResponseEntity.ok(scooterService.findByStatus(status));
    }

    @GetMapping("/available")
    public ResponseEntity<List<ScooterDTO>> getAvailableScooters() {
        return ResponseEntity.ok(scooterService.findAvailableScooters());
    }

    @GetMapping("/low-battery")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MAINTENANCE')")
    public ResponseEntity<List<ScooterDTO>> getLowBatteryScooters() {
        return ResponseEntity.ok(scooterService.findLowBatteryScooters());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScooterDTO> updateScooter(
            @PathVariable Long id,
            @Valid @RequestBody ScooterDTO scooterDTO) {
        return ResponseEntity.ok(scooterService.update(id, scooterDTO));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MAINTENANCE')")
    public ResponseEntity<ScooterDTO> updateScooterStatus(
            @PathVariable Long id,
            @RequestParam Scooter.ScooterStatus status) {
        return ResponseEntity.ok(scooterService.updateStatus(id, status));
    }

    @PutMapping("/{id}/location")
    public ResponseEntity<ScooterDTO> updateScooterLocation(
            @PathVariable Long id,
            @RequestParam double latitude,
            @RequestParam double longitude) {
        return ResponseEntity.ok(scooterService.updateLocation(id, latitude, longitude));
    }

    @PutMapping("/{id}/battery")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MAINTENANCE')")
    public ResponseEntity<ScooterDTO> updateBatteryLevel(
            @PathVariable Long id,
            @RequestParam int batteryLevel) {
        return ResponseEntity.ok(scooterService.updateBatteryLevel(id, batteryLevel));
    }

    @PutMapping("/{id}/maintain")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MAINTENANCE')")
    public ResponseEntity<ScooterDTO> markAsMaintained(@PathVariable Long id) {
        return ResponseEntity.ok(scooterService.markAsMaintained(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteScooter(@PathVariable Long id) {
        scooterService.delete(id);
        return ResponseEntity.ok().build();
    }
} 