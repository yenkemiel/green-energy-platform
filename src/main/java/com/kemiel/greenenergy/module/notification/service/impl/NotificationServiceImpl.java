package com.kemiel.greenenergy.module.notification.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kemiel.greenenergy.common.enums.NotificationType;
import com.kemiel.greenenergy.common.enums.RoleType;
import com.kemiel.greenenergy.common.exception.BusinessException;
import com.kemiel.greenenergy.common.exception.ErrorCode;
import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.module.notification.dto.NotificationResponse;
import com.kemiel.greenenergy.module.notification.dto.UnreadCountResponse;
import com.kemiel.greenenergy.module.notification.entity.Notification;
import com.kemiel.greenenergy.module.notification.mapper.NotificationMapper;
import com.kemiel.greenenergy.module.notification.service.NotificationService;
import com.kemiel.greenenergy.module.user.entity.User;
import com.kemiel.greenenergy.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系統內通知 Service 實作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;

    /**
     * 查詢登入使用者自己的通知清單，支援類型與已讀狀態篩選，依分頁回傳
     */
    @Override
    public PageResult<NotificationResponse> listNotifications(Long recipientId, String type,
                                                              Integer isRead, int page, int size) {
        log.info("查詢通知清單，recipientId={}, type={}, isRead={}", recipientId, type, isRead);

        PageHelper.startPage(page + 1, size);
        List<Notification> list =
                notificationMapper.selectList(recipientId, type, isRead);
        PageInfo<Notification> pageInfo = new PageInfo<>(list);

        List<NotificationResponse> content = pageInfo.getList().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PageResult.of(content, pageInfo);
    }

    /**
     * 查詢登入使用者的未讀通知數量
     */
    @Override
    public UnreadCountResponse getUnreadCount(Long recipientId) {
        log.info("查詢未讀通知數量，recipientId={}", recipientId);
        long count = notificationMapper.countUnread(recipientId);
        return UnreadCountResponse.builder().unreadCount(count).build();
    }

    /**
     * 標記單筆通知為已讀，僅限通知本人操作
     */
    @Override
    public void markAsRead(Long id, Long recipientId) {
        log.info("標記通知已讀，id={}, recipientId={}", id, recipientId);

        Notification notification = notificationMapper.selectById(id);
        if (notification == null) {
            throw new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (!notification.getRecipientId().equals(recipientId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        notificationMapper.updateReadById(id);
    }

    /**
     * 標記登入使用者所有未讀通知為已讀
     */
    @Override
    public void markAllAsRead(Long recipientId) {
        log.info("全部標記已讀，recipientId={}", recipientId);
        notificationMapper.updateReadAllByRecipient(recipientId);
    }

    /**
     * 建立太陽能發電異常通知，通知所有 EMPLOYEE 與 MANAGER
     */
    @Override
    public void createSolarAnomalyNotification(Long deviceId, String deviceName,
                                               int recordYear, int recordMonth,
                                               BigDecimal actualKwh, BigDecimal theoreticalKwh,
                                               String reason) {
        log.info("建立太陽能異常通知，deviceId={}, year={}, month={}", deviceId, recordYear, recordMonth);

        String title = "太陽能發電異常警告";
        String message = String.format("%s（%d-%02d）%s，請確認設備狀況。",
                deviceName, recordYear, recordMonth, reason);

        List<User> recipients = new ArrayList<>();
        recipients.addAll(userMapper.selectList(RoleType.EMPLOYEE));
        recipients.addAll(userMapper.selectList(RoleType.MANAGER));

        for (User user : recipients) {
            Notification notification = new Notification();
            notification.setType(NotificationType.SOLAR_ANOMALY.name());
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setRecipientId(user.getId());
            notification.setRefId(deviceId);
            notification.setRefType("SOLAR_DEVICE");
            notificationMapper.insert(notification);
        }

        log.info("太陽能異常通知已發送，共 {} 位收件人", recipients.size());
    }

    /**
     * 查詢同設備同月份是否已有 SOLAR_ANOMALY 通知
     */
    @Override
    public boolean existsSolarAnomalyNotification(Long deviceId, int recordYear, int recordMonth) {
        return notificationMapper.existsSolarAnomaly(deviceId, recordYear, recordMonth);
    }

    /**
     * 查詢同一份合約對同一位收件人是否已發過 CONTRACT_EXPIRY 通知
     */
    @Override
    public boolean existsContractExpiryNotification(Long contractId, Long recipientId) {
        return notificationMapper.existsContractExpiry(contractId, recipientId);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .isRead(n.getIsRead() == 1)
                .refId(n.getRefId())
                .refType(n.getRefType())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
