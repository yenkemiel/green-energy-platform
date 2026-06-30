package com.kemiel.greenenergy.module.greenenergy.service;

import com.kemiel.greenenergy.module.greenenergy.dto.CompletenessResponse;
import com.kemiel.greenenergy.module.greenenergy.dto.MonthlySummaryResponse;

/**
 * 綠電來源彙整 Service 介面
 */
public interface GreenEnergyService {

    MonthlySummaryResponse getMonthlySummary(int year, int month);

    CompletenessResponse getCompleteness(int year, int month);
}
