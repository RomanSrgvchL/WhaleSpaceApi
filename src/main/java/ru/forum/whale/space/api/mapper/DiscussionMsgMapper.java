package ru.forum.whale.space.api.mapper;

import org.mapstruct.Mapper;
import ru.forum.whale.space.api.dto.DiscussionMsgDto;
import ru.forum.whale.space.api.model.DiscussionMsg;

@Mapper(componentModel = "spring")
public interface DiscussionMsgMapper {
    DiscussionMsgDto discussionMsgToDiscussionMsgDto(DiscussionMsg discussionMsg);
}
