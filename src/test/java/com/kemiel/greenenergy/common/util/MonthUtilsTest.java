package com.kemiel.greenenergy.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.YearMonth;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * MonthUtils 單元測試，驗證補填截止判斷與合約有效性判斷邏輯。
 */
@DisplayName("MonthUtils 單元測試")
class MonthUtilsTest {

    @Test
    @DisplayName("isEditable：當月仍在補填期間內，回傳 true")
    void isEditable_withinDeadline_returnsTrue() {
        assertThat(MonthUtils.isEditable(YearMonth.now())).isTrue();
    }

    @Test
    @DisplayName("isEditable：已超過補填截止日，回傳 false")
    void isEditable_pastDeadline_returnsFalse() {
        assertThat(MonthUtils.isEditable(YearMonth.now().minusMonths(2))).isFalse();
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
}