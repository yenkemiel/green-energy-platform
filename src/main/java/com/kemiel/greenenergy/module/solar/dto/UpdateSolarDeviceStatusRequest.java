package com.kemiel.greenenergy.module.solar.dto;

import com.kemiel.greenenergy.common.enums.DeviceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 更新太陽能設備狀態請求 DTO
 */
@Getter
public class UpdateSolarDeviceStatusRequest {

    @NotNull(message = "設備狀態不可為空")
    private DeviceStatus status;
}
