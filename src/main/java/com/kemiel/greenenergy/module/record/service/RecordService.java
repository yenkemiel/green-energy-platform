package com.kemiel.greenenergy.module.record.service;

import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.module.record.dto.RecordItemResponse;

import java.time.LocalDate;

/**
 * 總紀錄頁面 Service 介面
 */
public interface RecordService {

    PageResult<RecordItemResponse> listRecords(String type, String status,
                                               LocalDate startDate, LocalDate endDate,
                                               String supplyType, int page, int size);
}
