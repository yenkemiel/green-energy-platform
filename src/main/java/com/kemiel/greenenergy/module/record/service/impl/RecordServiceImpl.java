package com.kemiel.greenenergy.module.record.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kemiel.greenenergy.common.exception.BusinessException;
import com.kemiel.greenenergy.common.exception.ErrorCode;
import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.module.record.dto.RecordItemResponse;
import com.kemiel.greenenergy.module.record.mapper.RecordMapper;
import com.kemiel.greenenergy.module.record.service.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 總紀錄頁面 Service 實作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final RecordMapper recordMapper;

    /**
     * 查詢合約與採購統一紀錄清單，支援類型、狀態、時間區間、供電類型篩選與分頁。
     * type=ALL 時不允許同時傳入 status，否則拋出 RECORD_STATUS_FILTER_INVALID。
     *
     * @param type       資料類型（CONTRACT／PROCUREMENT／ALL）
     * @param status     狀態篩選（type=ALL 時必須為 null）
     * @param startDate  事件日期起始
     * @param endDate    事件日期結束
     * @param supplyType 採購供電類型（PHYSICAL／REC_ONLY）
     * @param page       頁碼（從 0 開始）
     * @param size       每頁筆數
     */
    @Override
    public PageResult<RecordItemResponse> listRecords(String type, String status,
                                                      LocalDate startDate, LocalDate endDate,
                                                      String supplyType, int page, int size) {
        log.info("查詢總紀錄，type={}, status={}, startDate={}, endDate={}, supplyType={}",
                type, status, startDate, endDate, supplyType);

        if ("ALL".equals(type) && status != null) {
            throw new BusinessException(ErrorCode.RECORD_STATUS_FILTER_INVALID);
        }

        String typeParam = "ALL".equals(type) ? null : type;

        PageHelper.startPage(page + 1, size);
        List<RecordItemResponse> list =
                recordMapper.selectList(typeParam, status, startDate, endDate, supplyType);
        PageInfo<RecordItemResponse> pageInfo = new PageInfo<>(list);

        return PageResult.of(list, pageInfo);
    }
}
