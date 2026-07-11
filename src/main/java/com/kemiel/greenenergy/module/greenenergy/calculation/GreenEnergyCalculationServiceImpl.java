package com.kemiel.greenenergy.module.greenenergy.calculation;

import com.kemiel.greenenergy.common.enums.ElectricityRecordStatus;
import com.kemiel.greenenergy.common.enums.MonthRecordStatus;
import com.kemiel.greenenergy.common.exception.BusinessException;
import com.kemiel.greenenergy.common.exception.ErrorCode;
import com.kemiel.greenenergy.module.contract.mapper.ContractMapper;
import com.kemiel.greenenergy.module.electricity.entity.ElectricityUsageRecord;
import com.kemiel.greenenergy.module.electricity.mapper.ElectricityUsageRecordMapper;
import com.kemiel.greenenergy.module.greenenergy.calculation.dto.*;
import com.kemiel.greenenergy.module.greenenergy.entity.MonthlySummarySnapshot;
import com.kemiel.greenenergy.module.greenenergy.mapper.MonthlySummarySnapshotMapper;
import com.kemiel.greenenergy.module.procurement.mapper.ProcurementMapper;
import com.kemiel.greenenergy.module.solar.mapper.SolarMonthlyRecordMapper;
import com.kemiel.greenenergy.module.target.entity.AnnualTarget;
import com.kemiel.greenenergy.module.target.mapper.AnnualTargetMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 跨模組綠電計算 Service 實作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GreenEnergyCalculationServiceImpl implements GreenEnergyCalculationService {

    private final SolarMonthlyRecordMapper solarMonthlyRecordMapper;
    private final ContractMapper contractMapper;
    private final ProcurementMapper procurementMapper;
    private final ElectricityUsageRecordMapper electricityUsageRecordMapper;
    private final MonthlySummarySnapshotMapper monthlySummarySnapshotMapper;
    private final AnnualTargetMapper annualTargetMapper;

    private static final BigDecimal CO2_FACTOR = new BigDecimal("0.494");
    private static final BigDecimal ONE_THOUSAND = BigDecimal.valueOf(1000);

    /**
     * 從三個綠電來源（太陽能、合約、採購）動態計算指定月份的彙整結果，包含達成率、結餘與資料完整度
     */
    @Override
    public MonthlySummaryResult calculateMonthlySummary(int year, int month) {
        log.info("計算月度綠電彙整，year={}, month={}", year, month);

        ElectricityUsageRecord usageRecord =
                electricityUsageRecordMapper.selectByYearAndMonth(year, month);

        BigDecimal usageKwh = (usageRecord != null) ? usageRecord.getUsageKwh() : null;

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        BigDecimal solarKwh =
                solarMonthlyRecordMapper.selectSumActualKwhByYearAndMonth(year, month);
        BigDecimal contractKwh =
                contractMapper.selectSumMonthlySupplyKwhByMonth(firstDay, lastDay);
        BigDecimal procurementKwh =
                procurementMapper.selectSumKwhEquivalentByCompletedMonth(year, month);

        BigDecimal totalGreenKwh = solarKwh.add(contractKwh).add(procurementKwh);

        BigDecimal achievementRate = null;
        BigDecimal surplusKwh = BigDecimal.ZERO;

        if (usageKwh != null && usageKwh.compareTo(BigDecimal.ZERO) > 0) {
            achievementRate = calculateAchievementRate(totalGreenKwh, usageKwh);
            surplusKwh = calculateSurplus(totalGreenKwh, usageKwh);
        }

        boolean solarFilled = solarMonthlyRecordMapper.existsByYearAndMonth(year, month);
        boolean usageFilled = (usageRecord != null);
        int completeness = (solarFilled && usageFilled) ? 100 : 0;

        String status = (usageRecord != null &&
                ElectricityRecordStatus.LOCKED.name().equals(usageRecord.getStatus()))
                ? MonthRecordStatus.LOCKED.name()
                : MonthRecordStatus.OPEN.name();

        return MonthlySummaryResult.builder()
                .year(year)
                .month(month)
                .solarKwh(solarKwh)
                .contractKwh(contractKwh)
                .procurementKwh(procurementKwh)
                .totalGreenKwh(totalGreenKwh)
                .usageKwh(usageKwh)
                .achievementRate(achievementRate)
                .surplusKwh(surplusKwh)
                .completeness(completeness)
                .status(status)
                .build();
    }

    /**
     * 查詢指定月份的資料完整度，判斷太陽能與用電量是否已填
     */
    @Override
    public CompletenessResult checkCompleteness(int year, int month) {
        log.info("查詢資料完整度，year={}, month={}", year, month);

        boolean solarFilled = solarMonthlyRecordMapper.existsByYearAndMonth(year, month);
        ElectricityUsageRecord usageRecord =
                electricityUsageRecordMapper.selectByYearAndMonth(year, month);
        boolean usageFilled = (usageRecord != null);
        int completeness = (solarFilled && usageFilled) ? 100 : 0;
        boolean canLock = completeness == 100;

        return CompletenessResult.builder()
                .year(year)
                .month(month)
                .solarFilled(solarFilled)
                .usageFilled(usageFilled)
                .completeness(completeness)
                .canLock(canLock)
                .build();
    }

    /**
     * 計算達成率，最多回傳 1.0，usageKwh 為零時回傳 0.0
     */
    @Override
    public BigDecimal calculateAchievementRate(BigDecimal totalGreenKwh, BigDecimal usageKwh) {
        if (usageKwh == null || usageKwh.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = totalGreenKwh.divide(usageKwh, 4, RoundingMode.HALF_UP);
        return rate.compareTo(BigDecimal.ONE) > 0 ? BigDecimal.ONE : rate;
    }

    /**
     * 計算年度缺口，結果最小為 0
     */
    @Override
    public BigDecimal calculateGap(BigDecimal requiredGreenKwh, BigDecimal totalGreenKwh) {
        BigDecimal gap = requiredGreenKwh.subtract(totalGreenKwh);
        return gap.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : gap;
    }

    /**
     * 計算當月結餘，總綠電超過用電量的部分，最小為 0
     */
    @Override
    public BigDecimal calculateSurplus(BigDecimal totalGreenKwh, BigDecimal usageKwh) {
        if (usageKwh == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal surplus = totalGreenKwh.subtract(usageKwh);
        return surplus.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : surplus;
    }

    /**
     * 計算 CO₂ 減少量（公噸），以台灣電力碳排放係數 0.494 換算
     */
    @Override
    public BigDecimal calculateCo2Reduced(BigDecimal greenKwh) {
        return greenKwh.multiply(CO2_FACTOR)
                .divide(ONE_THOUSAND, 2, RoundingMode.HALF_UP);
    }

    /**
     * 計算年度需要綠電量
     *
     * @param annualElectricityKwh  年度預估總用電量
     * @param re100TargetRatio      RE100 目標比例（0.0 ~ 1.0）
     */
    @Override
    public BigDecimal calculateRequiredGreenKwh(BigDecimal annualElectricityKwh,
                                                BigDecimal re100TargetRatio) {
        return annualElectricityKwh.multiply(re100TargetRatio)
                .setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * 鎖定月份時計算並寫入 monthly_summary_snapshots；同年月 snapshot 已存在則拋出
     * SNAPSHOT_ALREADY_EXISTS，防止重複鎖定時二次覆蓋既有快照
     *
     * @param year     年份
     * @param month    月份
     * @param lockedBy 操作者 user id
     */
    @Override
    @Transactional
    public void writeMonthlySummarySnapshot(int year, int month, Long lockedBy) {
        log.info("寫入月度快照，year={}, month={}, lockedBy={}", year, month, lockedBy);

        MonthlySummarySnapshot existing =
                monthlySummarySnapshotMapper.selectByYearAndMonth(year, month);
        if (existing != null) {
            throw new BusinessException(ErrorCode.SNAPSHOT_ALREADY_EXISTS);
        }

        MonthlySummaryResult result = calculateMonthlySummary(year, month);

        MonthlySummarySnapshot snapshot = new MonthlySummarySnapshot();
        snapshot.setRecordYear(year);
        snapshot.setRecordMonth(month);
        snapshot.setTotalGreenKwh(result.getTotalGreenKwh());
        snapshot.setSolarKwh(result.getSolarKwh());
        snapshot.setContractKwh(result.getContractKwh());
        snapshot.setProcurementKwh(result.getProcurementKwh());
        snapshot.setUsageKwh(result.getUsageKwh() != null
                ? result.getUsageKwh() : BigDecimal.ZERO);
        snapshot.setAchievementRate(result.getAchievementRate() != null
                ? result.getAchievementRate() : BigDecimal.ZERO);
        snapshot.setSurplusKwh(result.getSurplusKwh());
        snapshot.setLockedBy(lockedBy);
        snapshot.setLockedAt(LocalDateTime.now());

        monthlySummarySnapshotMapper.insert(snapshot);
        log.info("月度快照寫入成功，year={}, month={}", year, month);
    }

    /**
     * 以線性回歸外推年底預估達成率，僅取月份連續序列（遇第一個缺漏月份即停止累積）；
     * 已鎖定月份取 snapshot 的定案達成率，未鎖定月份動態計算，
     * 未達年底的月份以外推值補全並限縮在 [0, 1]；預估年底缺口以年度預估用電量
     * （非已依目標比例折算的 requiredGreenKwh）乘以預估達成率推導預估綠電量，
     * 再與 requiredGreenKwh 計算差額，避免目標比例重複套用導致缺口失真
     *
     * @param year 目標年度
     */
    @Override
    public PredictionResult predictYearEnd(int year) {
        log.info("執行年底達成率線性外推，year={}", year);

        Map<Integer, BigDecimal> monthlyRates = new LinkedHashMap<>();

        for (int month = 1; month <= 12; month++) {
            MonthlySummaryResult result = getEffectiveMonthlySummary(year, month);
            if (result.getUsageKwh() == null) {
                break;
            }
            if (result.getAchievementRate() != null) {
                monthlyRates.put(month, result.getAchievementRate());
            }
        }

        int n = monthlyRates.size();

        if (n < 3) {
            log.info("資料不足，無法預測，basedOnMonths={}", n);
            return PredictionResult.builder()
                    .predictable(false)
                    .basedOnMonths(n)
                    .note("歷史資料不足，無法預測（至少需要 3 個月）")
                    .build();
        }

        List<Integer> months = new ArrayList<>(monthlyRates.keySet());
        List<BigDecimal> rates = new ArrayList<>(monthlyRates.values());

        BigDecimal sumX = BigDecimal.ZERO;
        BigDecimal sumY = BigDecimal.ZERO;
        BigDecimal sumXY = BigDecimal.ZERO;
        BigDecimal sumX2 = BigDecimal.ZERO;
        BigDecimal bigN = BigDecimal.valueOf(n);

        for (int i = 0; i < n; i++) {
            BigDecimal x = BigDecimal.valueOf(months.get(i));
            BigDecimal y = rates.get(i);
            sumX = sumX.add(x);
            sumY = sumY.add(y);
            sumXY = sumXY.add(x.multiply(y));
            sumX2 = sumX2.add(x.multiply(x));
        }

        BigDecimal slope = (bigN.multiply(sumXY).subtract(sumX.multiply(sumY)))
                .divide(bigN.multiply(sumX2).subtract(sumX.multiply(sumX)),
                        8, RoundingMode.HALF_UP);
        BigDecimal intercept = (sumY.subtract(slope.multiply(sumX)))
                .divide(bigN, 8, RoundingMode.HALF_UP);

        BigDecimal totalRate = BigDecimal.ZERO;
        for (int month = 1; month <= 12; month++) {
            BigDecimal rate;
            if (monthlyRates.containsKey(month)) {
                rate = monthlyRates.get(month);
            } else {
                rate = slope.multiply(BigDecimal.valueOf(month)).add(intercept);
                rate = rate.min(BigDecimal.ONE).max(BigDecimal.ZERO);
            }
            totalRate = totalRate.add(rate);
        }

        BigDecimal predictedYearEndRate = totalRate
                .divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP);

        BigDecimal currentAchievementRate = rates.get(rates.size() - 1);

        AnnualTarget target = annualTargetMapper.selectByYear(year);
        BigDecimal predictedYearEndGapKwh = null;
        if (target != null) {
            BigDecimal requiredGreenKwh = calculateRequiredGreenKwh(
                    target.getAnnualElectricityKwh(), target.getRe100TargetRatio());
            BigDecimal predictedYearEndGreenKwh = target.getAnnualElectricityKwh()
                    .multiply(predictedYearEndRate)
                    .setScale(4, RoundingMode.HALF_UP);
            predictedYearEndGapKwh = calculateGap(requiredGreenKwh, predictedYearEndGreenKwh);
        }

        String ratePercent = predictedYearEndRate
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP)
                .toPlainString();
        String gapNote = predictedYearEndGapKwh != null
                ? "，仍有缺口約 " + predictedYearEndGapKwh.setScale(0, RoundingMode.HALF_UP)
                                   .toPlainString() + " kWh"
                : "";
        String note = "依線性趨勢推估，年底預估達成率約 " + ratePercent + "%" + gapNote;

        return PredictionResult.builder()
                .predictable(true)
                .basedOnMonths(n)
                .currentAchievementRate(currentAchievementRate)
                .predictedYearEndRate(predictedYearEndRate)
                .predictedYearEndGapKwh(predictedYearEndGapKwh)
                .note(note)
                .build();
    }

    /**
     * 模擬增加合約供電或採購後的年度達成率變化，結果不儲存
     *
     * @param currentGreenKwh               當前累積綠電量
     * @param currentUsageKwh               當前累積用電量
     * @param requiredGreenKwh              年度需要綠電量
     * @param additionalContractKwh         假設增加的合約供電量
     * @param additionalProcurementQuantity  假設增加的 T-REC 採購張數
     */
    @Override
    public SimulationResult simulate(BigDecimal currentGreenKwh,
                                     BigDecimal currentUsageKwh,
                                     BigDecimal requiredGreenKwh,
                                     BigDecimal additionalContractKwh,
                                     int additionalProcurementQuantity) {
        log.info("執行 RE100 達成模擬，additionalContractKwh={}, additionalProcurementQuantity={}",
                additionalContractKwh, additionalProcurementQuantity);

        BigDecimal additionalProcurementKwh =
                BigDecimal.valueOf(additionalProcurementQuantity).multiply(ONE_THOUSAND);

        BigDecimal simulatedAdditionalKwh =
                additionalContractKwh.add(additionalProcurementKwh);
        BigDecimal simulatedTotalGreenKwh =
                currentGreenKwh.add(simulatedAdditionalKwh);

        BigDecimal currentAchievementRate =
                calculateAchievementRate(currentGreenKwh, currentUsageKwh);
        BigDecimal simulatedAchievementRate =
                calculateAchievementRate(simulatedTotalGreenKwh, currentUsageKwh);
        BigDecimal simulatedGapKwh = (requiredGreenKwh != null)
                ? calculateGap(requiredGreenKwh, simulatedTotalGreenKwh)
                : null;

        return SimulationResult.builder()
                .currentGreenKwh(currentGreenKwh)
                .currentAchievementRate(currentAchievementRate)
                .simulatedAdditionalKwh(simulatedAdditionalKwh)
                .simulatedTotalGreenKwh(simulatedTotalGreenKwh)
                .simulatedAchievementRate(simulatedAchievementRate)
                .simulatedGapKwh(simulatedGapKwh)
                .breakdown(SimulationBreakdown.builder()
                        .additionalContractKwh(additionalContractKwh)
                        .additionalProcurementKwh(additionalProcurementKwh)
                        .build())
                .build();
    }

    /**
     * 取得指定月份的有效彙整結果：已鎖定（存在 snapshot）讀取快照定案值，
     * 未鎖定則動態計算。快照月份的 status 固定為 LOCKED、completeness 固定為 100
     * （快照未儲存完整度，鎖定即視為定案）；快照的 usageKwh 與 achievementRate
     * 依寫入慣例不會是 null（無值時存 0）
     */
    @Override
    public MonthlySummaryResult getEffectiveMonthlySummary(int year, int month) {
        log.info("查詢有效月度彙整，year={}, month={}", year, month);

        MonthlySummarySnapshot snapshot =
                monthlySummarySnapshotMapper.selectByYearAndMonth(year, month);
        if (snapshot == null) {
            return calculateMonthlySummary(year, month);
        }
        return MonthlySummaryResult.builder()
                .year(year)
                .month(month)
                .solarKwh(snapshot.getSolarKwh())
                .contractKwh(snapshot.getContractKwh())
                .procurementKwh(snapshot.getProcurementKwh())
                .totalGreenKwh(snapshot.getTotalGreenKwh())
                .usageKwh(snapshot.getUsageKwh())
                .achievementRate(snapshot.getAchievementRate())
                .surplusKwh(snapshot.getSurplusKwh())
                .completeness(100)
                .status(MonthRecordStatus.LOCKED.name())
                .build();
    }

    /**
     * 查詢指定年度目標並推導年度需要綠電量，未設定年度目標時回傳 null
     */
    @Override
    public BigDecimal resolveRequiredGreenKwh(int year) {
        log.info("查詢年度目標並推導需要綠電量，year={}", year);
        AnnualTarget target = annualTargetMapper.selectByYear(year);
        if (target == null) {
            return null;
        }
        return calculateRequiredGreenKwh(
                target.getAnnualElectricityKwh(), target.getRe100TargetRatio());
    }

}
