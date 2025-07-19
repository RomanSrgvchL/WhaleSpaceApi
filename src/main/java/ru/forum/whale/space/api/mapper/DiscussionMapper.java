package ru.forum.whale.space.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.forum.whale.space.api.dto.DiscussionDto;
import ru.forum.whale.space.api.dto.DiscussionMetaDto;
import ru.forum.whale.space.api.model.Discussion;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DiscussionMapper {
    DiscussionDto discussionToDiscussionDto(Discussion discussion);

    DiscussionMetaDto discussionToDiscussionMetaDto(Discussion discussion);
}
