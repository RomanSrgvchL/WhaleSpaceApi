package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.AdminLogDto;
import ru.forum.whale.space.api.dto.response.PageResponseDto;
import ru.forum.whale.space.api.mapper.AdminLogMapper;
import ru.forum.whale.space.api.model.AdminLog;
import ru.forum.whale.space.api.model.LogType;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.AdminLogRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminLogService {
    private final AdminLogRepository adminLogRepository;
    private final AdminLogMapper adminLogMapper;

    public PageResponseDto<AdminLogDto> findAll(Sort sort, int page, int size) {
        Page<AdminLogDto> adminLogsPage = adminLogRepository.findAll(PageRequest.of(page, size, sort))
                .map(this::convertToAdminLogDto);

        return PageResponseDto.<AdminLogDto>builder()
                .content(adminLogsPage.getContent())
                .page(page)
                .size(size)
                .totalPages(adminLogsPage.getTotalPages())
                .totalElements(adminLogsPage.getTotalElements())
                .isLast(adminLogsPage.isLast())
                .build();
    }

    @Transactional
    public void save(User user, String content, LogType logType) {
        AdminLog adminLog = AdminLog.builder()
                .user(user)
                .logContent(content)
                .logType(logType)
                .build();

        adminLogRepository.save(adminLog);
    }

    private AdminLogDto convertToAdminLogDto(AdminLog adminLog) {
        return adminLogMapper.adminLogToAdminLogDto(adminLog);
    }
}
