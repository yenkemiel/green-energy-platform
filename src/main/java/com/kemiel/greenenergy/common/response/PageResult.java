package com.kemiel.greenenergy.common.response;

import com.github.pagehelper.PageInfo;
import lombok.Getter;

import java.util.List;

/**
 * 分頁查詢結果容器
 * <p>
 * 將 PageHelper 的 {@link PageInfo} 轉換為統一的分頁回傳格式。
 * 頁碼從 0 開始，與前端慣例一致。
 * </p>
 *
 * @param <T> 分頁資料的型別
 */
@Getter
public class PageResult<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean first;
    private final boolean last;

    private PageResult(PageInfo<T> pageInfo) {
        this.content = pageInfo.getList();
        this.page = pageInfo.getPageNum() - 1;
        this.size = pageInfo.getPageSize();
        this.totalElements = pageInfo.getTotal();
        this.totalPages = pageInfo.getPages();
        this.first = pageInfo.isIsFirstPage();
        this.last = pageInfo.isIsLastPage();
    }

    /**
     * 將 PageHelper 分頁結果轉換為 PageResult
     *
     * @param pageInfo PageHelper 查詢結果
     * @param <T>      資料型別
     * @return 分頁結果容器
     */
    public static <T> PageResult<T> of(PageInfo<T> pageInfo) {
        return new PageResult<>(pageInfo);
    }
}