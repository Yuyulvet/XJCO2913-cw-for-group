package com.scooter.service.impl;

import com.scooter.dto.BookingDTO;
import com.scooter.entity.Booking;
import com.scooter.entity.Scooter;
import com.scooter.entity.User;
import com.scooter.repository.BookingRepository;
import com.scooter.repository.ScooterRepository;
import com.scooter.repository.UserRepository;
import com.scooter.service.BookingService;
import com.scooter.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ScooterRepository scooterRepository;
    private final PricingService pricingService;

    @Override
    @Transactional
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        User user = userRepository.findById(bookingDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Scooter scooter = scooterRepository.findById(bookingDTO.getScooterId())
                .orElseThrow(() -> new RuntimeException("滑板车不存在"));

        if (scooter.getStatus() != Scooter.ScooterStatus.AVAILABLE) {
            throw new RuntimeException("滑板车当前不可用");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setScooter(scooter);
        booking.setStartTime(LocalDateTime.now());
        booking.setStatus(Booking.BookingStatus.ACTIVE);

        // 如果指定了时长，设置结束时间
        if (bookingDTO.getDurationMinutes() != null) {
            booking.setEndTime(booking.getStartTime().plusMinutes(bookingDTO.getDurationMinutes()));
            booking.setCost(pricingService.calculateCost(user.getId(), bookingDTO.getDurationMinutes()));
        }

        scooter.setStatus(Scooter.ScooterStatus.IN_USE);
        scooterRepository.save(scooter);

        return BookingDTO.fromEntity(bookingRepository.save(booking));
    }

    @Override
    public BookingDTO findById(Long id) {
        return bookingRepository.findById(id)
                .map(BookingDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("预订不存在"));
    }

    @Override
    public List<BookingDTO> findByUserId(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(BookingDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> findByScooterId(Long scooterId) {
        return bookingRepository.findByScooterId(scooterId).stream()
                .map(BookingDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> findActiveBookingsByUserId(Long userId) {
        return bookingRepository.findActiveBookingsByUserId(userId).stream()
                .map(BookingDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> findAllActiveBookings() {
        return bookingRepository.findAllActiveBookings().stream()
                .map(BookingDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDTO endBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("预订不存在"));

        if (booking.getStatus() != Booking.BookingStatus.ACTIVE) {
            throw new RuntimeException("只能结束进行中的预订");
        }

        booking.setEndTime(LocalDateTime.now());
        booking.setStatus(Booking.BookingStatus.COMPLETED);

        // 计算费用
        long minutes = ChronoUnit.MINUTES.between(booking.getStartTime(), booking.getEndTime());
        booking.setCost(pricingService.calculateCost(booking.getUser().getId(), (int) minutes));

        // 更新滑板车状态
        Scooter scooter = booking.getScooter();
        scooter.setStatus(Scooter.ScooterStatus.AVAILABLE);
        scooterRepository.save(scooter);

        return BookingDTO.fromEntity(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDTO extendBooking(Long id, int additionalMinutes) {
        if (additionalMinutes <= 0) {
            throw new IllegalArgumentException("延长时间必须大于0");
        }

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("预订不存在"));

        if (booking.getStatus() != Booking.BookingStatus.ACTIVE) {
            throw new RuntimeException("只能延长进行中的预订");
        }

        if (booking.getEndTime() == null) {
            throw new RuntimeException("无限时预订不能延长时间");
        }

        booking.setEndTime(booking.getEndTime().plusMinutes(additionalMinutes));
        
        // 重新计算费用
        long totalMinutes = ChronoUnit.MINUTES.between(booking.getStartTime(), booking.getEndTime());
        booking.setCost(pricingService.calculateCost(booking.getUser().getId(), (int) totalMinutes));

        return BookingDTO.fromEntity(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDTO cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("预订不存在"));

        if (booking.getStatus() != Booking.BookingStatus.ACTIVE) {
            throw new RuntimeException("只能取消进行中的预订");
        }

        booking.setEndTime(LocalDateTime.now());
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setCost(BigDecimal.ZERO);

        // 更新滑板车状态
        Scooter scooter = booking.getScooter();
        scooter.setStatus(Scooter.ScooterStatus.AVAILABLE);
        scooterRepository.save(scooter);

        return BookingDTO.fromEntity(bookingRepository.save(booking));
    }

    @Override
    public Double calculateUserSpending(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.calculateUserSpending(userId, startDate, endDate);
    }

    @Override
    public Long calculateUserUsageMinutes(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.calculateUserUsageMinutes(userId, startDate, endDate);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new RuntimeException("预订不存在");
        }
        bookingRepository.deleteById(id);
    }
} 