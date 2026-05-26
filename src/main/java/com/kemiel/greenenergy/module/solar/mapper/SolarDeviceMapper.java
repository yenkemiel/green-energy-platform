package com.kemiel.greenenergy.module.solar.mapper;

import com.kemiel.greenenergy.module.solar.entity.SolarDevice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 太陽能設備 Mapper
 */
@Mapper
public interface SolarDeviceMapper {

    SolarDevice selectById(Long id);
    List<SolarDevice> selectList(@Param("status") String status);
    void insert(SolarDevice device);
    void updateStatusById(@Param("id") Long id, @Param("status") String status);
}
