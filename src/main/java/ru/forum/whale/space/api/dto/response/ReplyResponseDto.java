package ru.forum.whale.space.api.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.forum.whale.space.api.dto.ReplyDto;

@Getter
@Setter
@NoArgsConstructor
public class ReplyResponseDto extends UserResponseDto {
    private ReplyDto replyDto;

    public ReplyResponseDto(boolean success, String message) {
        super(success, message);
    }

    public ReplyResponseDto(boolean success, String message, ReplyDto replyDto) {
        super(success, message);
        this.replyDto = replyDto;
    }
}
