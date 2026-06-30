package com.kemiel.greenenergy.module.prediction.service.impl;

import com.kemiel.greenenergy.module.greenenergy.calculation.GreenEnergyCalculationService;
import com.kemiel.greenenergy.module.greenenergy.calculation.dto.PredictionResult;
import com.kemiel.greenenergy.module.prediction.dto.PredictionResponse;
import com.kemiel.greenenergy.module.prediction.service.PredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 缺口預測 Service 實作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PredictionServiceImpl implements PredictionService {

    private final GreenEnergyCalculationService calculationService;

    /**
     * 查詢指定年度的年底達成率預測，資料不足時回傳 predictable: false
     *
     * @param year  目標年度
     */
    @Override
    public PredictionResponse getPrediction(int year) {
        log.info("查詢缺口預測，year={}", year);

        PredictionResult result = calculationService.predictYearEnd(year);

        return PredictionResponse.builder()
                .predictable(result.isPredictable())
                .basedOnMonths(result.getBasedOnMonths())
                .currentAchievementRate(result.getCurrentAchievementRate())
                .predictedYearEndRate(result.getPredictedYearEndRate())
                .predictedYearEndGapKwh(result.getPredictedYearEndGapKwh())
                .additionalGreenNeeded(result.getPredictedYearEndGapKwh())
                .note(result.getNote())
                .build();
    }
}
