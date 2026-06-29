package com.kemiel.greenenergy.module.procurement.service;

import com.kemiel.greenenergy.module.procurement.dto.CreateProcurementPresetRequest;
import com.kemiel.greenenergy.module.procurement.dto.ProcurementPresetResponse;
import com.kemiel.greenenergy.module.procurement.dto.UpdateProcurementPresetStatusRequest;

import java.util.List;

/**
 * 採購預設方案 Service 介面
 */
public interface ProcurementPresetService {

    List<ProcurementPresetResponse> listPresets();

    ProcurementPresetResponse createPreset(CreateProcurementPresetRequest request, Long userId);

    void updatePresetStatus(Long id, UpdateProcurementPresetStatusRequest request);
}
