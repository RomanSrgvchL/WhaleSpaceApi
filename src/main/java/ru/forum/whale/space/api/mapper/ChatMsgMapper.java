package ru.forum.whale.space.api.mapper;

import org.mapstruct.Mapper;
import ru.forum.whale.space.api.dto.ChatMsgDto;
import ru.forum.whale.space.api.model.ChatMsg;

@Mapper(componentModel = "spring")
public interface ChatMsgMapper {
    ChatMsgDto chatMsgToChatMsgDto(ChatMsg chatMsg);
}
