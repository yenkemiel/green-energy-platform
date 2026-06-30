package com.kemiel.greenenergy.module.solar.service.impl;

import com.kemiel.greenenergy.common.enums.DeviceStatus;
import com.kemiel.greenenergy.common.exception.BusinessException;
import com.kemiel.greenenergy.common.exception.ErrorCode;
import com.kemiel.greenenergy.common.util.MonthUtils;
import com.kemiel.greenenergy.module.greenenergy.config.AnomalyConfig;
import com.kemiel.greenenergy.module.notification.service.NotificationService;
import com.kemiel.greenenergy.module.solar.dto.CreateSolarMonthlyRecordRequest;
import com.kemiel.greenenergy.module.solar.dto.SolarMonthlyRecordResponse;
import com.kemiel.greenenergy.module.solar.dto.UpdateSolarMonthlyRecordRequest;
import com.kemiel.greenenergy.module.solar.entity.SolarDevice;
import com.kemiel.greenenergy.module.solar.entity.SolarMonthlyRecord;
import com.kemiel.greenenergy.module.solar.mapper.SolarDeviceMapper;
import com.kemiel.greenenergy.module.solar.mapper.SolarMonthlyRecordMapper;
import com.kemiel.greenenergy.module.solar.service.SolarMonthlyRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 太陽能設備月發電紀錄 Service 實作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SolarMonthlyRecordServiceImpl implements SolarMonthlyRecordService {

    private final SolarMonthlyRecordMapper recordMapper;
    private final SolarDeviceMapper deviceMapper;
    private final NotificationService notificationService;

    /**
     * 查詢指定設備指定年度所有月份發電紀錄
     */
    @Override
    public List<SolarMonthlyRecordResponse> listRecords(Long deviceId, Integer year) {
        log.info("查詢月發電紀錄，deviceId={}, year={}", deviceId, year);
        SolarDevice device = deviceMapper.selectById(deviceId);
        if (device == null) {
            throw new BusinessException(ErrorCode.SOLAR_DEVICE_NOT_FOUND);
        }
        List<SolarMonthlyRecord> records =recordMapper.selectListByDeviceIdAndYear(deviceId, year);
        return records.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 查詢指定設備有月發電紀錄的所有年份
     */
    @Override
    public List<Integer> listYears(Long deviceId) {
        log.info("查詢設備有記錄的年份列表，deviceId={}", deviceId);
        SolarDevice device = deviceMapper.selectById(deviceId);
        if (device == null) {
            throw new BusinessException(ErrorCode.SOLAR_DEVICE_NOT_FOUND);
        }
        return recordMapper.selectDistinctYears(deviceId);
    }

    /**
     * 新增月發電紀錄，並計算理論發電量快照
     *
     * @param operatorId 當前操作者 userId
     */
    @Override
    public SolarMonthlyRecordResponse createRecord(Long deviceId,
                                                   CreateSolarMonthlyRecordRequest request,
                                                   Long operatorId) {
        log.info("新增月發電紀錄，deviceId={}, year={}, month={}", deviceId, request.getRecordYear(), request.getRecordMonth());

        SolarDevice device = deviceMapper.selectById(deviceId);
        if (device == null) {
            throw new BusinessException(ErrorCode.SOLAR_DEVICE_NOT_FOUND);
        }

        YearMonth yearMonth = YearMonth.of(request.getRecordYear(), request.getRecordMonth());
        if (!MonthUtils.isEditable(yearMonth)) {
            throw new BusinessException(ErrorCode.ELECTRICITY_RECORD_EXPIRED);
        }

        SolarMonthlyRecord existing = recordMapper.selectByDeviceIdAndYearMonth(
                deviceId, request.getRecordYear(), request.getRecordMonth());
        if (existing != null) {
            throw new BusinessException(ErrorCode.SOLAR_RECORD_DUPLICATE);
        }

        BigDecimal theoreticalKwh = calculateTheoreticalKwh(device.getCapacityKw(), yearMonth);

        SolarMonthlyRecord record = new SolarMonthlyRecord();
        record.setDeviceId(deviceId);
        record.setRecordYear(request.getRecordYear());
        record.setRecordMonth(request.getRecordMonth());
        record.setActualKwh(request.getActualKwh());
        record.setTheoreticalKwh(theoreticalKwh);
        record.setSource("MANUAL");
        record.setCreatedBy(operatorId);

        recordMapper.insert(record);

        SolarMonthlyRecord saved = recordMapper.selectById(record.getId());
        checkAndTriggerAnomaly(device, saved);

        return toResponse(saved);

    }

    /**
     * 修改月發電紀錄，並重新計算理論發電量快照
     *
     * @param operatorId 當前操作者 userId
     */
    @Override
    public SolarMonthlyRecordResponse updateRecord(Long deviceId, Long recordId,
                                                   UpdateSolarMonthlyRecordRequest request,
                                                   Long operatorId) {
        log.info("修改月發電紀錄，deviceId={}, recordId={}", deviceId, recordId);

        SolarDevice device = deviceMapper.selectById(deviceId);
        if (device == null) {
            throw new BusinessException(ErrorCode.SOLAR_DEVICE_NOT_FOUND);
        }

        SolarMonthlyRecord record = recordMapper.selectById(recordId);
        if (record == null || !record.getDeviceId().equals(deviceId)) {
            throw new BusinessException(ErrorCode.SOLAR_RECORD_NOT_FOUND);
        }

        YearMonth yearMonth = YearMonth.of(record.getRecordYear(), record.getRecordMonth());
        if (!MonthUtils.isEditable(yearMonth)) {
            throw new BusinessException(ErrorCode.ELECTRICITY_RECORD_EXPIRED);
        }

        BigDecimal theoreticalKwh = calculateTheoreticalKwh(device.getCapacityKw(), yearMonth);

        record.setActualKwh(request.getActualKwh());
        record.setTheoreticalKwh(theoreticalKwh);
        recordMapper.updateById(record);

        SolarMonthlyRecord updated = recordMapper.selectById(recordId);
        checkAndTriggerAnomaly(device, updated);

        return toResponse(updated);
    }

    /**
     * 偵測月發電紀錄是否異常，滿足任一條件即觸發通知，同設備同月份不重複觸發
     *
     * @param device  太陽能設備
     * @param record  當月發電紀錄
     */
    private void checkAndTriggerAnomaly(SolarDevice device, SolarMonthlyRecord record) {
        int year = record.getRecordYear();
        int month = record.getRecordMonth();
        BigDecimal actualKwh = record.getActualKwh();
        BigDecimal theoreticalKwh = record.getTheoreticalKwh();

        if (device.getStatus() == DeviceStatus.INACTIVE) {
            return;
        }

        LocalDate installDate = device.getInstallDate();
        YearMonth installMonth = YearMonth.of(installDate.getYear(), installDate.getMonthValue());
        YearMonth recordMonth = YearMonth.of(year, month);
        if (recordMonth.equals(installMonth)) {
            return;
        }

        if (notificationService.existsSolarAnomalyNotification(device.getId(), year, month)) {
            log.info("同設備同月份已有異常通知，跳過，deviceId={}, year={}, month={}",
                    device.getId(), year, month);
            return;
        }

        String reason = null;

        BigDecimal threshold = theoreticalKwh.multiply(AnomalyConfig.THEORETICAL_THRESHOLD);
        if (actualKwh.compareTo(threshold) < 0) {
            reason = String.format("實際發電量 %.0f kWh，低於理論發電量 %.0f kWh 的 50%%",
                    actualKwh, theoreticalKwh);
        }

        if (reason == null) {
            int prevMonth = month == 1 ? 12 : month - 1;
            int prevYear = month == 1 ? year - 1 : year;
            SolarMonthlyRecord prevRecord = recordMapper.selectByDeviceIdAndYearMonth(
                    device.getId(), prevYear, prevMonth);
            if (prevRecord != null) {
                BigDecimal momThreshold = prevRecord.getActualKwh()
                        .multiply(BigDecimal.ONE.subtract(AnomalyConfig.MOM_DECLINE_THRESHOLD));
                if (actualKwh.compareTo(momThreshold) < 0) {
                    reason = String.format("實際發電量 %.0f kWh，較上月 %.0f kWh 下降超過 30%%",
                            actualKwh, prevRecord.getActualKwh());
                }
            }
        }

        if (reason != null) {
            log.warn("偵測到太陽能發電異常，deviceId={}, year={}, month={}, reason={}",
                    device.getId(), year, month, reason);
            notificationService.createSolarAnomalyNotification(
                    device.getId(), device.getDeviceName(),
                    year, month, actualKwh, theoreticalKwh, reason);
        }
    }

    /**
     * 計算理論月發電量（capacity_kw × 3.5 × 當月天數）
     */
    private BigDecimal calculateTheoreticalKwh(BigDecimal capacityKw, YearMonth yearMonth) {
        int daysInMonth = yearMonth.lengthOfMonth();
        return  capacityKw
                .multiply(BigDecimal.valueOf(3.5))
                .multiply(BigDecimal.valueOf(daysInMonth))
                .setScale(4, RoundingMode.HALF_UP);
    }
    private SolarMonthlyRecordResponse toResponse(SolarMonthlyRecord record) {
        return SolarMonthlyRecordResponse.builder()
                .id(record.getId())
                .deviceId(record.getDeviceId())
                .recordYear(record.getRecordYear())
                .recordMonth(record.getRecordMonth())
                .actualKwh(record.getActualKwh())
                .theoreticalKwh(record.getTheoreticalKwh())
                .source(record.getSource())
                .createdAt(record.getCreatedAt())
                .build();
    }


}
