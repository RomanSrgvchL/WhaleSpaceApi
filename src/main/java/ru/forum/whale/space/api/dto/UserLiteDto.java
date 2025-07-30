package ru.forum.whale.space.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.forum.whale.space.api.util.Messages;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserLiteDto {
    @NotNull
    private Long id;

    @Pattern(regexp = "^(?!.*[;\\\\/?&#]).*$", message = Messages.USERNAME_CANNOT_CONTAIN)
    @NotBlank(message = Messages.USERNAME_NOT_BLANK)
    @Size(max = 20, message = Messages.USERNAME_TOO_LONG)
    private String username;

    private String avatarFileName;
}