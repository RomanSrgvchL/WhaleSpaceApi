package ru.forum.whale.space.api.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.forum.whale.space.api.dto.AdminLogDto;
import ru.forum.whale.space.api.dto.response.PageResponseDto;
import ru.forum.whale.space.api.enums.AdminLogSortFields;
import ru.forum.whale.space.api.enums.SortOrder;
import ru.forum.whale.space.api.service.AdminLogService;
import ru.forum.whale.space.api.util.Messages;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class AdminLogController {
    private final AdminLogService adminLogService;

    @GetMapping
    public ResponseEntity<PageResponseDto<AdminLogDto>> getAll(
            @RequestParam(value = "sort", defaultValue = "CREATED_AT") AdminLogSortFields sort,
            @RequestParam(value = "order", defaultValue = "DESC") SortOrder order,
            @RequestParam(value = "page", defaultValue = "0")
            @PositiveOrZero(message = Messages.PAGE_POSITIVE_OR_ZERO) int page,
            @RequestParam(value = "size", defaultValue = "10")
            @Positive(message = Messages.SIZE_POSITIVE) int size) {
        PageResponseDto<AdminLogDto> adminLogsPage = adminLogService.findAll(Sort.by(order.getDirection(),
                sort.getFieldName()), page, size);
        return ResponseEntity.ok(adminLogsPage);
    }
}
