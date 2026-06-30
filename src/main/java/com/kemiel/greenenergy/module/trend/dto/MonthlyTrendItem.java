package com.kemiel.greenenergy.module.trend.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 月度趨勢報表單月資料 DTO
 */
@Getter
@Builder
public class MonthlyTrendItem {

    private Integer month;
    private BigDecimal usageKwh;
    private BigDecimal totalGreenKwh;
    private BigDecimal solarKwh;
    private BigDecimal contractKwh;
    private BigDecimal procurementKwh;
    private BigDecimal procurementPhysicalKwh;
    private BigDecimal procurementRecOnlyKwh;
    private BigDecimal achievementRate;
    private BigDecimal gapKwh;
    private BigDecimal surplusKwh;
    private String status;
}
