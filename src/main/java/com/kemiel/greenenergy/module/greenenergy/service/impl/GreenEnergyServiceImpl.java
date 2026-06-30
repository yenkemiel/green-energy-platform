package com.kemiel.greenenergy.module.greenenergy.service.impl;

import com.kemiel.greenenergy.common.enums.ContractType;
import com.kemiel.greenenergy.common.enums.GreenEnergySource;
import com.kemiel.greenenergy.common.enums.MonthRecordStatus;
import com.kemiel.greenenergy.common.enums.SupplyType;
import com.kemiel.greenenergy.module.contract.dto.ContractKwhByType;
import com.kemiel.greenenergy.module.contract.mapper.ContractMapper;
import com.kemiel.greenenergy.module.greenenergy.calculation.GreenEnergyCalculationService;
import com.kemiel.greenenergy.module.greenenergy.calculation.dto.CompletenessResult;
import com.kemiel.greenenergy.module.greenenergy.calculation.dto.MonthlySummaryResult;
import com.kemiel.greenenergy.module.greenenergy.dto.CompletenessResponse;
import com.kemiel.greenenergy.module.greenenergy.dto.GreenSummarySourceItem;
import com.kemiel.greenenergy.module.greenenergy.dto.MonthlySummaryResponse;
import com.kemiel.greenenergy.module.greenenergy.entity.MonthlySummarySnapshot;
import com.kemiel.greenenergy.module.greenenergy.mapper.MonthlySummarySnapshotMapper;
import com.kemiel.greenenergy.module.greenenergy.service.GreenEnergyService;
import com.kemiel.greenenergy.module.procurement.dto.ProcurementKwhBySupplyType;
import com.kemiel.greenenergy.module.procurement.mapper.ProcurementMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 綠電來源彙整 Service 實作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GreenEnergyServiceImpl implements GreenEnergyService {

    private final GreenEnergyCalculationService calculationService;
    private final MonthlySummarySnapshotMapper snapshotMapper;
    private final ContractMapper contractMapper;
    private final ProcurementMapper procurementMapper;

    /**
     * 查詢指定月份的綠電彙整結果，LOCKED 月份讀 snapshot，OPEN 月份動態計算
     */
    @Override
    public MonthlySummaryResponse getMonthlySummary(int year, int month) {
        log.info("查詢月度綠電彙整，year={}, month={}", year, month);

        MonthlySummarySnapshot snapshot = snapshotMapper.selectByYearAndMonth(year, month);

        if (snapshot != null) {
            return buildResponseFromSnapshot(snapshot);
        }

        MonthlySummaryResult result = calculationService.calculateMonthlySummary(year, month);
        return buildResponseFromResult(result, year, month);
    }

    /**
     * 查詢指定月份的資料完整度
     */
    @Override
    public CompletenessResponse getCompleteness(int year, int month) {
        log.info("查詢資料完整度，year={}, month={}", year, month);

        CompletenessResult result = calculationService.checkCompleteness(year, month);
        return CompletenessResponse.builder()
                .recordYear(result.getYear())
                .recordMonth(result.getMonth())
                .solarFilled(result.isSolarFilled())
                .usageFilled(result.isUsageFilled())
                .completeness(result.getCompleteness())
                .canLock(result.isCanLock())
                .build();
    }

    /**
     * 從 snapshot 組裝 API Response（LOCKED 狀態）
     */
    private MonthlySummaryResponse buildResponseFromSnapshot(MonthlySummarySnapshot snapshot) {
        List<GreenSummarySourceItem> sources = buildSourcesFromSnapshot(snapshot);
        return MonthlySummaryResponse.builder()
                .recordYear(snapshot.getRecordYear())
                .recordMonth(snapshot.getRecordMonth())
                .status(MonthRecordStatus.LOCKED.name())
                .totalGreenKwh(snapshot.getTotalGreenKwh())
                .solarKwh(snapshot.getSolarKwh())
                .contractKwh(snapshot.getContractKwh())
                .procurementKwh(snapshot.getProcurementKwh())
                .usageKwh(snapshot.getUsageKwh())
                .achievementRate(snapshot.getAchievementRate())
                .surplusKwh(snapshot.getSurplusKwh())
                .completeness(100)
                .sources(sources)
                .build();
    }

    /**
     * 從動態計算結果組裝 API Response（OPEN 狀態）
     */
    private MonthlySummaryResponse buildResponseFromResult(MonthlySummaryResult result,
                                                           int year, int month) {
        List<GreenSummarySourceItem> sources = buildSourcesDynamic(result, year, month);
        return MonthlySummaryResponse.builder()
                .recordYear(year)
                .recordMonth(month)
                .status(result.getStatus())
                .totalGreenKwh(result.getTotalGreenKwh())
                .solarKwh(result.getSolarKwh())
                .contractKwh(result.getContractKwh())
                .procurementKwh(result.getProcurementKwh())
                .usageKwh(result.getUsageKwh())
                .achievementRate(result.getAchievementRate())
                .surplusKwh(result.getSurplusKwh())
                .completeness(result.getCompleteness())
                .sources(sources)
                .build();
    }

    /**
     * LOCKED 狀態：從 snapshot 重組 sources，合約與採購以聚合 kwh 呈現，不拆分細項
     */
    private List<GreenSummarySourceItem> buildSourcesFromSnapshot(MonthlySummarySnapshot snapshot) {
        List<GreenSummarySourceItem> sources = new ArrayList<>();
        sources.add(GreenSummarySourceItem.builder()
                .type(GreenEnergySource.MANUAL.name())
                .label("太陽能實測")
                .kwh(snapshot.getSolarKwh())
                .build());
        sources.add(GreenSummarySourceItem.builder()
                .type(GreenEnergySource.CONTRACT.name())
                .label("合約保證供電")
                .kwh(snapshot.getContractKwh())
                .build());
        sources.add(GreenSummarySourceItem.builder()
                .type(GreenEnergySource.PROCUREMENT.name())
                .label("已完成採購")
                .kwh(snapshot.getProcurementKwh())
                .build());
        return sources;
    }

    /**
     * OPEN 狀態：動態查詢合約 contract_type 與採購 supply_type 拆分細項
     */
    private List<GreenSummarySourceItem> buildSourcesDynamic(MonthlySummaryResult result,
                                                             int year, int month) {
        List<GreenSummarySourceItem> sources = new ArrayList<>();

        sources.add(GreenSummarySourceItem.builder()
                .type(GreenEnergySource.MANUAL.name())
                .label("太陽能實測")
                .kwh(result.getSolarKwh())
                .build());

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        List<ContractKwhByType> contractByType =
                contractMapper.selectSumKwhGroupByType(firstDay, lastDay);
        for (ContractKwhByType c : contractByType) {
            String label = ContractType.CPPA.name().equals(c.getContractType())
                    ? "CPPA 長約" : "售電業供電";
            sources.add(GreenSummarySourceItem.builder()
                    .type(GreenEnergySource.CONTRACT.name())
                    .label(label)
                    .kwh(c.getKwh())
                    .contractType(c.getContractType())
                    .build());
        }

        List<ProcurementKwhBySupplyType> procByType =
                procurementMapper.selectSumKwhGroupBySupplyType(year, month);
        for (ProcurementKwhBySupplyType p : procByType) {
            String label = SupplyType.PHYSICAL.name().equals(p.getSupplyType())
                    ? "已完成採購（實質轉供）" : "已完成採購（純憑證）";
            sources.add(GreenSummarySourceItem.builder()
                    .type(GreenEnergySource.PROCUREMENT.name())
                    .label(label)
                    .kwh(p.getKwh())
                    .supplyType(p.getSupplyType())
                    .build());
        }

        return sources;
    }
}
