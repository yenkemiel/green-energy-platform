package com.kemiel.greenenergy.common.exception;

/**
 * 業務邏輯例外
 * 各 Service 層統一拋出此例外
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
