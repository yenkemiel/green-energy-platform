package com.kemiel.greenenergy.module.procurement.controller;

import com.kemiel.greenenergy.common.response.ApiResponse;
import com.kemiel.greenenergy.common.util.SecurityUtils;
import com.kemiel.greenenergy.module.procurement.dto.CreateProcurementPresetRequest;
import com.kemiel.greenenergy.module.procurement.dto.ProcurementPresetResponse;
import com.kemiel.greenenergy.module.procurement.dto.UpdateProcurementPresetStatusRequest;
import com.kemiel.greenenergy.module.procurement.service.ProcurementPresetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 採購預設方案 Controller
 */
@RestController
@RequestMapping("/api/v1/procurement-presets")
@RequiredArgsConstructor
@Tag(name = "Procurement Presets", description = "採購預設方案模組")
public class ProcurementPresetController {

    private final ProcurementPresetService procurementPresetService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    @Operation(summary = "[FR-036] 查詢預設採購方案")
    public ResponseEntity<ApiResponse<List<ProcurementPresetResponse>>> listPresets() {
        return ResponseEntity.ok(ApiResponse.success(procurementPresetService.listPresets()));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "[FR-037] 建立預設採購方案")
    public ResponseEntity<ApiResponse<ProcurementPresetResponse>> createPreset(
            @Valid @RequestBody CreateProcurementPresetRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(procurementPresetService.createPreset(request, userId)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "[FR-038] 停用預設採購方案")
    public ResponseEntity<ApiResponse<Void>> updatePresetStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProcurementPresetStatusRequest request) {
        procurementPresetService.updatePresetStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
