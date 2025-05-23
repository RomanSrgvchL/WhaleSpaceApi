package ru.forum.whale.space.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.forum.whale.space.api.dto.MessageDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDto {
    private boolean success;
    private String message;
    private MessageDto messageDto;

    public MessageResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
