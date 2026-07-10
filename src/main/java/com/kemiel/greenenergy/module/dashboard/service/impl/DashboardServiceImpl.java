package com.kemiel.greenenergy.module.dashboard.service.impl;

import com.kemiel.greenenergy.module.contract.dto.ContractActiveStats;
import com.kemiel.greenenergy.module.contract.entity.Contract;
import com.kemiel.greenenergy.module.contract.mapper.ContractMapper;
import com.kemiel.greenenergy.module.dashboard.dto.DashboardResponse;
import com.kemiel.greenenergy.module.dashboard.dto.ExpiringContractItem;
import com.kemiel.greenenergy.module.dashboard.dto.PendingItemsResponse;
import com.kemiel.greenenergy.module.dashboard.service.DashboardService;
import com.kemiel.greenenergy.module.electricity.entity.ElectricityUsageRecord;
import com.kemiel.greenenergy.module.electricity.mapper.ElectricityUsageRecordMapper;
import com.kemiel.greenenergy.module.greenenergy.calculation.GreenEnergyCalculationService;
import com.kemiel.greenenergy.module.greenenergy.calculation.dto.MonthlySummaryResult;
import com.kemiel.greenenergy.module.greenenergy.entity.MonthlySummarySnapshot;
import com.kemiel.greenenergy.module.greenenergy.mapper.MonthlySummarySnapshotMapper;
import com.kemiel.greenenergy.module.procurement.mapper.ProcurementMapper;
import com.kemiel.greenenergy.module.solar.mapper.SolarMonthlyRecordMapper;
import com.kemiel.greenenergy.module.target.entity.AnnualTarget;
import com.kemiel.greenenergy.module.target.mapper.AnnualTargetMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 缺口儀表板 Service 實作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final GreenEnergyCalculationService calculationService;
    private final MonthlySummarySnapshotMapper snapshotMapper;
    private final AnnualTargetMapper annualTargetMapper;
    private final ContractMapper contractMapper;
    private final ProcurementMapper procurementMapper;
    private final SolarMonthlyRecordMapper solarMonthlyRecordMapper;
    private final ElectricityUsageRecordMapper electricityUsageRecordMapper;

    /**
     * 查詢指定時間維度的缺口儀表板資料，彙整綠電量、達成率、缺口與待處理事項
     *
     * @param period  時間維度（THIS_MONTH / THIS_QUARTER / THIS_YEAR / LAST_YEAR）
     */
    @Override
    public DashboardResponse getDashboard(String period) {
        log.info("查詢缺口儀表板，period={}", period);

        LocalDate today = LocalDate.now();
        int targetYear = resolveTargetYear(period, today);
        List<Integer> months = resolveMonths(period, today);

        BigDecimal totalGreenKwh = BigDecimal.ZERO;
        BigDecimal totalUsageKwh = BigDecimal.ZERO;

        for (Integer month : months) {
            MonthlySummarySnapshot snapshot =
                    snapshotMapper.selectByYearAndMonth(targetYear, month);
            if (snapshot != null) {
                totalGreenKwh = totalGreenKwh.add(snapshot.getTotalGreenKwh());
                totalUsageKwh = totalUsageKwh.add(snapshot.getUsageKwh());
            } else {
                MonthlySummaryResult result =
                        calculationService.calculateMonthlySummary(targetYear, month);
                totalGreenKwh = totalGreenKwh.add(result.getTotalGreenKwh());
                if (result.getUsageKwh() != null) {
                    totalUsageKwh = totalUsageKwh.add(result.getUsageKwh());
                }
            }
        }

        AnnualTarget target = annualTargetMapper.selectByYear(targetYear);
        BigDecimal requiredGreenKwh = (target != null)
                ? calculationService.calculateRequiredGreenKwh(
                target.getAnnualElectricityKwh(), target.getRe100TargetRatio())
                : null;

        BigDecimal achievementRate = null;
        BigDecimal gapKwh = null;
        BigDecimal greenRatio = null;
        BigDecimal gridRatio = null;

        if (totalUsageKwh.compareTo(BigDecimal.ZERO) > 0) {
            achievementRate = calculationService.calculateAchievementRate(
                    totalGreenKwh, totalUsageKwh);
            greenRatio = achievementRate;
            gridRatio = BigDecimal.ONE.subtract(greenRatio);
        }
        if (requiredGreenKwh != null) {
            gapKwh = calculationService.calculateGap(requiredGreenKwh, totalGreenKwh);
        }

        BigDecimal co2ReducedTon = calculationService.calculateCo2Reduced(totalGreenKwh);

        PendingItemsResponse pendingItems = buildPendingItems(today);

        return DashboardResponse.builder()
                .period(period)
                .targetYear(targetYear)
                .requiredGreenKwh(requiredGreenKwh)
                .totalGreenKwh(totalGreenKwh)
                .totalUsageKwh(totalUsageKwh)
                .achievementRate(achievementRate)
                .gapKwh(gapKwh)
                .greenRatio(greenRatio)
                .gridRatio(gridRatio)
                .co2ReducedTon(co2ReducedTon)
                .pendingItems(pendingItems)
                .build();
    }

    /**
     * 根據 period 解析需要彙整的月份清單
     *
     * @param period  時間維度字串（THIS_MONTH / THIS_QUARTER / THIS_YEAR / LAST_YEAR）
     * @param today   當前日期
     * @return        月份清單（1-12）
     */
    private List<Integer> resolveMonths(String period, LocalDate today) {
        int currentMonth = today.getMonthValue();
        return switch (period) {
            case "THIS_MONTH" -> List.of(currentMonth);
            case "THIS_QUARTER" -> {
                int q = (currentMonth - 1) / 3;
                yield List.of(q * 3 + 1, q * 3 + 2, q * 3 + 3);
            }
            case "LAST_YEAR" -> List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
            default -> IntStream.rangeClosed(1, currentMonth)
                    .boxed().collect(Collectors.toList());
        };
    }

    /**
     * 組裝待處理事項，含履約合約、採購進度、缺漏資料、即將到期合約
     */
    private PendingItemsResponse buildPendingItems(LocalDate today) {
        ContractActiveStats stats = contractMapper.selectActiveContractStats();
        int inProgressProcurements = procurementMapper.countInProgress();

        List<String> missingDataItems = new ArrayList<>();
        int currentMonth = today.getMonthValue();
        boolean solarFilled = solarMonthlyRecordMapper.existsByYearAndMonth(
                today.getYear(), currentMonth);
        if (!solarFilled) {
            missingDataItems.add("太陽能發電（" + today.getYear() + "-"
                    + String.format("%02d", currentMonth) + "）");
        }
        ElectricityUsageRecord usageRecord = electricityUsageRecordMapper.selectByYearAndMonth(
                today.getYear(), currentMonth);
        if (usageRecord == null) {
            missingDataItems.add("用電量（" + today.getYear() + "-"
                    + String.format("%02d", currentMonth) + "）");
        }

        LocalDate thresholdDate = today.plusDays(30);
        List<Contract> expiringContracts = contractMapper.selectExpiringContracts(thresholdDate);
        List<ExpiringContractItem> expiringItems = expiringContracts.stream()
                .map(c -> ExpiringContractItem.builder()
                        .contractId(c.getId())
                        .supplierName(c.getSupplierName())
                        .endDate(c.getEndDate())
                        .daysUntilExpiry(ChronoUnit.DAYS.between(today, c.getEndDate()))
                        .build())
                .collect(Collectors.toList());

        return PendingItemsResponse.builder()
                .activeContracts(stats != null ? stats.getActiveContracts() : 0)
                .monthlyGuaranteedKwh(stats != null
                        ? stats.getMonthlyGuaranteedKwh() : BigDecimal.ZERO)
                .inProgressProcurements(inProgressProcurements)
                .missingDataItems(missingDataItems)
                .expiringContracts(expiringItems)
                .build();
    }

    /**
     * 根據 period 解析彙整目標年份，LAST_YEAR 回傳去年，其餘 period 一律回傳今年
     */
    private int resolveTargetYear(String period, LocalDate today) {
        if ("LAST_YEAR".equals(period)) {
            return today.getYear() - 1;
        }
        return today.getYear();
    }

}
