package com.kemiel.greenenergy.module.target.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 年度目標查詢回傳格式，含計算欄位 requiredGreenKwh。
 */
@Getter
@Builder
public class TargetResponse {
    private Long id;
    private Integer targetYear;
    private BigDecimal annualElectricityKwh;
    private BigDecimal re100TargetRatio;
    private BigDecimal growthRate;
    private BigDecimal requiredGreenKwh;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
