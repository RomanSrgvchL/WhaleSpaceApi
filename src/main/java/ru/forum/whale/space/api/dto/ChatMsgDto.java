package ru.forum.whale.space.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatMsgDto {
    @NotNull
    private Long id;

    @NotNull
    private UserDto sender;

    @NotBlank(message = "Сообщение не должно быть пустым")
    @Size(max = 200, message = "Длина сообщения не должна превышать 200 символов")
    private String content;

    @Size(max = 3, message = "В сообщении не может быть больше 3 изображений")
    private List<String> imageFileNames;

    @NotNull
    private LocalDateTime createdAt;
}
