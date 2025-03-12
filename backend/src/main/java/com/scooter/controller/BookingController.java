package com.scooter.controller;

import com.scooter.dto.BookingDTO;
import com.scooter.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@Valid @RequestBody BookingDTO bookingDTO) {
        return ResponseEntity.ok(bookingService.createBooking(bookingDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDTO>> getUserBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.findByUserId(userId));
    }

    @GetMapping("/scooter/{scooterId}")
    public ResponseEntity<List<BookingDTO>> getScooterBookings(@PathVariable Long scooterId) {
        return ResponseEntity.ok(bookingService.findByScooterId(scooterId));
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<BookingDTO>> getUserActiveBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.findActiveBookingsByUserId(userId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<BookingDTO>> getAllActiveBookings() {
        return ResponseEntity.ok(bookingService.findAllActiveBookings());
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<BookingDTO> endBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.endBooking(id));
    }

    @PutMapping("/{id}/extend")
    public ResponseEntity<BookingDTO> extendBooking(
            @PathVariable Long id,
            @RequestParam int additionalMinutes) {
        return ResponseEntity.ok(bookingService.extendBooking(id, additionalMinutes));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingDTO> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @GetMapping("/user/{userId}/spending")
    public ResponseEntity<Double> getUserSpending(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(bookingService.calculateUserSpending(userId, startDate, endDate));
    }

    @GetMapping("/user/{userId}/usage")
    public ResponseEntity<Long> getUserUsageMinutes(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(bookingService.calculateUserUsageMinutes(userId, startDate, endDate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.ok().build();
    }
} 