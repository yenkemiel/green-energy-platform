package com.kemiel.greenenergy.module.notification.service.impl;

import com.kemiel.greenenergy.module.notification.service.NotificationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void createSolarAnomalyNotification(Long deviceId, String deviceName,
                                               int recordYear, int recordMonth,
                                               BigDecimal actualKwh, BigDecimal theoreticalKwh,
                                               String reason) {
    }

    @Override
    public boolean existsSolarAnomalyNotification(Long deviceId, int recordYear, int recordMonth) {
        return false;
    }
}
