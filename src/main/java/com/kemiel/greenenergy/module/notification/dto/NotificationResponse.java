package com.kemiel.greenenergy.module.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 通知查詢回應 DTO
 */
@Getter
@Builder
public class NotificationResponse {

    private Long id;
    private String type;
    private String title;
    private String message;
    private Boolean isRead;
    private Long refId;
    private String refType;
    private LocalDateTime createdAt;
}
