package com.kemiel.greenenergy.module.electricity.service.impl;

import com.kemiel.greenenergy.common.enums.ElectricityRecordStatus;
import com.kemiel.greenenergy.common.exception.BusinessException;
import com.kemiel.greenenergy.common.exception.ErrorCode;
import com.kemiel.greenenergy.module.electricity.dto.CreateElectricityUsageRequest;
import com.kemiel.greenenergy.module.electricity.dto.ElectricityUsageResponse;
import com.kemiel.greenenergy.module.electricity.dto.UpdateElectricityUsageRequest;
import com.kemiel.greenenergy.module.electricity.entity.ElectricityUsageRecord;
import com.kemiel.greenenergy.module.electricity.mapper.ElectricityUsageRecordMapper;
import com.kemiel.greenenergy.module.electricity.service.ElectricityUsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * 新增當月用電量記錄
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
     * 修改指定用電量記錄的用電量數值
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

        record.setUsageKwh(request.getUsageKwh());
        record.setUpdatedBy(operatorId);

        electricityUsageRecordMapper.updateById(record);

        log.info("月用電量修改成功，id={}", id);
        return toResponse(electricityUsageRecordMapper.selectById(id));
    }

    /**
     * 鎖定指定月份，鎖定後不可修改
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

        electricityUsageRecordMapper.updateLockById(
                id,
                LocalDateTime.now(),
                operatorId,
                ElectricityRecordStatus.LOCKED.name()
        );

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
