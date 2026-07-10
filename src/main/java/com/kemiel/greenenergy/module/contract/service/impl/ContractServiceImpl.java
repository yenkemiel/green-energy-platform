package com.kemiel.greenenergy.module.contract.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kemiel.greenenergy.common.enums.AuditAction;
import com.kemiel.greenenergy.common.enums.ContractStatus;
import com.kemiel.greenenergy.common.exception.BusinessException;
import com.kemiel.greenenergy.common.exception.ErrorCode;
import com.kemiel.greenenergy.common.util.AuditLogHelper;
import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.common.util.MonthLockChecker;
import com.kemiel.greenenergy.module.contract.dto.ContractResponse;
import com.kemiel.greenenergy.module.contract.dto.CreateContractRequest;
import com.kemiel.greenenergy.module.contract.dto.TerminateContractRequest;
import com.kemiel.greenenergy.module.contract.dto.UpdateContractRequest;
import com.kemiel.greenenergy.module.contract.entity.Contract;
import com.kemiel.greenenergy.module.contract.mapper.ContractMapper;
import com.kemiel.greenenergy.module.contract.service.ContractService;
import com.kemiel.greenenergy.module.user.entity.User;
import com.kemiel.greenenergy.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 合約管理 Service 實作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractMapper contractMapper;
    private final AuditLogHelper auditLogHelper;
    private final UserMapper userMapper;
    private final MonthLockChecker monthLockChecker;

    /**
     * 查詢合約清單（支援分頁、狀態與類型篩選）
     */
    @Override
    public PageResult<ContractResponse> listContracts(int page, int size, String status, String contractType) {
        log.info("查詢合約清單，status={}, contractType={}", status, contractType);
        PageHelper.startPage(page + 1, size);
        List<Contract> contracts = contractMapper.selectList(status, contractType);
        PageInfo<Contract> pageInfo = new PageInfo<>(contracts);
        List<ContractResponse> content = contracts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PageResult.of(content, pageInfo);
    }

    /**
     * 依 id 查詢單一合約
     */
    @Override
    public ContractResponse getContractById(Long id) {
        log.info("查詢合約，id={}", id);
        Contract contract =contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException(ErrorCode.CONTRACT_NOT_FOUND);
        }
        return toResponse(contract);
    }

    /**
     * 建立合約，驗證日期順序、檢查同業者期間重疊與月份鎖定衝突，
     * 計算月成本快照並寫入 Audit Log
     */
    @Override
    public ContractResponse createContract(CreateContractRequest request, Long operatorId) {
        log.info("建立合約，supplierName={}, operatorId={}", request.getSupplierName(), operatorId);
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new BusinessException(ErrorCode.CONTRACT_DATE_INVALID);
        }
        int overlapCount = contractMapper.countOverlap(
                request.getSupplierName(), request.getStartDate(), request.getEndDate(), null);
        if (overlapCount > 0) {
            throw new BusinessException(ErrorCode.CONTRACT_OVERLAP);
        }

        monthLockChecker.assertNoLockedMonthInRange(request.getStartDate(), request.getEndDate());

        BigDecimal monthlyCostSnapshot = request.getRatePerKwh()
                .multiply(request.getMonthlySupplyKwh())
                .setScale(4, RoundingMode.HALF_UP);

        Contract contract =new Contract();
        contract.setSupplierName(request.getSupplierName());
        contract.setContractType(request.getContractType().name());
        contract.setMonthlySupplyKwh(request.getMonthlySupplyKwh());
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setRatePerKwh(request.getRatePerKwh());
        contract.setMonthlyCostSnapshot(monthlyCostSnapshot);
        contract.setStatus(ContractStatus.ACTIVE.name());
        contract.setNotes(request.getNotes());
        contract.setCreatedBy(operatorId);

        contractMapper.insert(contract);

        User operator = userMapper.selectById(operatorId);
        String afterValue = String.format(
                "{\"supplierName\": \"%s\", \"contractType\": \"%s\", \"monthlySupplyKwh\": \"%s\"}",
                contract.getSupplierName(), contract.getContractType(), contract.getMonthlySupplyKwh());
        auditLogHelper.logCreate(
                AuditAction.CREATE.name(), "contracts", contract.getId(),
                afterValue, operatorId, operator.getDisplayName());

        log.info("合約建立成功，contractId={}, supplierName={}", contract.getId(), contract.getSupplierName());
        return toResponse(contractMapper.selectById(contract.getId()));
    }

    /**
     * 修改合約（僅限 ACTIVE 狀態，費率不可變更），檢查修改區間是否涵蓋已鎖定月份；
     * 供電區間若有變動，一併檢查舊區間是否涵蓋已鎖定月份，避免把鎖定月份移出區間
     * 而改變其已凍結的數字
     */
    @Override
    public ContractResponse updateContract(Long id, UpdateContractRequest request, Long operatorId) {
        log.info("修改合約，id={}, operatorId={}", id, operatorId);
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException(ErrorCode.CONTRACT_NOT_FOUND);
        }
        if (!ContractStatus.ACTIVE.name().equals(contract.getStatus())) {
            throw new BusinessException(ErrorCode.CONTRACT_NOT_ACTIVE);
        }
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new BusinessException(ErrorCode.CONTRACT_DATE_INVALID);
        }
        int overlapCount = contractMapper.countOverlap(
                request.getSupplierName(), request.getStartDate(), request.getEndDate(), id);
        if (overlapCount > 0) {
            throw new BusinessException(ErrorCode.CONTRACT_OVERLAP);
        }

        monthLockChecker.assertNoLockedMonthInRange(request.getStartDate(), request.getEndDate());

        boolean rangeChanged = !contract.getStartDate().equals(request.getStartDate())
                || !contract.getEndDate().equals(request.getEndDate());
        if (rangeChanged) {
            log.info("合約供電區間變動，一併檢查舊區間鎖定狀態，contractId={}, oldStartDate={}, oldEndDate={}",
                    id, contract.getStartDate(), contract.getEndDate());
            monthLockChecker.assertNoLockedMonthInRange(contract.getStartDate(), contract.getEndDate());
        }

        contract.setSupplierName(request.getSupplierName());
        contract.setMonthlySupplyKwh(request.getMonthlySupplyKwh());
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setNotes(request.getNotes());
        contract.setUpdatedBy(operatorId);

        contractMapper.updateById(contract);
        log.info("合約修改成功，contractId={}", id);
        return toResponse(contractMapper.selectById(id));
    }

    /**
     * 手動終止合約（status -> TERMINATED），檢查終止月份是否已鎖定，並寫入 Audit Log
     */
    @Override
    public void  terminateContract(Long id, TerminateContractRequest request, Long operatorId) {
        log.info("終止合約，id={}, operatorId={}", id, operatorId);
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException(ErrorCode.CONTRACT_NOT_FOUND);
        }
        if (!ContractStatus.ACTIVE.name().equals(contract.getStatus())) {
            throw new BusinessException(ErrorCode.CONTRACT_NOT_ACTIVE);
        }

        monthLockChecker.assertNotLocked(YearMonth.from(request.getTerminatedAt()));

        contract.setStatus(ContractStatus.TERMINATED.name());
        contract.setTerminatedAt(request.getTerminatedAt());
        contract.setTerminatedBy(operatorId);

        contractMapper.updateStatusById(contract);

        User operator = userMapper.selectById(operatorId);
        String terminateAfterValue = String.format(
                "{\"status\": \"%s\", \"terminatedAt\": \"%s\"}",
                ContractStatus.TERMINATED.name(), request.getTerminatedAt());
        auditLogHelper.logCreate(
                AuditAction.TERMINATE.name(), "contracts", id,
                terminateAfterValue, operatorId, operator.getDisplayName());

        log.info("合約終止成功，contractId={}, terminate={}", id, operatorId);
    }

    private ContractResponse toResponse(Contract contract) {
        return ContractResponse.builder()
                .id(contract.getId())
                .supplierName(contract.getSupplierName())
                .contractType(contract.getContractType())
                .monthlySupplyKwh(contract.getMonthlySupplyKwh())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .ratePerKwh(contract.getRatePerKwh())
                .monthlyCostSnapshot(contract.getMonthlyCostSnapshot())
                .status(contract.getStatus())
                .terminatedAt(contract.getTerminatedAt())
                .notes(contract.getNotes())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .build();
    }
}
