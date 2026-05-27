package com.kemiel.greenenergy.module.solar.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 太陽能設備月發電紀錄 Response DTO
 */
@Getter
@Builder
public class SolarMonthlyRecordResponse {
    private Long id;
    private Long deviceId;
    private Integer recordYear;
    private Integer recordMonth;
    private BigDecimal actualKwh;
    private BigDecimal theoreticalKwh;
    private String source;
    private LocalDateTime createdAt;
}
