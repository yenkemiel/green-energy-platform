package com.kemiel.greenenergy.module.trend.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 月度趨勢報表回應 DTO
 */
@Getter
@Builder
public class MonthlyTrendResponse {

    private Integer year;
    private List<MonthlyTrendItem> months;
}