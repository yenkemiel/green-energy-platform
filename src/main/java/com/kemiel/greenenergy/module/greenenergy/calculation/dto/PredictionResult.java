package com.kemiel.greenenergy.module.greenenergy.calculation.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 年底達成率預測計算層中間承載 DTO
 */
@Data
@Builder
public class PredictionResult {

    private boolean predictable;
    private int basedOnMonths;
    private BigDecimal currentAchievementRate;
    private BigDecimal predictedYearEndRate;
    private BigDecimal predictedYearEndGapKwh;
    private String note;
}

