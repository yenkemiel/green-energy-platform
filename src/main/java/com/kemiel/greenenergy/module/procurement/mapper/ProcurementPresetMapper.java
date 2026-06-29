package com.kemiel.greenenergy.module.procurement.mapper;

import com.kemiel.greenenergy.module.procurement.entity.ProcurementPreset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 採購預設方案 Mapper
 */
@Mapper
public interface ProcurementPresetMapper {

    List<ProcurementPreset> selectList();

    ProcurementPreset selectById(Long id);

    int insert(ProcurementPreset preset);

    int updateStatusById(@Param("id") Long id, @Param("isActive") Integer isActive);
}
