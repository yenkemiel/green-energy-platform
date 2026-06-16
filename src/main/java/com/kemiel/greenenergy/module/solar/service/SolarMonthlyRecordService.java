package com.kemiel.greenenergy.module.solar.service;

import com.kemiel.greenenergy.module.solar.dto.CreateSolarMonthlyRecordRequest;
import com.kemiel.greenenergy.module.solar.dto.SolarMonthlyRecordResponse;
import com.kemiel.greenenergy.module.solar.dto.UpdateSolarMonthlyRecordRequest;

import java.util.List;

/**
 * 太陽能設備月發電紀錄 Service
 */
public interface SolarMonthlyRecordService {
    List<SolarMonthlyRecordResponse> listRecords(Long deviceId, Integer year);
    List<Integer> listYears(Long deviceId);
    SolarMonthlyRecordResponse createRecord(Long deviceId,
                                            CreateSolarMonthlyRecordRequest request,
                                            Long operatorId);
    SolarMonthlyRecordResponse updateRecord(Long deviceId, Long recordId,
                                            UpdateSolarMonthlyRecordRequest request,
                                            Long operatorId);
}
