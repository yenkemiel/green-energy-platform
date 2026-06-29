package com.kemiel.greenenergy.module.procurement.service;

import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.module.procurement.dto.*;

/**
 * 採購管理 Service 介面
 */
public interface ProcurementService {

    PageResult<ProcurementResponse> listProcurements(String status, Integer isVoid, int page, int size);

    ProcurementResponse getProcurementById(Long id);

    ProcurementResponse createProcurement(CreateProcurementRequest request, Long operatorId);

    ProcurementResponse updateProcurement(Long id, UpdateProcurementRequest request, Long operatorId);

    void submitProcurement(Long id, Long operatorId);

    void cancelProcurement(Long id, Long operatorId);

    void approveProcurement(Long id, Long operatorId);

    void startProcurement(Long id, Long operatorId);

    void completeProcurement(Long id, CompleteProcurementRequest request, Long operatorId);

    void voidProcurement(Long id, Long operatorId);

    ProcurementSummaryResponse getSummary();

}
