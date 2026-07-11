package com.kemiel.greenenergy.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MonthUtils 單元測試，驗證補填截止判斷與合約有效性判斷邏輯。
 */
@DisplayName("MonthUtils 單元測試")
class MonthUtilsTest {

    @Test
    @DisplayName("isEditable：當月仍在補填期間內，回傳 true")
    void isEditable_withinDeadline_returnsTrue() {
        assertThat(MonthUtils.isEditable(YearMonth.of(2026, 4))).isTrue();
    }

    @Test
    @DisplayName("isEditable：已超過補填截止日，回傳 false")
    void isEditable_pastDeadline_returnsFalse() {
        YearMonth twoMonthsAgo = YearMonth.now().minusMonths(2);
        assertThat(MonthUtils.isEditable(twoMonthsAgo)).isFalse();
    }

    @Test
    @DisplayName("isEditable(Clock)：固定在截止日當天，回傳 true")
    void isEditable_clockOnDeadline_returnsTrue() {
        Clock fixedClock = Clock.fixed(
                LocalDate.of(2026, 2, 5).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());
        assertThat(MonthUtils.isEditable(YearMonth.of(2026, 1), fixedClock)).isTrue();
    }

    @Test
    @DisplayName("isEditable(Clock)：固定在截止日隔天，回傳 false")
    void isEditable_clockAfterDeadline_returnsFalse() {
        Clock fixedClock = Clock.fixed(
                LocalDate.of(2026, 2, 6).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());
        assertThat(MonthUtils.isEditable(YearMonth.of(2026, 1), fixedClock)).isFalse();
    }

    @Test
    @DisplayName("isContractActiveInMonth：合約完整涵蓋該月，回傳 true")
    void isContractActiveInMonth_fullyCovered_returnsTrue() {
        assertThat(MonthUtils.isContractActiveInMonth(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                YearMonth.of(2026, 5)
        )).isTrue();
    }

    @Test
    @DisplayName("isContractActiveInMonth：合約在月中到期，回傳 true")
    void isContractActiveInMonth_endsWithinMonth_returnsTrue() {
        assertThat(MonthUtils.isContractActiveInMonth(
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 15),
                YearMonth.of(2026, 5)
        )).isTrue();
    }

    @Test
    @DisplayName("isContractActiveInMonth：合約在該月前就結束，回傳 false")
    void isContractActiveInMonth_endedBeforeMonth_returnsFalse() {
        assertThat(MonthUtils.isContractActiveInMonth(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 4, 30),
                YearMonth.of(2026, 5)
        )).isFalse();
    }

    @Test
    @DisplayName("isContractActiveInMonth：合約在該月後才開始，回傳 false")
    void isContractActiveInMonth_startsAfterMonth_returnsFalse() {
        assertThat(MonthUtils.isContractActiveInMonth(
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 12, 31),
                YearMonth.of(2026, 5)
        )).isFalse();
    }

    @Test
    @DisplayName("currentMonth(Clock)：1~5 號期間，回傳上個月")
    void currentMonth_clockWithinFillPeriod_returnsPreviousMonth() {
        Clock fixedClock = Clock.fixed(
                LocalDate.of(2026, 8, 3).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());
        assertThat(MonthUtils.currentMonth(fixedClock)).isEqualTo(YearMonth.of(2026, 7));
    }

    @Test
    @DisplayName("currentMonth(Clock)：6 號以後，回傳當月")
    void currentMonth_clockAfterFillPeriod_returnsThisMonth() {
        Clock fixedClock = Clock.fixed(
                LocalDate.of(2026, 8, 6).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());
        assertThat(MonthUtils.currentMonth(fixedClock)).isEqualTo(YearMonth.of(2026, 8));
    }
}