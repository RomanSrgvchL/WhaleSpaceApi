package ru.forum.whale.space.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.forum.whale.space.api.dto.DiscussionMsgDto;
import ru.forum.whale.space.api.model.DiscussionMsg;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DiscussionMsgMapper {
    DiscussionMsgDto discussionMsgToDiscussionMsgDto(DiscussionMsg discussionMsg);
}
