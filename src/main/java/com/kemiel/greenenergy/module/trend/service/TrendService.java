package com.kemiel.greenenergy.module.trend.service;

import com.kemiel.greenenergy.module.trend.dto.MonthlyTrendResponse;

/**
 * 月度趨勢報表 Service 介面
 */
public interface TrendService {

    MonthlyTrendResponse getMonthlyTrend(int year);
}
