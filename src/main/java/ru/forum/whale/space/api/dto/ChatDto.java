package ru.forum.whale.space.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChatDto {
    @NotNull
    private Long id;

    @NotNull
    private UserLiteDto user1;

    @NotNull
    private UserLiteDto user2;

    @NotNull
    private ZonedDateTime createdAt;

    private List<ChatMsgDto> messages;
}
