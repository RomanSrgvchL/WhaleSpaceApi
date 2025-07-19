package ru.forum.whale.space.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.forum.whale.space.api.dto.ChatDto;
import ru.forum.whale.space.api.dto.ChatWithLastMsgDto;
import ru.forum.whale.space.api.model.Chat;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChatMapper {
    ChatDto chatToChatDto(Chat chat);

    ChatWithLastMsgDto chatToChatWithLastMsgDto(Chat chat);
}
