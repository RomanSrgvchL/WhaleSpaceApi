package ru.forum.whale.space.api.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.forum.whale.space.api.dto.DiscussionMsgDto;

@Getter
@Setter
@NoArgsConstructor
public class DiscussionMsgResponseDto extends ResponseDto {
    private DiscussionMsgDto discussionMsgDto;

    public DiscussionMsgResponseDto(boolean success, String message) {
        super(success, message);
    }

    public DiscussionMsgResponseDto(boolean success, String message, DiscussionMsgDto discussionMsgDto) {
        super(success, message);
        this.discussionMsgDto = discussionMsgDto;
    }

    public static DiscussionMsgResponseDto buildFailure(String message) {
        return new DiscussionMsgResponseDto(false, message);
    }

    public static DiscussionMsgResponseDto buildSuccess(String message, DiscussionMsgDto discussionMsgDto) {
        return new DiscussionMsgResponseDto(true, message, discussionMsgDto);
    }
}
