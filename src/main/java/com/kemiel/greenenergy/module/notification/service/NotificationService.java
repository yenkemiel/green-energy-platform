package com.kemiel.greenenergy.module.notification.service;

import java.math.BigDecimal;

/**
 * 系統內通知 Service 介面
 */
public interface NotificationService {

    void createSolarAnomalyNotification(Long deviceId, String deviceName,
                                        int recordYear, int recordMonth,
                                        BigDecimal actualKwh, BigDecimal theoreticalKwh,
                                        String reason);

    boolean existsSolarAnomalyNotification(Long deviceId, int recordYear, int recordMonth);
}
