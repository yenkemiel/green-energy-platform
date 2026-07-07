package com.kemiel.greenenergy.common.util;

import com.kemiel.greenenergy.common.enums.ElectricityRecordStatus;
import com.kemiel.greenenergy.common.exception.BusinessException;
import com.kemiel.greenenergy.common.exception.ErrorCode;
import com.kemiel.greenenergy.module.electricity.entity.ElectricityUsageRecord;
import com.kemiel.greenenergy.module.electricity.mapper.ElectricityUsageRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * 月份鎖定狀態檢查工具，供各模組寫入類 API 呼叫，防止修改已鎖定月份的資料
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MonthLockChecker {

    private final ElectricityUsageRecordMapper electricityUsageRecordMapper;

    /**
     * 確認指定月份尚未鎖定，若已鎖定則拋出例外；該月份查無電力使用量紀錄時視為未鎖定，予以放行
     *
     * @param yearMonth 欲檢查的年月
     */
    public void assertNotLocked(YearMonth yearMonth) {
        ElectricityUsageRecord record = electricityUsageRecordMapper.selectByYearAndMonth(
                yearMonth.getYear(), yearMonth.getMonthValue());
        if (record != null && ElectricityRecordStatus.LOCKED.name().equals(record.getStatus())) {
            log.warn("月份已鎖定，拒絕操作，yearMonth={}", yearMonth);
            throw new BusinessException(ErrorCode.ELECTRICITY_RECORD_LOCKED);
        }
    }

    /**
     * 檢查指定日期區間（如合約有效期間）是否涵蓋任何已鎖定的月份，
     * 若有重疊則拋出例外，避免透過建立或修改區間資料回填已鎖定月份的數字
     *
     * @param startDate 區間起始日期
     * @param endDate   區間結束日期
     */
    public void assertNoLockedMonthInRange(LocalDate startDate, LocalDate endDate) {
        List<ElectricityUsageRecord> lockedRecords = electricityUsageRecordMapper.selectLockedRecords();
        for (ElectricityUsageRecord record : lockedRecords) {
            YearMonth lockedMonth = YearMonth.of(record.getRecordYear(), record.getRecordMonth());
            if (MonthUtils.isContractActiveInMonth(startDate, endDate, lockedMonth)) {
                log.warn("合約日期範圍涵蓋鎖定月份，拒絕操作，startDate={}, endDate={}, lockedMonth={}",
                        startDate, endDate, lockedMonth);
                throw new BusinessException(ErrorCode.ELECTRICITY_RECORD_LOCKED);
            }
        }
    }
}