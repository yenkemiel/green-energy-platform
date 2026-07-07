package com.kemiel.greenenergy.module.target.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kemiel.greenenergy.common.enums.AuditAction;
import com.kemiel.greenenergy.common.exception.BusinessException;
import com.kemiel.greenenergy.common.exception.ErrorCode;
import com.kemiel.greenenergy.common.util.AuditLogHelper;
import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.module.target.dto.CreateTargetRequest;
import com.kemiel.greenenergy.module.target.dto.TargetResponse;
import com.kemiel.greenenergy.module.target.dto.UpdateTargetRequest;
import com.kemiel.greenenergy.module.target.entity.AnnualTarget;
import com.kemiel.greenenergy.module.target.mapper.AnnualTargetMapper;
import com.kemiel.greenenergy.module.target.service.TargetService;
import com.kemiel.greenenergy.module.user.entity.User;
import com.kemiel.greenenergy.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 年度目標服務實作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TargetServiceImpl implements TargetService {

    private final AnnualTargetMapper annualTargetMapper;
    private final AuditLogHelper auditLogHelper;
    private final UserMapper userMapper;

    /**
     * 查詢年度目標清單，支援依年度篩選
     */
    @Override
    public PageResult<TargetResponse> listTargets(Integer targetYear, int page ,int size) {
        log.info("查詢年度目標清單，targetYear={}, page={}, size={}", targetYear, page, size);
        PageHelper.startPage(page + 1, size);
        List<AnnualTarget> targets = annualTargetMapper.selectList(targetYear);
        PageInfo<AnnualTarget> pageInfo = new PageInfo<>(targets);
        List<TargetResponse> content = targets.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PageResult.of(content, pageInfo);
    }

    /**
     * 建立年度目標，同年度不可重複
     */
    @Override
    public TargetResponse createTarget(CreateTargetRequest request, Long operatorId) {
        log.info("建立年度目標，targetYear={}, operatorId={}", request.getTargetYear(), operatorId);
        AnnualTarget existing = annualTargetMapper.selectByYear(request.getTargetYear());
        if (existing != null) {
            throw new BusinessException(ErrorCode.TARGET_YEAR_DUPLICATE);
        }
        AnnualTarget target =new AnnualTarget();
        target.setTargetYear(request.getTargetYear());
        target.setAnnualElectricityKwh(request.getAnnualElectricityKwh());
        target.setRe100TargetRatio(request.getRe100TargetRatio());
        target.setGrowthRate(request.getGrowthRate());
        target.setCreatedBy(operatorId);
        target.setUpdatedBy(operatorId);
        annualTargetMapper.insert(target);
        log.info("年度目標建立完成，id={}", target.getId());
        return toResponse(annualTargetMapper.selectById(target.getId()));
    }

    /**
     * 修改年度目標內容（targetYear 不可變更），並寫入 Audit Log 記錄異動前後值
     */
    @Override
    public TargetResponse updateTarget(Long id, UpdateTargetRequest request, Long operatorId) {
        log.info("修改年度目標，id={}, operatorId={}", id, operatorId);
        AnnualTarget target = annualTargetMapper.selectById(id);
        if (target == null) {
            throw new BusinessException(ErrorCode.TARGET_NOT_FOUND);
        }

        BigDecimal oldRatio = target.getRe100TargetRatio();
        BigDecimal oldGrowthRate = target.getGrowthRate();

        target.setAnnualElectricityKwh(request.getAnnualElectricityKwh());
        target.setRe100TargetRatio(request.getRe100TargetRatio());
        target.setGrowthRate(request.getGrowthRate());
        target.setUpdatedBy(operatorId);
        annualTargetMapper.updateById(target);

        String beforeValue = String.format(
                "{\"re100TargetRatio\": \"%s\", \"growthRate\": \"%s\"}",
                oldRatio, oldGrowthRate);
        String afterValue = String.format(
                "{\"re100TargetRatio\": \"%s\", \"growthRate\": \"%s\"}",
                request.getRe100TargetRatio(), request.getGrowthRate());
        User operator = userMapper.selectById(operatorId);
        auditLogHelper.log(
                AuditAction.UPDATE.name(), "annual_targets", target.getId(),
                beforeValue, afterValue, operatorId, operator.getDisplayName());

        log.info("年度目標修改完成，id={}", id);
        return toResponse(annualTargetMapper.selectById(id));
    }

    /**
     * 軟刪除年度目標
     */
    @Override
    public void deleteTarget(Long id) {
        log.info("刪除年度目標，id={}", id);
        AnnualTarget target = annualTargetMapper.selectById(id);
        if (target == null) {
            throw new BusinessException(ErrorCode.TARGET_NOT_FOUND);
        }
        annualTargetMapper.deleteById(id);
        log.info("年度目標刪除完成，id={}", id);
    }

    /**
     * 將 AnnualTarget entity 轉為 TargetResponse，轉換過程即時計算 requiredGreenKwh
     * （annualElectricityKwh × re100TargetRatio）
     */
    private TargetResponse toResponse(AnnualTarget target) {
        BigDecimal requiredGreenKwh = target.getAnnualElectricityKwh()
                .multiply(target.getRe100TargetRatio())
                .setScale(4, RoundingMode.HALF_UP);
        return TargetResponse.builder()
                .id(target.getId())
                .targetYear(target.getTargetYear())
                .annualElectricityKwh(target.getAnnualElectricityKwh())
                .re100TargetRatio(target.getRe100TargetRatio())
                .growthRate(target.getGrowthRate())
                .requiredGreenKwh(requiredGreenKwh)
                .createdAt(target.getCreatedAt())
                .updatedAt(target.getUpdatedAt())
                .build();
    }
}
