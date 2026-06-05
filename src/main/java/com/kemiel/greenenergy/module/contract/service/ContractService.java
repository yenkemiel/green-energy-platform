package com.kemiel.greenenergy.module.contract.service;

import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.module.contract.dto.ContractResponse;
import com.kemiel.greenenergy.module.contract.dto.CreateContractRequest;
import com.kemiel.greenenergy.module.contract.dto.TerminateContractRequest;
import com.kemiel.greenenergy.module.contract.dto.UpdateContractRequest;

/**
 * 合約管理 Service
 */
public interface ContractService {

    PageResult<ContractResponse> listContracts(int page, int size, String status, String contractType);

    ContractResponse getContractById(Long id);

    ContractResponse createContract(CreateContractRequest request, Long operatorId);

    ContractResponse updateContract(Long id, UpdateContractRequest request, Long operatorId);

    void terminateContract(Long id, TerminateContractRequest request, Long operatorId);
}
