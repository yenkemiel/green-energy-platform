package com.kemiel.greenenergy.module.auditlog.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.module.auditlog.dto.AuditLogResponse;
import com.kemiel.greenenergy.module.auditlog.entity.AuditLog;
import com.kemiel.greenenergy.module.auditlog.mapper.AuditLogMapper;
import com.kemiel.greenenergy.module.auditlog.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Audit Log Service 實作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogMapper auditLogMapper;

    /**
     * 查詢 Audit Log 清單，支援資料表、操作類型、操作者、時間區間篩選與分頁
     */
    @Override
    public PageResult<AuditLogResponse> listAuditLogs(String targetTable, String action,
                                                      Long operatorId, LocalDate startDate,
                                                      LocalDate endDate, int page, int size) {
        log.info("查詢 Audit Log，targetTable={}, action={}, operatorId={}",
                targetTable, action, operatorId);

        PageHelper.startPage(page + 1, size);
        List<AuditLog> list =
                auditLogMapper.selectList(targetTable, action, operatorId, startDate, endDate);
        PageInfo<AuditLog> pageInfo = new PageInfo<>(list);

        List<AuditLogResponse> content = pageInfo.getList().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PageResult.of(content, pageInfo);
    }

    /**
     * 寫入一筆 Audit Log
     */
    @Override
    public void log(String action, String targetTable, Long targetId,
                    String beforeValue, String afterValue,
                    Long operatorId, String operatorName) {
        log.info("寫入 Audit Log，action={}, targetTable={}, targetId={}", action, targetTable, targetId);

        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setTargetTable(targetTable);
        auditLog.setTargetId(targetId);
        auditLog.setBeforeValue(beforeValue);
        auditLog.setAfterValue(afterValue);
        auditLog.setOperatorId(operatorId);
        auditLog.setOperatorName(operatorName);

        auditLogMapper.insert(auditLog);
    }

    private AuditLogResponse toResponse(AuditLog a) {
        return AuditLogResponse.builder()
                .id(a.getId())
                .action(a.getAction())
                .targetTable(a.getTargetTable())
                .targetId(a.getTargetId())
                .beforeValue(a.getBeforeValue())
                .afterValue(a.getAfterValue())
                .operatorId(a.getOperatorId())
                .operatorName(a.getOperatorName())
                .createdAt(a.getCreatedAt())
                .build();
    }
}