package com.kemiel.greenenergy.module.notification.service;

import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.module.notification.dto.NotificationResponse;
import com.kemiel.greenenergy.module.notification.dto.UnreadCountResponse;

import java.math.BigDecimal;

/**
 * 系統內通知 Service 介面
 */
public interface NotificationService {

    PageResult<NotificationResponse> listNotifications(Long recipientId, String type,
                                                       Integer isRead, int page, int size);

    UnreadCountResponse getUnreadCount(Long recipientId);

    void markAsRead(Long id, Long recipientId);

    void markAllAsRead(Long recipientId);

    void createSolarAnomalyNotification(Long deviceId, String deviceName,
                                        int recordYear, int recordMonth,
                                        BigDecimal actualKwh, BigDecimal theoreticalKwh,
                                        String reason);

    boolean existsSolarAnomalyNotification(Long deviceId, int recordYear, int recordMonth);

    boolean existsContractExpiryNotification(Long contractId, Long recipientId);
}
