package com.kemiel.greenenergy.module.auditlog.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Audit Log 操作紀錄，對應 audit_logs 資料表
 */
@Data
public class AuditLog {

    private Long id;
    private String action;           // AuditAction enum name
    private String targetTable;      // 異動的資料表名稱
    private Long targetId;           // 異動的資料 id
    private String beforeValue;      // 變更前（JSON 字串）
    private String afterValue;       // 變更後（JSON 字串）
    private Long operatorId;
    private String operatorName;     // 操作者名稱快照，避免 join
    private LocalDateTime createdAt;
}