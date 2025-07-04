package ru.forum.whale.space.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatWithLastMsgDto {
    @NotNull
    private Long id;

    @NotNull
    private UserDto user1;

    @NotNull
    private UserDto user2;

    @NotNull
    private LocalDateTime createdAt;

    private ChatMsgDto lastMessage;
}
