package com.kemiel.greenenergy.common.helper;

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
     * 寫入一筆無 before / after 的 Audit Log（如建立、鎖定等只有 after 的操作）
     */
    public void logCreate(String action, String targetTable, Long targetId,
                          String afterValue, Long operatorId, String operatorName) {
        auditLogService.log(action, targetTable, targetId,
                null, afterValue, operatorId, operatorName);
    }
}
