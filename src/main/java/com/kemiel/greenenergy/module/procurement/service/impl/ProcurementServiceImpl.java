package com.kemiel.greenenergy.module.procurement.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kemiel.greenenergy.common.enums.ProcurementStatus;
import com.kemiel.greenenergy.common.exception.BusinessException;
import com.kemiel.greenenergy.common.exception.ErrorCode;
import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.module.procurement.dto.*;
import com.kemiel.greenenergy.module.procurement.entity.Procurement;
import com.kemiel.greenenergy.module.procurement.mapper.ProcurementMapper;
import com.kemiel.greenenergy.module.procurement.service.ProcurementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 採購管理 Service 實作
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProcurementServiceImpl implements ProcurementService {

    private final ProcurementMapper procurementMapper;

    /**
     * 查詢採購清單（支援分頁、狀態與作廢篩選）
     */
    @Override
    public PageResult<ProcurementResponse> listProcurements(String status, Integer isVoid, int page, int size) {
        log.info("查詢採購清單，status={}, isVoid={}, page={}, size={}", status, isVoid, page, size);
        PageHelper.startPage(page + 1, size);
        List<Procurement> procurements = procurementMapper.selectList(status, isVoid);
        PageInfo<Procurement> pageInfo = new PageInfo<>(procurements);
        List<ProcurementResponse> content = procurements.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PageResult.of(content, pageInfo);
    }

    /**
     * 依 id 查詢單一採購
     */
    @Override
    public ProcurementResponse getProcurementById(Long id) {
        Procurement procurement = procurementMapper.selectById(id);
        if(procurement == null) {
            throw  new BusinessException(ErrorCode.PROCUREMENT_NOT_FOUND);
        }
        return toResponse(procurement);
    }

    /**
     * 建立採購草稿，計算 kwh_equivalent 與 total_amount
     */
    @Override
    public ProcurementResponse createProcurement(CreateProcurementRequest request, Long operatorId) {
        log.info("建立採購草稿，supplierName={},operator={}", request.getSupplierName(), operatorId);
        BigDecimal kwhEquivalent = BigDecimal.valueOf(request.getQuantity())
                                            .multiply(BigDecimal.valueOf(1000));
        BigDecimal totalAmount = request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        Procurement procurement = new Procurement();
        procurement.setSupplierName(request.getSupplierName());
        procurement.setQuantity(request.getQuantity());
        procurement.setKwhEquivalent(kwhEquivalent);
        procurement.setCertificateYear(request.getCertificateYear());
        procurement.setUnitPrice(request.getUnitPrice());
        procurement.setTotalAmount(totalAmount);
        procurement.setStatus(ProcurementStatus.DRAFT.name());
        procurement.setExpectedDate(request.getExpectedDate());
        procurement.setNotes(request.getNotes());
        procurement.setSupplyType(request.getSupplyType());
        procurement.setIsVoid(0);
        procurement.setCreatedBy(operatorId);
        procurement.setUpdatedBy(operatorId);

        procurementMapper.insert(procurement);

        log.info("採購草稿建立成功，procurement={}", procurement.getId());
        return toResponse(procurementMapper.selectById(procurement.getId()));
    }

    /**
     * 修改採購（僅限 DRAFT 狀態）
     */
    @Override
    public ProcurementResponse updateProcurement(Long id, UpdateProcurementRequest request, Long operatorId) {
        log.info("修改採購，id={}, operator={}", id, operatorId);

        Procurement procurement = procurementMapper.selectById(id);
        if (procurement == null) {
            throw new BusinessException(ErrorCode.PROCUREMENT_NOT_FOUND);
        }
        if(!ProcurementStatus.DRAFT.name().equals(procurement.getStatus())) {
            throw new BusinessException(ErrorCode.PROCUREMENT_STATUS_INVALID);
        }

        BigDecimal kwhEquivalent = BigDecimal.valueOf(request.getQuantity())
                .multiply(BigDecimal.valueOf(1000));
        BigDecimal totalAmount = request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        procurement.setSupplierName(request.getSupplierName());
        procurement.setQuantity(request.getQuantity());
        procurement.setKwhEquivalent(kwhEquivalent);
        procurement.setCertificateYear(request.getCertificateYear());
        procurement.setUnitPrice(request.getUnitPrice());
        procurement.setTotalAmount(totalAmount);
        procurement.setExpectedDate(request.getExpectedDate());
        procurement.setNotes(request.getNotes());
        procurement.setUpdatedBy(operatorId);

        procurementMapper.updateById(procurement);

        log.info("採購修改成功，id={}",id);
        return toResponse(procurementMapper.selectById(id));
    }

    /**
     * 送出採購，草稿狀態改為已送出
     */
    @Override
    public void submitProcurement(Long id, Long operatorId) {
        log.info("送出採購，id={}, operatorId={}", id, operatorId);

        Procurement procurement = procurementMapper.selectById(id);
        if (procurement == null) {
            throw new BusinessException(ErrorCode.PROCUREMENT_NOT_FOUND);
        }
        if (!ProcurementStatus.DRAFT.name().equals(procurement.getStatus())) {
            throw new BusinessException(ErrorCode.PROCUREMENT_STATUS_INVALID);
        }

        procurementMapper.updateStatusById(id, ProcurementStatus.SUBMITTED.name(), operatorId);
    }

    /**
     * 取消採購，草稿或已送出狀態改為已取消
     */
    @Override
    public void cancelProcurement(Long id, Long operatorId) {
        log.info("取消採購，id={}, operatorId={}", id, operatorId);

        Procurement procurement = procurementMapper.selectById(id);
        if (procurement == null) {
            throw new BusinessException(ErrorCode.PROCUREMENT_NOT_FOUND);
        }
        String status = procurement.getStatus();
        if (!ProcurementStatus.DRAFT.name().equals(status) && !ProcurementStatus.SUBMITTED.name().equals(status)) {
            throw new BusinessException(ErrorCode.PROCUREMENT_STATUS_INVALID);
        }

        procurementMapper.updateStatusById(id, ProcurementStatus.CANCELLED.name(), operatorId);
    }

    /**
     * 審核採購，已送出狀態改為審核通過
     */
    @Override
    public void approveProcurement(Long id, Long operatorId) {
        log.info("審核採購，id={}, operatorId={}", id, operatorId);

        Procurement procurement = procurementMapper.selectById(id);
        if (procurement == null) {
            throw new BusinessException(ErrorCode.PROCUREMENT_NOT_FOUND);
        }
        if (!ProcurementStatus.SUBMITTED.name().equals(procurement.getStatus())) {
            throw new BusinessException(ErrorCode.PROCUREMENT_STATUS_INVALID);
        }

        procurementMapper.updateStatusById(id, ProcurementStatus.APPROVED.name(), operatorId);
    }

    /**
     * 開始處理採購，審核通過狀態改為處理中
     */
    @Override
    public void startProcurement(Long id, Long operatorId) {
        log.info("開始處理採購，id={}, operatorId={}", id, operatorId);

        Procurement procurement = procurementMapper.selectById(id);
        if (procurement == null) {
            throw new BusinessException(ErrorCode.PROCUREMENT_NOT_FOUND);
        }
        if (!ProcurementStatus.APPROVED.name().equals(procurement.getStatus())) {
            throw new BusinessException(ErrorCode.PROCUREMENT_STATUS_INVALID);
        }

        procurementMapper.updateStatusById(id, ProcurementStatus.IN_PROGRESS.name(), operatorId);
    }

    /**
     * 完成採購，處理中狀態改為已完成，寫入完成日期並計算 purchase_month 與 expiry_date
     */
    @Override
    public void completeProcurement(Long id, CompleteProcurementRequest request, Long operatorId) {
        log.info("完成採購，id={}, completedDate={}, operatorId={}", id, request.getCompletedDate(), operatorId);

        Procurement procurement = procurementMapper.selectById(id);
        if (procurement == null) {
            throw new BusinessException(ErrorCode.PROCUREMENT_NOT_FOUND);
        }
        if (!ProcurementStatus.IN_PROGRESS.name().equals(procurement.getStatus())) {
            throw new BusinessException(ErrorCode.PROCUREMENT_STATUS_INVALID);
        }

        LocalDate completedDate = request.getCompletedDate();
        String purchaseMonth = YearMonth.from(completedDate).toString();
        LocalDate expiryDate = completedDate.plusYears(2);

        procurement.setStatus(ProcurementStatus.COMPLETED.name());
        procurement.setCompletedDate(completedDate);
        procurement.setPurchaseMonth(purchaseMonth);
        procurement.setExpiryDate(expiryDate);
        procurement.setUpdatedBy(operatorId);

        procurementMapper.updateById(procurement);
    }

    /**
     * 作廢採購，已完成狀態改為作廢並標記 is_void
     */
    @Override
    public void voidProcurement(Long id, Long operatorId) {
        log.info("作廢採購，id={}, operatorId={}", id, operatorId);

        Procurement procurement = procurementMapper.selectById(id);
        if (procurement == null) {
            throw new BusinessException(ErrorCode.PROCUREMENT_NOT_FOUND);
        }
        if (!ProcurementStatus.COMPLETED.name().equals(procurement.getStatus())) {
            throw new BusinessException(ErrorCode.PROCUREMENT_STATUS_INVALID);
        }

        procurement.setStatus(ProcurementStatus.VOID.name());
        procurement.setIsVoid(1);
        procurement.setUpdatedBy(operatorId);
        procurementMapper.updateById(procurement);
    }

    /**
     * 查詢憑證庫存總覽，usedQuantity 暫時固定為 0，待模組 07 整合
     */
    @Override
    public ProcurementSummaryResponse getSummary() {
        LocalDate deadline = LocalDate.now().plusDays(30);
        ProcurementSummaryStats stats = procurementMapper.selectSummaryStats(deadline);

        if (stats == null) {
            stats = new ProcurementSummaryStats();
            stats.setTotalQuantity(0);
            stats.setExpiringWithin30Days(0);
        }

        int usedQuantity = 0;
        int availableQuantity = stats.getTotalQuantity() - usedQuantity;

        return ProcurementSummaryResponse.builder()
                .totalQuantity(stats.getTotalQuantity())
                .usedQuantity(usedQuantity)
                .availableQuantity(availableQuantity)
                .expiringWithin30Days(stats.getExpiringWithin30Days())
                .build();
    }

    private ProcurementResponse toResponse(Procurement procurement) {
        return ProcurementResponse.builder()
                .id(procurement.getId())
                .supplierName(procurement.getSupplierName())
                .quantity(procurement.getQuantity())
                .kwhEquivalent(procurement.getKwhEquivalent())
                .certificateYear(procurement.getCertificateYear())
                .unitPrice(procurement.getUnitPrice())
                .totalAmount(procurement.getTotalAmount())
                .status(procurement.getStatus())
                .expectedDate(procurement.getExpectedDate())
                .completedDate(procurement.getCompletedDate())
                .purchaseMonth(procurement.getPurchaseMonth())
                .expiryDate(procurement.getExpiryDate())
                .isVoid(procurement.getIsVoid())
                .notes(procurement.getNotes())
                .supplyType(procurement.getSupplyType())
                .createdAt(procurement.getCreatedAt())
                .updatedAt(procurement.getUpdatedAt())
                .build();
    }

}
