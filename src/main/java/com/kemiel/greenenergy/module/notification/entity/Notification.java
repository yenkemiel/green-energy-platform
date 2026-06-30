package com.kemiel.greenenergy.module.notification.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系統內通知，對應 notifications 資料表
 */
@Data
public class Notification {

    private Long id;
    private String type;
    private String title;
    private String message;
    private Long recipientId;
    private Integer isRead;
    private Long refId;
    private String refType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer isDeleted;
}