package com.kemiel.greenenergy.module.solar.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kemiel.greenenergy.common.enums.DeviceStatus;
import com.kemiel.greenenergy.common.exception.BusinessException;
import com.kemiel.greenenergy.common.exception.ErrorCode;
import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.module.solar.dto.CreateSolarDeviceRequest;
import com.kemiel.greenenergy.module.solar.dto.SolarDeviceResponse;
import com.kemiel.greenenergy.module.solar.dto.UpdateSolarDeviceStatusRequest;
import com.kemiel.greenenergy.module.solar.entity.SolarDevice;
import com.kemiel.greenenergy.module.solar.mapper.SolarDeviceMapper;
import com.kemiel.greenenergy.module.solar.service.SolarDeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 太陽能設備管理 Service 實作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SolarDeviceServiceImpl implements SolarDeviceService {

    private static final BigDecimal DAILY_SUN_HOURS = new BigDecimal("3.5");

    private final SolarDeviceMapper solarDeviceMapper;

    @Override
    public PageResult<SolarDeviceResponse> listDevices(String status, int page, int size) {
        log.info("查詢太陽能設備清單，status={}, page={}, size={}", status, page, size);
        PageHelper.startPage(page + 1, size);
        List<SolarDevice> devices = solarDeviceMapper.selectList(status);
        PageInfo<SolarDevice> pageInfo = new PageInfo<>(devices);
        List<SolarDeviceResponse> content = devices.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PageResult.of(content, pageInfo);

    }

    @Override
    public SolarDeviceResponse getDeviceById(Long id) {
        log.info("查詢太陽能設備，id={}", id);
        SolarDevice device = solarDeviceMapper.selectById(id);
        if (device == null) {
            throw new BusinessException(ErrorCode.SOLAR_DEVICE_NOT_FOUND);
        }
        return toResponse(device);
    }

    @Override
    public SolarDeviceResponse createDevice(CreateSolarDeviceRequest request, Long operatorId) {
        log.info("新增太陽能設備，deviceName={}, operatorId={}", request.getDeviceName(), operatorId);
        SolarDevice device = new SolarDevice();
        device.setDeviceName(request.getDeviceName());
        device.setCapacityKw(request.getCapacityKw());
        device.setInstallDate(request.getInstallDate());
        device.setLocation(request.getLocation());
        device.setCreatedBy(operatorId);
        solarDeviceMapper.insert(device);
        log.info("太陽能設備新增完成，id={}", device.getId());
        return toResponse(solarDeviceMapper.selectById(device.getId()));
    }

    /**
     * 更新設備狀態，設備已停用時拋出 SOLAR_DEVICE_ALREADY_INACTIVE
     */
    @Override
    public void updateDeviceStatus(Long id, UpdateSolarDeviceStatusRequest request) {
        log.info("更新太陽能設備狀態，id={}, status={}", id, request.getStatus());
        SolarDevice device = solarDeviceMapper.selectById(id);
        if (device == null) {
            throw new BusinessException(ErrorCode.SOLAR_DEVICE_NOT_FOUND);
        }
        if (device.getStatus() == DeviceStatus.INACTIVE) {
            throw new BusinessException(ErrorCode.SOLAR_DEVICE_ALREADY_INACTIVE);
        }
        solarDeviceMapper.updateStatusById(id, request.getStatus().name());
        log.info("太陽能設備狀態更新完成，id={}, status={}", id, request.getStatus());
    }

    private SolarDeviceResponse toResponse(SolarDevice device) {
        int daysInMonth = YearMonth.now().lengthOfMonth();
        BigDecimal theoreticalMonthlyKwh = device.getCapacityKw()
                .multiply(DAILY_SUN_HOURS)
                .multiply(BigDecimal.valueOf(daysInMonth))
                .setScale(4, RoundingMode.HALF_UP);
        return SolarDeviceResponse.builder()
                .id(device.getId())
                .deviceName(device.getDeviceName())
                .capacityKw(device.getCapacityKw())
                .installDate(device.getInstallDate())
                .location(device.getLocation())
                .status(device.getStatus())
                .theoreticalMonthlyKwh(theoreticalMonthlyKwh)
                .createdAt(device.getCreatedAt())
                .updatedAt(device.getUpdatedAt())
                .build();
    }
}
