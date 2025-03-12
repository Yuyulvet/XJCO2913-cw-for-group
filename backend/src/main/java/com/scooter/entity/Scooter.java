package com.scooter.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scooters")
public class Scooter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ScooterStatus status = ScooterStatus.AVAILABLE;

    private Integer batteryLevel;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    private LocalDateTime lastMaintained;

    @OneToMany(mappedBy = "scooter", cascade = CascadeType.ALL)
    private Set<Booking> bookings = new HashSet<>();

    @OneToMany(mappedBy = "scooter", cascade = CascadeType.ALL)
    private Set<Issue> issues = new HashSet<>();

    public enum ScooterStatus {
        AVAILABLE, IN_USE, MAINTENANCE
    }
} 