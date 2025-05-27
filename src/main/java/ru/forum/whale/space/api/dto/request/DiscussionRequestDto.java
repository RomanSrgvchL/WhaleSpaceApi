package ru.forum.whale.space.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DiscussionRequestDto {
    @NotBlank(message = "Тема не должна быть пустой")
    @Size(min = 5, max = 100, message = "Длина темы должна быть в диапазоне от 5 до 100 символов")
    private String title;
}
