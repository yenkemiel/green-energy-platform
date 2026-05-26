package com.kemiel.greenenergy.module.solar.dto;

import com.kemiel.greenenergy.common.enums.DeviceStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 太陽能設備查詢回應 DTO
 */
@Getter
@Builder
public class SolarDeviceResponse {
    private Long id;
    private String deviceName;
    private BigDecimal capacityKw;
    private LocalDate installDate;
    private String location;
    private DeviceStatus status;
    private BigDecimal theoreticalMonthlyKwh;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
