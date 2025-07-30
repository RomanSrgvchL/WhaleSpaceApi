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
public class ChatMsgDto {
    @NotNull
    private Long id;

    @NotNull
    private UserLiteDto sender;

    @NotBlank(message = Messages.MSG_NOT_BLANK)
    @Size(max = 200, message = Messages.MSG_TOO_LONG)
    private String content;

    @Size(max = 3, message = Messages.MSG_IMAGES_LIMIT)
    private List<String> imageFileNames;

    @NotNull
    private ZonedDateTime createdAt;
}
