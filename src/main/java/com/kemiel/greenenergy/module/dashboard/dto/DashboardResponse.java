package com.kemiel.greenenergy.module.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 缺口儀表板查詢回應 DTO
 */
@Getter
@Builder
public class DashboardResponse {

    private String period;
    private Integer targetYear;
    private BigDecimal requiredGreenKwh;
    private BigDecimal totalGreenKwh;
    private BigDecimal totalUsageKwh;
    private BigDecimal achievementRate;
    private BigDecimal gapKwh;
    private BigDecimal greenRatio;
    private BigDecimal gridRatio;
    private BigDecimal co2ReducedTon;
    private PendingItemsResponse pendingItems;
}