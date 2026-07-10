package com.kemiel.greenenergy.module.contract.mapper;

import com.kemiel.greenenergy.module.contract.dto.ContractActiveStats;
import com.kemiel.greenenergy.module.contract.dto.ContractKwhByType;
import com.kemiel.greenenergy.module.contract.entity.Contract;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 合約 Mapper
 */
@Mapper
public interface ContractMapper {

    List<Contract> selectList(@Param("status") String status,
                              @Param("contractType") String contractType);

    Contract selectById(Long id);

    /**
     * 查詢同供電業者於指定期間內重疊的 ACTIVE 合約數量。
     * 修改時傳入 excludeId 排除自身；建立時傳 null。
     */
    int countOverlap(@Param("supplierName") String supplierName,
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate,
                     @Param("excludeId") Long excludeId);

    void insert(Contract contract);

    void updateById(Contract contract);

    void updateStatusById(Contract contract);

    BigDecimal selectSumMonthlySupplyKwhByMonth(@Param("firstDay") LocalDate firstDay,
                                                @Param("lastDay") LocalDate lastDay);

    List<ContractKwhByType> selectSumKwhGroupByType(@Param("firstDay") LocalDate firstDay,
                                                    @Param("lastDay") LocalDate lastDay);

    ContractActiveStats selectActiveContractStats(@Param("today") LocalDate today);

    List<Contract> selectExpiringContracts(@Param("thresholdDate") LocalDate thresholdDate);

}