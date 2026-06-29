package com.kemiel.greenenergy.common.exception;

import com.kemiel.greenenergy.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全域例外處理器
 * 統一攔截例外並轉換為 ApiResponse 格式回傳
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 處理業務邏輯例外
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage());
        return ResponseEntity
                .status(resolveHttpStatus(e.getErrorCode()))
                .body(ApiResponse.error(e.getErrorCode()));
    }

    /**
     * 處理請求參數驗證失敗
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handlerValidationException(MethodArgumentNotValidException e){
        FieldError fieldError = e.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String message = fieldError != null ? fieldError.getDefaultMessage() : ErrorCode.VALIDATION_ERROR.getMessage();
        log.error("ValidationException: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR, message));
    }

    /**
     * 處理權限不足
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e){
        log.error("AccessDeniedException: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ErrorCode.FORBIDDEN));
    }

    /**
     * 處理未預期例外
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handlerException(Exception e){
        log.error("UnexpectedException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.INTERNAL_ERROR));
    }

    /**
     * 依 ErrorCode 對應 HTTP Status
     */
    private HttpStatus resolveHttpStatus(ErrorCode errorCode){
        return switch (errorCode){
            case UNAUTHORIZED,
                 INVALID_CREDENTIALS-> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case USER_NOT_FOUND,
                 SOLAR_DEVICE_NOT_FOUND,
                 SOLAR_RECORD_NOT_FOUND,
                 TARGET_NOT_FOUND,
                 CONTRACT_NOT_FOUND,
                 PROCUREMENT_NOT_FOUND,
                 PROCUREMENT_PRESET_NOT_FOUND,
                 NOTIFICATION_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case USERNAME_ALREADY_EXISTS,
                 TARGET_YEAR_DUPLICATE,
                 SOLAR_RECORD_DUPLICATE,
                 CONTRACT_OVERLAP -> HttpStatus.CONFLICT;
            case SOLAR_DEVICE_ALREADY_INACTIVE,
                 ELECTRICITY_RECORD_EXPIRED,
                 CONTRACT_NOT_ACTIVE,
                 PROCUREMENT_STATUS_INVALID-> HttpStatus.UNPROCESSABLE_ENTITY;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
