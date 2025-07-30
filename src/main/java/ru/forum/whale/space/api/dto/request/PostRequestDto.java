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
public class PostRequestDto {
    @NotBlank(message = Messages.POST_NOT_BLANK)
    @Size(max = 2000, message = Messages.POST_TOO_LONG)
    private String content;
}
