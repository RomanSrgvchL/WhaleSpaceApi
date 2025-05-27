package ru.forum.whale.space.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.forum.whale.space.api.dto.ReplyDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyResponseDto {
    private boolean success;
    private String message;
    private ReplyDto replyDto;

    public ReplyResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
