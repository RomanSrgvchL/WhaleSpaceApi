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
public class DiscussionMsgRequestDto {
    @NotNull(message = "Не указан ID обсуждения")
    private Long discussionId;

    @NotNull(message = "Не указан ID отправителя")
    private Long senderId;

    @NotBlank(message = "Сообщение не должно быть пустым")
    @Size(max = 200, message = "Длина сообщения не должна превышать 200 символов")
    private String content;
}
