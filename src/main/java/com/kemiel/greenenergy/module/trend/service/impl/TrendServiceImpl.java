package com.kemiel.greenenergy.module.trend.service.impl;

import com.kemiel.greenenergy.common.enums.MonthRecordStatus;
import com.kemiel.greenenergy.common.enums.SupplyType;
import com.kemiel.greenenergy.module.greenenergy.calculation.GreenEnergyCalculationService;
import com.kemiel.greenenergy.module.greenenergy.calculation.dto.MonthlySummaryResult;
import com.kemiel.greenenergy.module.procurement.dto.ProcurementKwhBySupplyType;
import com.kemiel.greenenergy.module.procurement.mapper.ProcurementMapper;
import com.kemiel.greenenergy.module.trend.dto.MonthlyTrendItem;
import com.kemiel.greenenergy.module.trend.dto.MonthlyTrendResponse;
import com.kemiel.greenenergy.module.trend.service.TrendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 月度趨勢報表 Service 實作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TrendServiceImpl implements TrendService {

    private final GreenEnergyCalculationService calculationService;
    private final ProcurementMapper procurementMapper;

    /**
     * 查詢指定年度 12 個月的趨勢報表，LOCKED 月份讀 snapshot、OPEN 月份動態計算，
     * 無資料月份回傳空白項目（僅 month 欄位有值，其餘 kwh 欄位為 null）
     *
     * @param year 目標年度
     */
    @Override
    public MonthlyTrendResponse getMonthlyTrend(int year) {
        log.info("查詢月度趨勢報表，year={}", year);

        BigDecimal requiredGreenKwh = calculationService.resolveRequiredGreenKwh(year);

        List<MonthlyTrendItem> months = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            MonthlyTrendItem item = buildTrendItem(year, month, requiredGreenKwh);
            months.add(item);
        }

        return MonthlyTrendResponse.builder()
                .year(year)
                .months(months)
                .build();
    }

    /**
     * 組裝單月趨勢資料，透過 getEffectiveMonthlySummary 取得有效彙整
     * （LOCKED 月份為 snapshot 定案值、OPEN 月份動態計算），無資料回傳空白項目。
     * gapKwh 只依賴年度目標與總綠電量，口徑與 Dashboard 一致；
     * PHYSICAL/REC_ONLY 細項因 snapshot 未儲存型態分解，一律動態查詢——
     * 已知限制：鎖定後採購資料若曾異動（DEFER-005 修復前的舊資料），
     * 細項加總可能與 snapshot 的 procurementKwh 總數不一致
     *
     * @param year             年份
     * @param month            月份
     * @param requiredGreenKwh 年度需要綠電量（null 表示未設定年度目標）
     */
    private MonthlyTrendItem buildTrendItem(int year, int month, BigDecimal requiredGreenKwh) {
        MonthlySummaryResult result = calculationService.getEffectiveMonthlySummary(year, month);
        boolean locked = MonthRecordStatus.LOCKED.name().equals(result.getStatus());

        if (!locked && result.getUsageKwh() == null
                && result.getTotalGreenKwh().compareTo(BigDecimal.ZERO) == 0) {
            return MonthlyTrendItem.builder().month(month).build();
        }

        BigDecimal gapKwh = requiredGreenKwh != null
                ? calculationService.calculateGap(
                requiredGreenKwh.divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP),
                result.getTotalGreenKwh())
                : null;

        BigDecimal physicalKwh = BigDecimal.ZERO;
        BigDecimal recOnlyKwh = BigDecimal.ZERO;
        List<ProcurementKwhBySupplyType> procByType =
                procurementMapper.selectSumKwhGroupBySupplyType(year, month);
        for (ProcurementKwhBySupplyType p : procByType) {
            if (SupplyType.PHYSICAL.name().equals(p.getSupplyType())) {
                physicalKwh = p.getKwh();
            } else if (SupplyType.REC_ONLY.name().equals(p.getSupplyType())) {
                recOnlyKwh = p.getKwh();
            }
        }

        return MonthlyTrendItem.builder()
                .month(month)
                .usageKwh(result.getUsageKwh())
                .totalGreenKwh(result.getTotalGreenKwh())
                .solarKwh(result.getSolarKwh())
                .contractKwh(result.getContractKwh())
                .procurementKwh(result.getProcurementKwh())
                .procurementPhysicalKwh(physicalKwh)
                .procurementRecOnlyKwh(recOnlyKwh)
                .achievementRate(result.getAchievementRate())
                .gapKwh(gapKwh)
                .surplusKwh(result.getSurplusKwh())
                .status(result.getStatus())
                .build();
    }
}