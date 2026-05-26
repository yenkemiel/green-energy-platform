package com.kemiel.greenenergy.module.solar.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 太陽能設備月發電紀錄 Entity，對應 solar_monthly_records 資料表
 */
@Data
public class SolarDevice {

    private Long id;
    private Long deviceId;
    private Integer recordYear;
    private Integer recordMonth;
    private BigDecimal actualKwh;
    private BigDecimal theoreticalKwh;
    private String source;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
