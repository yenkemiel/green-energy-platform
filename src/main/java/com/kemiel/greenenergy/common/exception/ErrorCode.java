package com.kemiel.greenenergy.common.exception;

/**
 * 系統錯誤代碼枚舉
 */
public enum ErrorCode {
    // 通用
    SUCCESS("SUCCESS", "操作成功"),
    INTERNAL_ERROR("INTERNAL_ERROR", "系統錯誤，請稍後再試"),
    VALIDATION_ERROR("VALIDATION_ERROR", "請求參數驗證失敗"),
    UNAUTHORIZED("UNAUTHORIZED", "請先登入"),
    FORBIDDEN("FORBIDDEN", "您無此操作權限"),

    // 帳號
    USER_NOT_FOUND("USER_NOT_FOUND","使用者不存在"),
    USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS","帳號已存在"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS","帳號或密碼錯誤"),
    PASSWORD_SAME_AS_OLD("PASSWORD_SAME_AS_OLD", "新密碼不可與舊密碼相同"),

    // 合約
    CONTRACT_NOT_FOUND("CONTRACT_NOT_FOUND", "合約不存在"),
    CONTRACT_OVERLAP("CONTRACT_OVERLAP", "合約期間與既有合約重疊"),
    CONTRACT_NOT_ACTIVE("CONTRACT_NOT_ACTIVE", "合約狀態非有效，無法執行此操作"),
    CONTRACT_DATE_INVAILD("CONTRACT_DATE_INVALID", "合約起訖日期設定錯誤"),

    // 採購
    PROCUREMENT_NOT_FOUND("PROCUREMENT_NOT_FOUND", "採購單不存在"),
    PROCUREMENT_STATUS_INVALID("PROCUREMENT_STATUS_INVALID", "採購單狀態不允許此操作"),

    // 太陽能
    SOLAR_DEVICE_NOT_FOUND("SOLAR_DEVICE_NOT_FOUND", "太陽能設備不存在"),
    SOLAR_RECORD_DUPLICATE("SOLAR_RECORD_DUPLICATE", "該設備當月發電紀錄已存在"),

    // 用電量
    ELECTRICITY_RECORD_DUPLICATE("ELECTRICITY_RECORD_DUPLICATE", "當月用電量紀錄已存在"),
    ELECTRICITY_RECORD_LOCKED("ELECTRICITY_RECORD_LOCKED", "當月紀錄已鎖定，無法修改"),
    ELECTRICITY_RECORD_EXPIRED("ELECTRICITY_RECORD_EXPIRED", "超過可編輯期限，無法修改"),

    // 月結
    MONTH_NOT_COMPLETE("MONTH_NOT_COMPLETE", "當月資料尚未完整，無法鎖定"),
    MONTH_ALREADY_LOCKED("MONTH_ALREADY_LOCKED", "當月已鎖定，無法重複操作"),

    // 目標
    TARGET_NOT_FOUND("TARGET_NOT_FOUND", "當年度目標不存在"),
    TARGET_YEAR_DUPLICATE("TARGET_YEAR_DUPLICATE", "該年度目標已存在"),

    // 通知
    NOTIFICATION_NOT_FOUND("NOTIFICATION_NOT_FOUND", "通知不存在")
    ;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}