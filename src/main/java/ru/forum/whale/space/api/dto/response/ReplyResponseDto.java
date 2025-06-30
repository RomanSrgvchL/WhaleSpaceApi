package ru.forum.whale.space.api.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.forum.whale.space.api.dto.ReplyDto;

@Getter
@Setter
@NoArgsConstructor
public class ReplyResponseDto extends ResponseDto {
    private ReplyDto replyDto;

    public ReplyResponseDto(boolean success, String message) {
        super(success, message);
    }

    public ReplyResponseDto(boolean success, String message, ReplyDto replyDto) {
        super(success, message);
        this.replyDto = replyDto;
    }

    public static ReplyResponseDto buildFailure(String message) {
        return new ReplyResponseDto(false, message);
    }

    public static ReplyResponseDto buildSuccess(String message, ReplyDto replyDto) {
        return new ReplyResponseDto(true, message, replyDto);
    }
}
