package com.kemiel.greenenergy.module.notification.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 未讀通知數量回應 DTO
 */
@Getter
@Builder
public class UnreadCountResponse {

    private Long unreadCount;
}