package ru.forum.whale.space.api.mapper;

import org.mapstruct.Mapper;
import ru.forum.whale.space.api.dto.ChatDto;
import ru.forum.whale.space.api.dto.ChatWithLastMsgDto;
import ru.forum.whale.space.api.model.Chat;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    ChatDto chatToChatDto(Chat chat);

    ChatWithLastMsgDto chatToChatWithLastMsgDto(Chat chat);
}
