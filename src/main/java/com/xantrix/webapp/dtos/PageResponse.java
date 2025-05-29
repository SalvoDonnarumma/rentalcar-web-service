package com.xantrix.webapp.dtos;

import java.util.List;

public class PageResponse<T> {
    private List<T> data;
    private int currentPage;
    private int pageSize;
    private int totalItems;
    private int totalPages;

    // Costruttore, getter e setter
    public PageResponse(List<T> data, int currentPage, int pageSize, int totalItems) {
        this.data = data;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
    }
}
