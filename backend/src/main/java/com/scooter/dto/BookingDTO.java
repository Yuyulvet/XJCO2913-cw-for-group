package com.scooter.dto;

import com.scooter.entity.Booking;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingDTO {
    private Long id;
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotNull(message = "滑板车ID不能为空")
    private Long scooterId;
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Booking.BookingStatus status;
    private BigDecimal cost;
    
    // 用于创建预订的扩展字段
    private Integer durationMinutes;
    
    public static BookingDTO fromEntity(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setScooterId(booking.getScooter().getId());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setStatus(booking.getStatus());
        dto.setCost(booking.getCost());
        return dto;
    }
} 