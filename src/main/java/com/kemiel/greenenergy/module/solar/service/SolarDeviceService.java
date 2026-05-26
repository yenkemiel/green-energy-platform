package com.kemiel.greenenergy.module.solar.service;

import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.module.solar.dto.CreateSolarDeviceRequest;
import com.kemiel.greenenergy.module.solar.dto.SolarDeviceResponse;
import com.kemiel.greenenergy.module.solar.dto.UpdateSolarDeviceStatusRequest;

/**
 * 太陽能設備管理 Service
 */
public interface SolarDeviceService {

    PageResult<SolarDeviceResponse> listDevices(String status, int page, int size);
    SolarDeviceResponse getDeviceById(Long id);
    SolarDeviceResponse createDevice(CreateSolarDeviceRequest request, Long operatorId);
    void updateDeviceStatus(Long id, UpdateSolarDeviceStatusRequest request);
}
