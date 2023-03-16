package com.class1.boot.pojo;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.domain.Sort;

@ToString
public class PageRequest {
    private int pageNumber;
    private int pageSize;
    private Sort.Direction sortDirection;
    private String sortBy;

    public int getPageNumber() {
        return pageNumber;
    }

    public PageRequest setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public PageRequest setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Sort.Direction getSortDirection() {
        return sortDirection;
    }

    public PageRequest setSortDirection(Sort.Direction sortDirection) {
        this.sortDirection = sortDirection;
        return this;
    }

    public String getSortBy() {
        return sortBy;
    }

    public PageRequest setSortBy(String sortBy) {
        this.sortBy = sortBy;
        return this;
    }
}
