package ru.forum.whale.space.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatDto {
    @NotNull
    private int id;

    private List<MessageDto> messages;

    @NotNull
    private PersonDto user1;

    @NotNull
    private PersonDto user2;

    @NotNull
    private LocalDateTime createdAt;
}
