package ru.forum.whale.space.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatRequestDto {
    @Positive(message = "ID должен быть положительным")
    @NotNull(message = "Не указан ID собеседника")
    private Long partnerId;
}
