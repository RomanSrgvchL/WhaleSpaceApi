package ru.forum.whale.space.api.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.forum.whale.space.api.dto.MessageDto;

@Getter
@Setter
@NoArgsConstructor
public class MessageResponseDto extends ResponseDto {
    private MessageDto messageDto;

    public MessageResponseDto(boolean success, String message) {
        super(success, message);
    }

    public MessageResponseDto(boolean success, String message, MessageDto messageDto) {
        super(success, message);
        this.messageDto = messageDto;
    }

    public static MessageResponseDto buildFailure(String message) {
        return new MessageResponseDto(false, message);
    }

    public static MessageResponseDto buildSuccess(String message, MessageDto messageDto) {
        return new MessageResponseDto(true, message, messageDto);
    }
}
