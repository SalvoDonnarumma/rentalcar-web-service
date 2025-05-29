package com.xantrix.webapp.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PageResponse<T> {
    private List<T> data;
    private int currentPage;
    private int pageSize;
    private int totalItems;
    private int totalPages;

    public PageResponse(List<T> data, int currentPage, int pageSize, int totalItems) {
        this.data = data;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
    }
}
