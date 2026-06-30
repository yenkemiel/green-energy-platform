package com.kemiel.greenenergy.module.greenenergy.calculation.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 月度綠電彙整計算層中間承載 DTO
 */
@Data
@Builder
public class MonthlySummaryResult {

    private int year;
    private int month;
    private BigDecimal solarKwh;
    private BigDecimal contractKwh;
    private BigDecimal procurementKwh;
    private BigDecimal totalGreenKwh;
    private BigDecimal usageKwh;
    private BigDecimal achievementRate;
    private BigDecimal surplusKwh;
    private int completeness;
    private String status;
}
