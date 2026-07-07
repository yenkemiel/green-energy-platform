package com.kemiel.greenenergy.common.util;

import com.kemiel.greenenergy.module.auditlog.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Audit Log 寫入共用工具，統一各 Service 層的呼叫方式
 */
@Component
@RequiredArgsConstructor
public class AuditLogHelper {

    private final AuditLogService auditLogService;

    /**
     * 寫入一筆完整 Audit Log，包含變更前後值
     */
    public void log(String action, String targetTable, Long targetId,
                    String beforeValue, String afterValue,
                    Long operatorId, String operatorName) {
        auditLogService.log(action, targetTable, targetId,
                beforeValue, afterValue, operatorId, operatorName);
    }

    /**
     * 寫入一筆 beforeValue 為 null 的 Audit Log，適用於建立、鎖定等僅需記錄異動後狀態的操作
     */
    public void logCreate(String action, String targetTable, Long targetId,
                          String afterValue, Long operatorId, String operatorName) {
        auditLogService.log(action, targetTable, targetId,
                null, afterValue, operatorId, operatorName);
    }
}
