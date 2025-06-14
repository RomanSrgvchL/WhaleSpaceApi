package ru.forum.whale.space.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatWithLastMessageDto {
    @NotNull
    private Integer id;

    @NotNull
    private PersonDto user1;

    @NotNull
    private PersonDto user2;

    @NotNull
    private LocalDateTime createdAt;

    private MessageDto lastMessage;
}
