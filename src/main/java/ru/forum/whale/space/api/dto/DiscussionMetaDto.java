package ru.forum.whale.space.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class DiscussionMetaDto {
    @NotNull
    private Long id;

    @NotBlank(message = "Тема не должна быть пустой")
    @Size(min = 5, max = 100, message = "Длина темы должна быть в диапазоне от 5 до 100 символов")
    private String title;

    @NotNull
    private UserLiteDto creator;

    @NotNull
    private LocalDateTime createdAt;
}
