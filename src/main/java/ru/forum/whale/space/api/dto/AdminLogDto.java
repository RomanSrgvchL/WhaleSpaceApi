package ru.forum.whale.space.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.forum.whale.space.api.model.LogType;
import ru.forum.whale.space.api.util.Messages;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AdminLogDto {
    @NotNull
    private Long id;

    @NotNull
    private UserLiteDto user;

    @NotBlank(message = Messages.COMMENT_NOT_BLANK)
    @Size(max = 200, message = Messages.COMMENT_TOO_LONG)
    private String logContent;

    @NotNull
    private LogType logType;

    private ZonedDateTime createdAt;
}
