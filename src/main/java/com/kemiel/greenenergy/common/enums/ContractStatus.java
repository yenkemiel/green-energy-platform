package com.kemiel.greenenergy.common.enums;

/**
 * 合約狀態；到期與否由 end_date 動態判斷，不設 EXPIRED 狀態
 */
public enum ContractStatus {
    ACTIVE,
    TERMINATED
}