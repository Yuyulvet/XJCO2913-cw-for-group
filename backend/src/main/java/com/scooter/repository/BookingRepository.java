package com.scooter.repository;

import com.scooter.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findByScooterId(Long scooterId);
    
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status = 'ACTIVE'")
    List<Booking> findActiveBookingsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT b FROM Booking b WHERE b.status = 'ACTIVE'")
    List<Booking> findAllActiveBookings();
    
    @Query("SELECT SUM(b.cost) FROM Booking b WHERE b.user.id = :userId AND " +
           "b.startTime >= :startDate AND b.endTime <= :endDate")
    Double calculateUserSpending(@Param("userId") Long userId,
                               @Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate);
    
    @Query(value = "SELECT COALESCE(SUM(TIMESTAMPDIFF(MINUTE, start_time, end_time)), 0) " +
           "FROM booking b WHERE b.user_id = :userId AND " +
           "b.start_time >= :startDate AND b.end_time <= :endDate AND " +
           "b.status = 'COMPLETED'", nativeQuery = true)
    Long calculateUserUsageMinutes(@Param("userId") Long userId,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);
} 