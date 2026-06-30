package com.kemiel.greenenergy.config;

import java.math.BigDecimal;

/**
 * 太陽能發電異常偵測閾值常數
 */
public class AnomalyConfig {

    /**
     * 實際發電量低於理論發電量的比例閾值（低於此比例觸發異常）
     */
    public static final BigDecimal THEORETICAL_THRESHOLD = new BigDecimal("0.50");

    /**
     * 比上月下降的比例閾值（下降超過此比例觸發異常）
     */
    public static final BigDecimal MOM_DECLINE_THRESHOLD = new BigDecimal("0.30");
}
