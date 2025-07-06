package ru.forum.whale.space.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostCreateRequestDto {
    @NotBlank(message = "Пост не должен быть пустым")
    @Size(max = 2000, message = "Длина поста не должна превышать 2000 символов")
    private String content;
}
