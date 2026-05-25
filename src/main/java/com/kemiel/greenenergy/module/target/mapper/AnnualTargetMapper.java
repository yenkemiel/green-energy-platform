package com.kemiel.greenenergy.module.target.mapper;

import com.kemiel.greenenergy.module.target.entity.AnnualTarget;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 年度目標 Mapper，對應 annual_targets 資料表。
 */
@Mapper
public interface AnnualTargetMapper {
    AnnualTarget selectById(Long id);
    AnnualTarget selectByYear(Integer targetYear);
    List<AnnualTarget> selectList(@Param("targetYear") Integer targetYear);
    void insert(AnnualTarget target);
    void updateById(AnnualTarget target);
    void deleteById(Long id);
}
