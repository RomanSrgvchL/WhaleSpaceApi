package ru.forum.whale.space.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.forum.whale.space.api.util.Messages;

@Getter
@Setter
@NoArgsConstructor
public class ChatRequestDto {
    @Positive(message = Messages.ID_POSITIVE)
    @NotNull(message = "Не указан ID собеседника")
    private Long partnerId;
}
