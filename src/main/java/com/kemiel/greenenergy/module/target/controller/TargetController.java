package com.kemiel.greenenergy.module.target.controller;

import com.kemiel.greenenergy.common.response.ApiResponse;
import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.common.util.SecurityUtils;
import com.kemiel.greenenergy.module.target.dto.CreateTargetRequest;
import com.kemiel.greenenergy.module.target.dto.TargetResponse;
import com.kemiel.greenenergy.module.target.dto.UpdateTargetRequest;
import com.kemiel.greenenergy.module.target.service.TargetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 年度目標 Controller，處理 FR-009 ~ FR-012。
 */
@RestController
@RequestMapping("/api/v1/targets")
@RequiredArgsConstructor
@Tag(name = "Targets", description = "年度目標管理模組")
public class TargetController {

    private final TargetService targetService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    @Operation(summary = "[FR-009] 查詢年度目標清單")
    public ResponseEntity<ApiResponse<PageResult<TargetResponse>>> listTargets (
            @RequestParam(required = false) Integer targetYear,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<TargetResponse> result = targetService.listTargets(targetYear, page, size);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "[FR-010] 建立年度目標")
    public ResponseEntity<ApiResponse<TargetResponse>> createTarget(
            @Valid @RequestBody CreateTargetRequest request) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        TargetResponse response = targetService.createTarget(request, operatorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "[FR-011] 修改年度目標")
    public ResponseEntity<ApiResponse<TargetResponse>> updateTarget(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTargetRequest request) {
        Long operatorId =SecurityUtils.getCurrentUserId();
        TargetResponse response = targetService.updateTarget(id, request, operatorId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "[FR-012] 刪除年度目標")
    public ResponseEntity<ApiResponse<Void>> deleteTarget(@PathVariable Long id) {
        targetService.deleteTarget(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

}
