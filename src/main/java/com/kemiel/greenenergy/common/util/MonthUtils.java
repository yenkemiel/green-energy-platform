package com.kemiel.greenenergy.common.util;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * 月份相關工具類，提供補填截止判斷、合約有效性判斷與當前歸屬月份計算
 */
public class MonthUtils {

    private static final int EDITABLE_DAY_OF_MONTH = 5;

    private MonthUtils() {}

    /**
     * 判斷指定月份是否仍在可補填期間（次月 5 號含當天）
     *
     * @param yearMonth 欲判斷的月份
     * @return 可補填回傳 true，已超過截止日回傳 false
     */
    public static boolean isEditable(YearMonth yearMonth) {
        return isEditable(yearMonth, Clock.systemDefaultZone());
    }

    /**
     * 判斷指定月份是否仍在可補填期間（次月 5 號含當天），可注入 Clock 供測試控制目前時間
     *
     * @param yearMonth 欲判斷的月份
     * @param clock     用於取得「現在時間」的 Clock，測試時可傳入固定時間
     * @return 可補填回傳 true，已超過截止日回傳 false
     */
    public static boolean isEditable(YearMonth yearMonth, Clock clock) {
        LocalDate today = LocalDate.now(clock);
        LocalDate deadline = yearMonth.plusMonths(1).atDay(EDITABLE_DAY_OF_MONTH);
        return !today.isAfter(deadline);
    }

    /**
     * 判斷合約在指定月份是否有效（合約期間與該月份有任何重疊即視為有效）
     */
    public static boolean isContractActiveInMonth(LocalDate startDate, LocalDate endDate, YearMonth month) {
        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();
        return !startDate.isAfter(monthEnd) && !endDate.isBefore(monthStart);
    }

    /**
     * 取得當前歸屬月份，5 號含當天以前回傳上個月，6 號以後回傳本月
     *
     * @return 當前歸屬月份
     */
    public static YearMonth currentMonth() {
        return currentMonth(Clock.systemDefaultZone());
    }

    /**
     * 取得當前歸屬月份，5 號含當天以前回傳上個月，6 號以後回傳本月，可注入 Clock 供測試控制目前時間
     *
     * @param clock 用於取得「現在時間」的 Clock，測試時可傳入固定時間
     * @return 當前歸屬月份
     */
    public static YearMonth currentMonth(Clock clock) {
        LocalDate today = LocalDate.now(clock);
        YearMonth thisMonth = YearMonth.now(clock);
        if (today.getDayOfMonth() <= EDITABLE_DAY_OF_MONTH) {
            return thisMonth.minusMonths(1);
        }
        return thisMonth;
    }
}