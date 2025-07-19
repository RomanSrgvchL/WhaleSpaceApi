package ru.forum.whale.space.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.forum.whale.space.api.dto.ChatMsgDto;
import ru.forum.whale.space.api.model.ChatMsg;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChatMsgMapper {
    ChatMsgDto chatMsgToChatMsgDto(ChatMsg chatMsg);
}
