package com.kemiel.greenenergy.module.auditlog.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Audit Log 查詢回應 DTO
 */
@Getter
@Builder
public class AuditLogResponse {

    private Long id;
    private String action;
    private String targetTable;
    private Long targetId;
    private String beforeValue;
    private String afterValue;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime createdAt;
}
