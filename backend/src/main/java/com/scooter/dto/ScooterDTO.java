package com.scooter.dto;

import com.scooter.entity.Scooter;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScooterDTO {
    private Long id;
    
    private Scooter.ScooterStatus status;
    
    @NotNull(message = "电池电量不能为空")
    @Min(value = 0, message = "电池电量不能小于0")
    @Max(value = 100, message = "电池电量不能大于100")
    private Integer batteryLevel;
    
    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;
    
    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;
    
    private LocalDateTime lastMaintained;
    
    public static ScooterDTO fromEntity(Scooter scooter) {
        ScooterDTO dto = new ScooterDTO();
        dto.setId(scooter.getId());
        dto.setStatus(scooter.getStatus());
        dto.setBatteryLevel(scooter.getBatteryLevel());
        dto.setLatitude(scooter.getLatitude());
        dto.setLongitude(scooter.getLongitude());
        dto.setLastMaintained(scooter.getLastMaintained());
        return dto;
    }
} 