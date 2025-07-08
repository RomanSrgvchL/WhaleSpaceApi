package ru.forum.whale.space.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostWithCommentsDto {
    @NotNull
    private Long id;

    @NotNull
    private UserLiteDto author;

    @NotBlank(message = "Пост не должно быть пустым")
    @Size(max = 2000, message = "Длина поста не должна превышать 2000 символов")
    private String content;

    @NotNull
    private LocalDateTime createdAt;

    private List<CommentDto> comments;

    private List<Long> likedUserIds;
}
