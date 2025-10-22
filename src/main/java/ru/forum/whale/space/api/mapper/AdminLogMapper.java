package ru.forum.whale.space.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.forum.whale.space.api.dto.AdminLogDto;
import ru.forum.whale.space.api.model.AdminLog;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdminLogMapper {
    AdminLogDto adminLogToAdminLogDto(AdminLog adminLog);
}
