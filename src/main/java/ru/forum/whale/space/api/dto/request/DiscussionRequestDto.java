package ru.forum.whale.space.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.forum.whale.space.api.util.Messages;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DiscussionRequestDto {
    @NotBlank(message = Messages.DISCUSSION_NOT_BLANK)
    @Size(min = 5, max = 100, message = Messages.DISCUSSION_TITLE_RANGE)
    private String title;
}
