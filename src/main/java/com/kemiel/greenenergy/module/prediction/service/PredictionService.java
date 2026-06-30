package com.kemiel.greenenergy.module.prediction.service;

import com.kemiel.greenenergy.module.prediction.dto.PredictionResponse;

/**
 * 缺口預測 Service 介面
 */
public interface PredictionService {

    PredictionResponse getPrediction(int year);
}