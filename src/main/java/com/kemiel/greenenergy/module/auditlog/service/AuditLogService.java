package com.kemiel.greenenergy.module.auditlog.service;

import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.module.auditlog.dto.AuditLogResponse;

import java.time.LocalDate;

/**
 * Audit Log Service 介面
 */
public interface AuditLogService {

    PageResult<AuditLogResponse> listAuditLogs(String targetTable, String action,
                                               Long operatorId, LocalDate startDate,
                                               LocalDate endDate, int page, int size);

    void log(String action, String targetTable, Long targetId,
             String beforeValue, String afterValue,
             Long operatorId, String operatorName);
}
