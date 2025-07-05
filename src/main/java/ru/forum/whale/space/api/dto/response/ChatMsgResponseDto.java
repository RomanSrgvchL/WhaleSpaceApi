package ru.forum.whale.space.api.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.forum.whale.space.api.dto.ChatMsgDto;

@Getter
@Setter
@NoArgsConstructor
public class ChatMsgResponseDto extends ResponseDto {
    private ChatMsgDto chatMsgDto;

    public ChatMsgResponseDto(boolean success, String message) {
        super(success, message);
    }

    public ChatMsgResponseDto(boolean success, String message, ChatMsgDto chatMsgDto) {
        super(success, message);
        this.chatMsgDto = chatMsgDto;
    }

    public static ChatMsgResponseDto buildFailure(String message) {
        return new ChatMsgResponseDto(false, message);
    }

    public static ChatMsgResponseDto buildSuccess(String message, ChatMsgDto chatMsgDto) {
        return new ChatMsgResponseDto(true, message, chatMsgDto);
    }
}
