package ru.forum.whale.space.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentRequestDto {
    @NotNull(message = "Не указан ID поста")
    private Long postId;

    @NotBlank(message = "Комментарий не должен быть пустым")
    @Size(max = 1000, message = "Длина комментария не должна превышать 1000 символов")
    private String content;
}