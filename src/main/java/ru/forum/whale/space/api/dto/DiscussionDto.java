package ru.forum.whale.space.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.forum.whale.space.api.util.Messages;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DiscussionDto {
    @NotNull
    private Long id;

    @NotBlank(message = Messages.DISCUSSION_NOT_BLANK)
    @Size(min = 5, max = 100, message = Messages.DISCUSSION_TITLE_RANGE)
    private String title;

    @NotNull
    private UserLiteDto creator;

    @NotNull
    private ZonedDateTime createdAt;

    private List<DiscussionMsgDto> messages;
}
