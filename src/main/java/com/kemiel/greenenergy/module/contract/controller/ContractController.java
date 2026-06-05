package com.kemiel.greenenergy.module.contract.controller;

import com.kemiel.greenenergy.common.response.ApiResponse;
import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.common.util.SecurityUtils;
import com.kemiel.greenenergy.module.contract.dto.ContractResponse;
import com.kemiel.greenenergy.module.contract.dto.CreateContractRequest;
import com.kemiel.greenenergy.module.contract.dto.TerminateContractRequest;
import com.kemiel.greenenergy.module.contract.dto.UpdateContractRequest;
import com.kemiel.greenenergy.module.contract.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 合約管理 Controller
 */
@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
@Tag(name = "Contracts", description = "合約管理模組")
public class ContractController {

    private final ContractService contractService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    @Operation(summary = "[FR-020] 查詢合約清單")
    public ResponseEntity<ApiResponse<PageResult<ContractResponse>>> listContracts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String contractType) {
        return ResponseEntity.ok(ApiResponse.success(contractService.listContracts(page, size, status, contractType)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    @Operation(summary = "[FR-022] 查詢單一合約")
    public ResponseEntity<ApiResponse<ContractResponse>> getContractById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(contractService.getContractById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "[FR-021] 建立合約")
    public ResponseEntity<ApiResponse<ContractResponse>> createContract(
            @Valid @RequestBody CreateContractRequest request) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(contractService.createContract(request, operatorId)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "[FR-023] 修改合約")
    public ResponseEntity<ApiResponse<ContractResponse>> updateContract(
            @PathVariable Long id,
            @Valid @RequestBody UpdateContractRequest request) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(contractService.updateContract(id, request, operatorId)));
    }

    @PostMapping("/{id}/terminate")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "[FR-024] 終止合約")
    public ResponseEntity<ApiResponse<Void>> terminateContract(
            @PathVariable Long id,
            @Valid @RequestBody TerminateContractRequest request) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        contractService.terminateContract(id, request,operatorId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
