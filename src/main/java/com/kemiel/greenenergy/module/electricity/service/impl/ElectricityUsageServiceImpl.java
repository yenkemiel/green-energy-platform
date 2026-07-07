package com.kemiel.greenenergy.module.electricity.service.impl;

import com.kemiel.greenenergy.common.enums.AuditAction;
import com.kemiel.greenenergy.common.enums.ElectricityRecordStatus;
import com.kemiel.greenenergy.common.exception.BusinessException;
import com.kemiel.greenenergy.common.exception.ErrorCode;
import com.kemiel.greenenergy.common.util.AuditLogHelper;
import com.kemiel.greenenergy.module.electricity.dto.CreateElectricityUsageRequest;
import com.kemiel.greenenergy.module.electricity.dto.ElectricityUsageResponse;
import com.kemiel.greenenergy.module.electricity.dto.UpdateElectricityUsageRequest;
import com.kemiel.greenenergy.module.electricity.entity.ElectricityUsageRecord;
import com.kemiel.greenenergy.module.electricity.mapper.ElectricityUsageRecordMapper;
import com.kemiel.greenenergy.module.electricity.service.ElectricityUsageService;
import com.kemiel.greenenergy.module.greenenergy.calculation.GreenEnergyCalculationService;
import com.kemiel.greenenergy.module.user.entity.User;
import com.kemiel.greenenergy.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 每月用電量登記 Service 實作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ElectricityUsageServiceImpl implements ElectricityUsageService {

    private final ElectricityUsageRecordMapper electricityUsageRecordMapper;
    private final GreenEnergyCalculationService greenEnergyCalculationService;
    private final AuditLogHelper auditLogHelper;
    private final UserMapper userMapper;

    /**
     * 查詢指定年度的所有用電量記錄
     */
    @Override
    public List<ElectricityUsageResponse> listByYear(Integer recordYear) {
        log.info("查詢用電量記錄，recordYear={}", recordYear);
        return electricityUsageRecordMapper.selectListByYear(recordYear).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 新增當月用電量記錄，同月份不可重複建立，且需在補填截止期限（次月 5 號）內
     */
    @Override
    public ElectricityUsageResponse createRecord(CreateElectricityUsageRequest request, Long operatorId) {
        log.info("新增月用電量，recordYear={}, recordMonth={}, operatorId={}",
                request.getRecordYear(), request.getRecordMonth(), operatorId);

        ElectricityUsageRecord existing = electricityUsageRecordMapper.selectByYearAndMonth(
                request.getRecordYear(), request.getRecordMonth());
        if (existing != null) {
            throw new BusinessException(ErrorCode.ELECTRICITY_RECORD_DUPLICATE);
        }

        validateDeadline(request.getRecordYear(), request.getRecordMonth());

        ElectricityUsageRecord record = new ElectricityUsageRecord();
        record.setRecordYear(request.getRecordYear());
        record.setRecordMonth(request.getRecordMonth());
        record.setUsageKwh(request.getUsageKwh());
        record.setStatus(ElectricityRecordStatus.OPEN.name());
        record.setCreatedBy(operatorId);
        record.setUpdatedBy(operatorId);

        electricityUsageRecordMapper.insert(record);

        log.info("月用電量新增成功，id={}", record.getId());
        return toResponse(electricityUsageRecordMapper.selectById(record.getId()));
    }

    /**
     * 修改指定月份的用電量數值，已鎖定或超過補填截止期限不可修改，修改後寫入 Audit Log
     */
    @Override
    public ElectricityUsageResponse updateRecord(Long id, UpdateElectricityUsageRequest request, Long operatorId) {
        log.info("修改月用電量，id={}, operatorId={}", id, operatorId);

        ElectricityUsageRecord record = electricityUsageRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.ELECTRICITY_RECORD_NOT_FOUND);
        }
        if (ElectricityRecordStatus.LOCKED.name().equals(record.getStatus())) {
            throw new BusinessException(ErrorCode.ELECTRICITY_RECORD_LOCKED);
        }

        validateDeadline(record.getRecordYear(), record.getRecordMonth());

        BigDecimal oldUsageKwh = record.getUsageKwh();

        record.setUsageKwh(request.getUsageKwh());
        record.setUpdatedBy(operatorId);

        electricityUsageRecordMapper.updateById(record);

        String beforeValue = String.format("{\"usageKwh\": \"%s\"}", oldUsageKwh);
        String afterValue = String.format("{\"usageKwh\": \"%s\"}", request.getUsageKwh());
        User operator = userMapper.selectById(operatorId);
        auditLogHelper.log(
                AuditAction.UPDATE.name(), "electricity_usage_records", id,
                beforeValue, afterValue, operatorId, operator.getDisplayName());

        log.info("月用電量修改成功，id={}", id);
        return toResponse(electricityUsageRecordMapper.selectById(id));
    }

    /**
     * 鎖定指定月份（status -> LOCKED），並觸發 GreenEnergyCalculationService 計算並寫入
     * monthly_summary_snapshot；全程於 @Transactional 內執行，鎖定後寫入 Audit Log
     */
    @Override
    @Transactional
    public void lockRecord(Long id, Long operatorId) {
        log.info("鎖定月份，id={}, operatorId={}", id, operatorId);

        ElectricityUsageRecord record = electricityUsageRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.ELECTRICITY_RECORD_NOT_FOUND);
        }
        if (ElectricityRecordStatus.LOCKED.name().equals(record.getStatus())) {
            throw new BusinessException(ErrorCode.MONTH_ALREADY_LOCKED);
        }

        LocalDateTime lockedAt = LocalDateTime.now();

        electricityUsageRecordMapper.updateLockById(
                id,
                lockedAt,
                operatorId,
                ElectricityRecordStatus.LOCKED.name()
        );

        greenEnergyCalculationService.writeMonthlySummarySnapshot(
                record.getRecordYear(), record.getRecordMonth(), operatorId);

        String lockAfterValue = String.format(
                "{\"status\": \"%s\", \"lockedAt\": \"%s\"}",
                ElectricityRecordStatus.LOCKED.name(), lockedAt);
        User operator = userMapper.selectById(operatorId);
        auditLogHelper.logCreate(
                AuditAction.LOCK.name(), "electricity_usage_records", id,
                lockAfterValue, operatorId, operator.getDisplayName());

        log.info("月份鎖定成功，id={}", id);
    }

    /**
     * 驗證補填截止日，超過下個月 5 號則拋出例外
     */
    private void validateDeadline(Integer recordYear, Integer recordMonth) {
        LocalDate deadline = LocalDate.of(recordYear, recordMonth, 1)
                .plusMonths(1)
                .withDayOfMonth(5);
        if (LocalDate.now().isAfter(deadline)) {
            throw new BusinessException(ErrorCode.ELECTRICITY_RECORD_EXPIRED);
        }
    }

    private ElectricityUsageResponse toResponse(ElectricityUsageRecord r) {
        return ElectricityUsageResponse.builder()
                .id(r.getId())
                .recordYear(r.getRecordYear())
                .recordMonth(r.getRecordMonth())
                .usageKwh(r.getUsageKwh())
                .status(r.getStatus())
                .lockedAt(r.getLockedAt())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
