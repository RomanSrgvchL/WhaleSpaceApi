package ru.forum.whale.space.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.forum.whale.space.api.util.Messages;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MessageRequestDto {
    @NotBlank(message = Messages.MSG_NOT_BLANK)
    @Size(max = 200, message = Messages.MSG_TOO_LONG)
    String content;
}
