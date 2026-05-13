package com.kemiel.greenenergy.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * CO₂ 減少量換算工具類，依台灣電力碳排放係數計算綠電對應的碳減量。
 */
public class Co2Calculator {

    private static final BigDecimal EMISSION_FACTOR = new BigDecimal("0.494");
    private static final BigDecimal KG_TO_TON = new BigDecimal("1000");
    private static final int SCALE = 4;


    private Co2Calculator() {}

    /**
     * 計算綠電量對應減少的 CO₂ 公噸數，公式：綠電量 × 0.494 ÷ 1000。
     *
     * @param greenKwh 綠電量（kWh）
     * @return 減少的 CO₂ 公噸數，保留 4 位小數
     */
    public static BigDecimal calculate(BigDecimal greenKwh){
        return greenKwh.multiply(EMISSION_FACTOR)
                .divide(KG_TO_TON, SCALE, RoundingMode.HALF_UP);
    }
}
