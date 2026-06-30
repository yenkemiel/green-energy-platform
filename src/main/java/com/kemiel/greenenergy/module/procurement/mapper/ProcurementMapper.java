package com.kemiel.greenenergy.module.procurement.mapper;

import com.kemiel.greenenergy.module.procurement.dto.ProcurementKwhBySupplyType;
import com.kemiel.greenenergy.module.procurement.dto.ProcurementSummaryStats;
import com.kemiel.greenenergy.module.procurement.entity.Procurement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 採購記錄 Mapper。
 */
@Mapper
public interface ProcurementMapper {
    List<Procurement> selectList(@Param("status") String status,
                                 @Param("isVoid") Integer isVoid);

    Procurement selectById(Long id);

    int insert(Procurement procurement);

    int updateById(Procurement procurement);

    int updateStatusById(@Param("id") Long id,
                         @Param("status") String status,
                         @Param("updatedBy") Long updatedBy);

    ProcurementSummaryStats selectSummaryStats(LocalDate deadline);

    BigDecimal selectSumKwhEquivalentByCompletedMonth(@Param("year") Integer year,
                                                      @Param("month") Integer month);

    List<ProcurementKwhBySupplyType> selectSumKwhGroupBySupplyType(@Param("year") Integer year,
                                                                   @Param("month") Integer month);

    int countInProgress();

}
