package com.kemiel.greenenergy.module.target.service;


import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.module.target.dto.CreateTargetRequest;
import com.kemiel.greenenergy.module.target.dto.TargetResponse;
import com.kemiel.greenenergy.module.target.dto.UpdateTargetRequest;

/**
 * 年度目標服務介面。
 */
public interface TargetService {
    PageResult<TargetResponse> listTargets(Integer targetYear, int page, int size);
    TargetResponse createTarget(CreateTargetRequest request, Long operatorId);
    TargetResponse updateTarget(Long id, UpdateTargetRequest request, Long operatorId);
    void deleteTarget(Long id);
}
