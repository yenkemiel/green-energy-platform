package com.kemiel.greenenergy.module.prediction.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 年底達成率預測回應 DTO
 */
@Getter
@Builder
public class PredictionResponse {

    private Boolean predictable;
    private Integer basedOnMonths;
    private BigDecimal currentAchievementRate;
    private BigDecimal predictedYearEndRate;
    private BigDecimal predictedYearEndGapKwh;
    private BigDecimal additionalGreenNeeded;
    private String note;
}
