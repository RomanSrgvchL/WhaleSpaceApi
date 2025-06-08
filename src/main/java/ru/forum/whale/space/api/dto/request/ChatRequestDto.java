package ru.forum.whale.space.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatRequestDto {
    @NotNull(message = "Не указан ID первого пользователя")
    private Integer user1Id;

    @NotNull(message = "Не указан ID второго пользователя")
    private Integer user2Id;
}
