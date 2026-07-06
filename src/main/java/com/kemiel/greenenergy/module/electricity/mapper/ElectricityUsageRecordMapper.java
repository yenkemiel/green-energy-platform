package com.kemiel.greenenergy.module.electricity.mapper;

import com.kemiel.greenenergy.module.electricity.entity.ElectricityUsageRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 每月用電量記錄 Mapper
 */
@Mapper
public interface ElectricityUsageRecordMapper {

    List<ElectricityUsageRecord> selectListByYear(@Param("recordYear") Integer recordYear);

    ElectricityUsageRecord selectById(Long id);

    ElectricityUsageRecord selectByYearAndMonth(@Param("recordYear") Integer recordYear,
                                                @Param("recordMonth") Integer recordMonth);

    List<ElectricityUsageRecord> selectLockedRecords();

    int insert(ElectricityUsageRecord record);

    int updateById(ElectricityUsageRecord record);

    int updateLockById(@Param("id") Long id,
                       @Param("lockedAt") LocalDateTime lockedAt,
                       @Param("lockedBy") Long lockedBy,
                       @Param("status") String status);
}
