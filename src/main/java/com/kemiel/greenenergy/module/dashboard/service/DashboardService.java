package com.kemiel.greenenergy.module.dashboard.service;

import com.kemiel.greenenergy.module.dashboard.dto.DashboardResponse;

/**
 * 缺口儀表板 Service 介面
 */
public interface DashboardService {

    DashboardResponse getDashboard(String period);
}
