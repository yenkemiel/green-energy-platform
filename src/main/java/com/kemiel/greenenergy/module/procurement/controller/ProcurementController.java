package com.kemiel.greenenergy.module.procurement.controller;

import com.kemiel.greenenergy.common.response.ApiResponse;
import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.common.util.SecurityUtils;
import com.kemiel.greenenergy.module.procurement.dto.*;
import com.kemiel.greenenergy.module.procurement.service.ProcurementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 採購管理 Controller
 */
@RestController
@RequestMapping("/api/v1/procurements")
@RequiredArgsConstructor
@Tag(name = "Procurements", description = "採購管理模組")
public class ProcurementController {

    private final ProcurementService procurementService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    @Operation(summary = "[FR-025] 查詢採購清單")
    public ResponseEntity<ApiResponse<PageResult<ProcurementResponse>>> listProcurements(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer isVoid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                procurementService.listProcurements(status, isVoid, page, size)));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "[FR-026] 建立採購草稿")
    public ResponseEntity<ApiResponse<ProcurementResponse>> createProcurement(
            @Valid @RequestBody CreateProcurementRequest request) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(procurementService.createProcurement(request, operatorId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    @Operation(summary = "[FR-027] 查詢單一採購")
    public ResponseEntity<ApiResponse<ProcurementResponse>> getProcurementById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(procurementService.getProcurementById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "[FR-028] 修改採購")
    public ResponseEntity<ApiResponse<ProcurementResponse>> updateProcurement(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProcurementRequest request) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                procurementService.updateProcurement(id, request, operatorId)));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "[FR-029] 採購送出")
    public ResponseEntity<ApiResponse<Void>> submitProcurement(@PathVariable Long id) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        procurementService.submitProcurement(id, operatorId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "[FR-030] 取消採購")
    public ResponseEntity<ApiResponse<Void>> cancelProcurement(@PathVariable Long id) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        procurementService.cancelProcurement(id, operatorId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "[FR-031] 採購審核通過")
    public ResponseEntity<ApiResponse<Void>> approveProcurement(@PathVariable Long id) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        procurementService.approveProcurement(id, operatorId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "[FR-032] 採購開始處理")
    public ResponseEntity<ApiResponse<Void>> startProcurement(@PathVariable Long id) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        procurementService.startProcurement(id, operatorId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "[FR-033] 採購完成")
    public ResponseEntity<ApiResponse<Void>> completeProcurement(
            @PathVariable Long id,
            @Valid @RequestBody CompleteProcurementRequest request) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        procurementService.completeProcurement(id, request, operatorId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping("/{id}/void")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "[FR-034] 採購作廢")
    public ResponseEntity<ApiResponse<Void>> voidProcurement(@PathVariable Long id) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        procurementService.voidProcurement(id, operatorId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    @Operation(summary = "[FR-035] 憑證庫存總覽")
    public ResponseEntity<ApiResponse<ProcurementSummaryResponse>> getSummary() {
        return ResponseEntity.ok(ApiResponse.success(procurementService.getSummary()));
    }
}

