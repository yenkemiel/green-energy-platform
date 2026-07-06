package com.kemiel.greenenergy.common.util;

import com.kemiel.greenenergy.common.enums.ElectricityRecordStatus;
import com.kemiel.greenenergy.common.exception.BusinessException;
import com.kemiel.greenenergy.common.exception.ErrorCode;
import com.kemiel.greenenergy.module.electricity.entity.ElectricityUsageRecord;
import com.kemiel.greenenergy.module.electricity.mapper.ElectricityUsageRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MonthLockChecker {

    private final ElectricityUsageRecordMapper electricityUsageRecordMapper;

    public void assertNotLocked(YearMonth yearMonth) {
        ElectricityUsageRecord record = electricityUsageRecordMapper.selectByYearAndMonth(
                yearMonth.getYear(), yearMonth.getMonthValue());
        if (record != null && ElectricityRecordStatus.LOCKED.name().equals(record.getStatus())) {
            throw new BusinessException(ErrorCode.ELECTRICITY_RECORD_LOCKED);
        }
    }

    public void assertNoLockedMonthInRange(LocalDate startDate, LocalDate endDate) {
        List<ElectricityUsageRecord> lockedRecords = electricityUsageRecordMapper.selectLockedRecords();
        for (ElectricityUsageRecord record : lockedRecords) {
            YearMonth lockedMonth = YearMonth.of(record.getRecordYear(), record.getRecordMonth());
            if (MonthUtils.isContractActiveInMonth(startDate, endDate, lockedMonth)) {
                throw new BusinessException(ErrorCode.ELECTRICITY_RECORD_LOCKED);
            }
        }
    }
}