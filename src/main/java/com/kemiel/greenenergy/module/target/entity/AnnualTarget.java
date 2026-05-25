package com.kemiel.greenenergy.module.target.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 年度綠電目標設定，對應 annual_targets 資料表。
 */
@Data
public class AnnualTarget {
    private Long id;
    private Integer targetYear;
    private BigDecimal annualElectricityKwh;
    private BigDecimal re100TargetRatio;
    private BigDecimal growthRate;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
