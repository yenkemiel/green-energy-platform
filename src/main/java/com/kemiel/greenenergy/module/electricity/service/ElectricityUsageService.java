package com.kemiel.greenenergy.module.electricity.service;

import com.kemiel.greenenergy.module.electricity.dto.CreateElectricityUsageRequest;
import com.kemiel.greenenergy.module.electricity.dto.ElectricityUsageResponse;
import com.kemiel.greenenergy.module.electricity.dto.UpdateElectricityUsageRequest;

import java.util.List;

/**
 * 每月用電量登記 Service 介面
 */
public interface ElectricityUsageService {

    List<ElectricityUsageResponse> listByYear(Integer recordYear);

    ElectricityUsageResponse createRecord(CreateElectricityUsageRequest request, Long operatorId);

    ElectricityUsageResponse updateRecord(Long id, UpdateElectricityUsageRequest request, Long operatorId);

    void lockRecord(Long id, Long operatorId);
}