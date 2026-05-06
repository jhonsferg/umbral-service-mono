package com.codesoftlabs.umbral.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PaginatedResponseDto<T> {
    private List<T> data;
    private Meta meta;

    public PaginatedResponseDto(List<T> data, long total, int page, int limit, int totalPages) {
        this.data = data;
        this.meta = new Meta(total, page, limit, totalPages);
    }

    @Setter
    @Getter
    public static class Meta {
        private long total;
        private int page;
        private int limit;
        private int totalPages;

        public Meta(long total, int page, int limit, int totalPages) {
            this.total = total;
            this.page = page;
            this.limit = limit;
            this.totalPages = totalPages;
        }
    }
}