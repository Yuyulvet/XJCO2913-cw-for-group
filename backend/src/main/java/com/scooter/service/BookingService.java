package com.scooter.service;

import com.scooter.dto.BookingDTO;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    BookingDTO createBooking(BookingDTO bookingDTO);
    
    BookingDTO findById(Long id);
    
    List<BookingDTO> findByUserId(Long userId);
    
    List<BookingDTO> findByScooterId(Long scooterId);
    
    List<BookingDTO> findActiveBookingsByUserId(Long userId);
    
    List<BookingDTO> findAllActiveBookings();
    
    BookingDTO endBooking(Long id);
    
    BookingDTO extendBooking(Long id, int additionalMinutes);
    
    BookingDTO cancelBooking(Long id);
    
    Double calculateUserSpending(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    Long calculateUserUsageMinutes(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    void delete(Long id);
} 