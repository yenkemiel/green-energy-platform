package com.kemiel.greenenergy.module.record.mapper;

import com.kemiel.greenenergy.module.record.dto.RecordItemResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 總紀錄頁面 Mapper，使用 UNION SQL 彙整合約與採購資料
 */
@Mapper
public interface RecordMapper {

    List<RecordItemResponse> selectList(@Param("type") String type,
                                        @Param("status") String status,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        @Param("supplyType") String supplyType);
}
