package com.scooter.service.impl;

import com.scooter.dto.ScooterDTO;
import com.scooter.entity.Scooter;
import com.scooter.repository.ScooterRepository;
import com.scooter.service.ScooterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScooterServiceImpl implements ScooterService {
    private final ScooterRepository scooterRepository;

    @Override
    @Transactional
    public ScooterDTO create(ScooterDTO scooterDTO) {
        Scooter scooter = new Scooter();
        scooter.setStatus(Scooter.ScooterStatus.AVAILABLE);
        scooter.setBatteryLevel(scooterDTO.getBatteryLevel());
        scooter.setLatitude(scooterDTO.getLatitude());
        scooter.setLongitude(scooterDTO.getLongitude());
        scooter.setLastMaintained(LocalDateTime.now());

        return ScooterDTO.fromEntity(scooterRepository.save(scooter));
    }

    @Override
    public ScooterDTO findById(Long id) {
        return scooterRepository.findById(id)
                .map(ScooterDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("滑板车不存在"));
    }

    @Override
    public List<ScooterDTO> findAll() {
        return scooterRepository.findAll().stream()
                .map(ScooterDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScooterDTO> findByStatus(Scooter.ScooterStatus status) {
        return scooterRepository.findByStatus(status).stream()
                .map(ScooterDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScooterDTO> findAvailableScooters() {
        return scooterRepository.findAvailableScooters().stream()
                .map(ScooterDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScooterDTO> findLowBatteryScooters() {
        return scooterRepository.findLowBatteryScooters().stream()
                .map(ScooterDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ScooterDTO update(Long id, ScooterDTO scooterDTO) {
        Scooter scooter = scooterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("滑板车不存在"));

        scooter.setBatteryLevel(scooterDTO.getBatteryLevel());
        scooter.setLatitude(scooterDTO.getLatitude());
        scooter.setLongitude(scooterDTO.getLongitude());

        return ScooterDTO.fromEntity(scooterRepository.save(scooter));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!scooterRepository.existsById(id)) {
            throw new RuntimeException("滑板车不存在");
        }
        scooterRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ScooterDTO updateStatus(Long id, Scooter.ScooterStatus status) {
        Scooter scooter = scooterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("滑板车不存在"));

        scooter.setStatus(status);
        return ScooterDTO.fromEntity(scooterRepository.save(scooter));
    }

    @Override
    @Transactional
    public ScooterDTO updateLocation(Long id, double latitude, double longitude) {
        Scooter scooter = scooterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("滑板车不存在"));

        scooter.setLatitude(BigDecimal.valueOf(latitude));
        scooter.setLongitude(BigDecimal.valueOf(longitude));
        return ScooterDTO.fromEntity(scooterRepository.save(scooter));
    }

    @Override
    @Transactional
    public ScooterDTO updateBatteryLevel(Long id, int batteryLevel) {
        if (batteryLevel < 0 || batteryLevel > 100) {
            throw new IllegalArgumentException("电池电量必须在0-100之间");
        }

        Scooter scooter = scooterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("滑板车不存在"));

        scooter.setBatteryLevel(batteryLevel);
        return ScooterDTO.fromEntity(scooterRepository.save(scooter));
    }

    @Override
    @Transactional
    public ScooterDTO markAsMaintained(Long id) {
        Scooter scooter = scooterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("滑板车不存在"));

        scooter.setLastMaintained(LocalDateTime.now());
        scooter.setStatus(Scooter.ScooterStatus.AVAILABLE);
        return ScooterDTO.fromEntity(scooterRepository.save(scooter));
    }
} 