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
public class CommentDto {
    @NotNull
    private Long id;

    @NotNull
    private UserLiteDto author;

    @NotBlank(message = "Комментарий не должен быть пустым")
    @Size(max = 1000, message = "Комментарий не должен превышать 1000 символов")
    private String content;

    @NotNull
    private LocalDateTime createdAt;

    private List<Long> likedUserIds;
}
