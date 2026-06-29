package com.kemiel.greenenergy.module.greenenergy.mapper;

import com.kemiel.greenenergy.module.greenenergy.entity.MonthlySummarySnapshot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 月度綠電彙整快照 Mapper
 */
@Mapper
public interface MonthlySummarySnapshotMapper {

    MonthlySummarySnapshot selectByYearAndMonth(@Param("recordYear") Integer recordYear,
                                                @Param("recordMonth") Integer recordMonth);

    int insert(MonthlySummarySnapshot snapshot);
}

