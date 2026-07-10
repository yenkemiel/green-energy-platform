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

    /**
     * 更新採購內容，以 expectedStatus 作為更新條件防止並發下的狀態覆蓋，
     * 回傳實際更新筆數（0 表示狀態已被其他操作變更）。
     */
    int updateById(@Param("procurement") Procurement procurement,
                   @Param("expectedStatus") String expectedStatus);

    /**
     * 更新採購狀態，以 expectedStatus 作為更新條件防止並發下的重複流轉，
     * 回傳實際更新筆數（0 表示狀態已被其他操作變更）。
     */
    int updateStatusById(@Param("id") Long id,
                         @Param("status") String status,
                         @Param("expectedStatus") String expectedStatus,
                         @Param("updatedBy") Long updatedBy);

    ProcurementSummaryStats selectSummaryStats(@Param("today") LocalDate today,
                                               @Param("deadline") LocalDate deadline);

    BigDecimal selectSumKwhEquivalentByCompletedMonth(@Param("year") Integer year,
                                                      @Param("month") Integer month);

    List<ProcurementKwhBySupplyType> selectSumKwhGroupBySupplyType(@Param("year") Integer year,
                                                                   @Param("month") Integer month);

    int countInProgress();

}
