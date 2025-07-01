package ru.forum.whale.space.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatRequestDto {
    @NotNull(message = "Не указан ID собеседника")
    private Long partnerId;
}
