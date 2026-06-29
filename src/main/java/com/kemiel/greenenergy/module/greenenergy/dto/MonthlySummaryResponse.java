package com.kemiel.greenenergy.module.greenenergy.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 月度綠電彙整查詢回應 DTO
 */
@Getter
@Builder
public class MonthlySummaryResponse {

    private Integer recordYear;
    private Integer recordMonth;
    private String status;
    private BigDecimal totalGreenKwh;
    private BigDecimal solarKwh;
    private BigDecimal contractKwh;
    private BigDecimal procurementKwh;
    private BigDecimal usageKwh;
    private BigDecimal achievementRate;
    private BigDecimal surplusKwh;
    private Integer completeness;
    private List<GreenSummarySourceItem> sources;
}
