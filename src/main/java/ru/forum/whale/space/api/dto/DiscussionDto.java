package ru.forum.whale.space.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.forum.whale.space.api.util.Messages;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DiscussionDto {
    @NotNull
    private Long id;

    @NotBlank(message = Messages.DISCUSSION_NOT_BLANK)
    @Size(min = 5, max = 100, message = Messages.DISCUSSION_TITLE_RANGE)
    private String title;

    @NotNull
    private UserLiteDto creator;

    @NotNull
    private LocalDateTime createdAt;

    private List<DiscussionMsgDto> messages;
}
