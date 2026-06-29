package com.kemiel.greenenergy.module.greenenergy.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 月度綠電彙整快照，對應 monthly_summary_snapshots 資料表
 */
@Data
public class MonthlySummarySnapshot {

    private Long id;
    private Integer recordYear;
    private Integer recordMonth;
    private BigDecimal totalGreenKwh;
    private BigDecimal solarKwh;
    private BigDecimal contractKwh;
    private BigDecimal procurementKwh;
    private BigDecimal usageKwh;
    private BigDecimal achievementRate;
    private BigDecimal surplusKwh;
    private Long lockedBy;
    private LocalDateTime lockedAt;
    private LocalDateTime createdAt;
}

