package ru.forum.whale.space.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.forum.whale.space.api.util.Messages;

@Getter
@Setter
@NoArgsConstructor
public class CommentDto {
    @NotNull
    private Long id;

    @NotNull
    private UserLiteDto author;

    @NotBlank(message = Messages.COMMENT_NOT_BLANK)
    @Size(max = 1000, message = Messages.COMMENT_TOO_LONG)
    private String content;

    @NotNull
    private LocalDateTime createdAt;

    private List<Long> likedUserIds;
}
