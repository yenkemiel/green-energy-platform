package com.kemiel.greenenergy.module.solar.mapper;

import com.kemiel.greenenergy.module.solar.entity.SolarMonthlyRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 太陽能設備月發電紀錄 Mapper
 * 對應資料表 solar_monthly_records
 */
@Mapper
public interface SolarMonthlyRecordMapper {
    List<SolarMonthlyRecord> selectListByDeviceIdAndYear(@Param("deviceId") Long deviceId,
                                                                 @Param("year") Integer year);
    SolarMonthlyRecord selectByDeviceIdAndYearMonth(@Param("deviceId") Long deviceId,
                                                                @Param("year") Integer year, @Param("month") Integer month);
    List<Integer> selectDistinctYears(@Param("deviceId") Long deviceId);
    SolarMonthlyRecord selectById(Long id);
    void insert(SolarMonthlyRecord record);
    void updateById(SolarMonthlyRecord record);
}
