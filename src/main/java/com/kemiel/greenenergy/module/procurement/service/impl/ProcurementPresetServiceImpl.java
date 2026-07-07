package com.kemiel.greenenergy.module.procurement.service.impl;

import com.kemiel.greenenergy.common.exception.BusinessException;
import com.kemiel.greenenergy.common.exception.ErrorCode;
import com.kemiel.greenenergy.module.procurement.dto.CreateProcurementPresetRequest;
import com.kemiel.greenenergy.module.procurement.dto.ProcurementPresetResponse;
import com.kemiel.greenenergy.module.procurement.dto.UpdateProcurementPresetStatusRequest;
import com.kemiel.greenenergy.module.procurement.entity.ProcurementPreset;
import com.kemiel.greenenergy.module.procurement.mapper.ProcurementPresetMapper;
import com.kemiel.greenenergy.module.procurement.service.ProcurementPresetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 採購預設方案 Service 實作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcurementPresetServiceImpl implements ProcurementPresetService {

    private final ProcurementPresetMapper procurementPresetMapper;

    /**
     * 查詢全部採購預設方案清單（含已停用），供快速建立採購選用與方案管理／重新啟用使用
     */
    @Override
    public List<ProcurementPresetResponse> listPresets() {
        log.info("查詢採購預設方案清單");
        return procurementPresetMapper.selectList().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 建立採購預設方案
     */
    @Override
    public ProcurementPresetResponse createPreset(CreateProcurementPresetRequest request, Long userId) {
        log.info("建立採購預設方案，label={}, userId={}", request.getLabel(), userId);

        ProcurementPreset preset = new ProcurementPreset();
        preset.setLabel(request.getLabel());
        preset.setQuantity(request.getQuantity());
        preset.setIsActive(1);
        preset.setCreatedBy(userId);

        procurementPresetMapper.insert(preset);

        log.info("採購預設方案建立成功，presetId={}", preset.getId());
        return toResponse(procurementPresetMapper.selectById(preset.getId()));
    }

    /**
     * 更新採購預設方案啟用狀態
     */
    @Override
    public void updatePresetStatus(Long id, UpdateProcurementPresetStatusRequest request) {
        log.info("更新採購預設方案狀態，id={}, isActive={}", id, request.getIsActive());

        ProcurementPreset preset = procurementPresetMapper.selectById(id);
        if (preset == null) {
            throw new BusinessException(ErrorCode.PROCUREMENT_PRESET_NOT_FOUND);
        }

        procurementPresetMapper.updateStatusById(id, request.getIsActive() ? 1 : 0);
    }

    private ProcurementPresetResponse toResponse(ProcurementPreset p) {
        return ProcurementPresetResponse.builder()
                .id(p.getId())
                .label(p.getLabel())
                .quantity(p.getQuantity())
                .isActive(p.getIsActive())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
