package com.sms.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Generic pagination container holding paginated records and metadata.
 * Demonstrates Java Generics with type parameter T.
 * 
 * // [GENERICS] Generic class ResultPage<T>
 */
public class ResultPage<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<T> data;
    private final int pageNo;
    private final int pageSize;
    private final int totalPages;
    private final long totalRecords;

    public ResultPage(List<T> data, int pageNo, int pageSize, long totalRecords) {
        this.data = data != null ? data : Collections.emptyList();
        this.pageNo = pageNo;
        this.pageSize = pageSize > 0 ? pageSize : 10;
        this.totalRecords = totalRecords;
        this.totalPages = (int) Math.ceil((double) totalRecords / this.pageSize);
    }

    public List<T> getData() {
        return data;
    }

    public int getPageNo() {
        return pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public boolean hasPrevious() {
        return pageNo > 1;
    }

    public boolean hasNext() {
        return pageNo < totalPages;
    }

    @Override
    public String toString() {
        return "ResultPage{" +
                "pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", totalPages=" + totalPages +
                ", totalRecords=" + totalRecords +
                ", itemsCount=" + data.size() +
                '}';
    }
}
