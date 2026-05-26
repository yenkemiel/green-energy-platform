package com.kemiel.greenenergy.module.solar.entity;

import com.kemiel.greenenergy.common.enums.DeviceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 太陽能設備 Entity，對應 solar_devices 資料表
 */
@Data
public class SolarDevice {

    private Long id;
    private String deviceName;
    private BigDecimal capacityKw;
    private LocalDate installDate;
    private String location;
    private DeviceStatus status;
    private Long locationId;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
