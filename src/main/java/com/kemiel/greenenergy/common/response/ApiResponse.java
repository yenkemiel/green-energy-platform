package com.kemiel.greenenergy.common.response;


import com.kemiel.greenenergy.common.exception.ErrorCode;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 統一 API 回傳結構
 */
@Getter
public class ApiResponse<T> {
    private final boolean success;
    private final String code;
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    private ApiResponse(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(true, ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode){
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }
}