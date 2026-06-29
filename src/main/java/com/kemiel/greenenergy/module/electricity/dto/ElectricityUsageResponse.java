package com.kemiel.greenenergy.module.electricity.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 每月用電量查詢回應 DTO
 */
@Getter
@Builder
public class ElectricityUsageResponse {

    private Long id;
    private Integer recordYear;
    private Integer recordMonth;
    private BigDecimal usageKwh;
    private String status;
    private LocalDateTime lockedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

