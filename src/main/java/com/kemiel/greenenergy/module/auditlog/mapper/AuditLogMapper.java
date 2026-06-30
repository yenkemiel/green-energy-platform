package com.kemiel.greenenergy.module.auditlog.mapper;

import com.kemiel.greenenergy.module.auditlog.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Audit Log Mapper
 */
@Mapper
public interface AuditLogMapper {

    List<AuditLog> selectList(@Param("targetTable") String targetTable,
                              @Param("action") String action,
                              @Param("operatorId") Long operatorId,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    int insert(AuditLog auditLog);
}