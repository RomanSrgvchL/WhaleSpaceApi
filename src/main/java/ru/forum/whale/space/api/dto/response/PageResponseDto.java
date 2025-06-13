package ru.forum.whale.space.api.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class PageResponseDto<T> {
    private List<T> content;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private boolean isLast;
}
