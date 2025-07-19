package ru.forum.whale.space.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.forum.whale.space.api.util.Messages;

@Getter
@Setter
@NoArgsConstructor
public class UserAuthRequestDto {
    @Pattern(regexp = "^(?!.*[;\\\\/?&#]).*$", message = Messages.USERNAME_CANNOT_CONTAIN)
    @NotBlank(message = Messages.USERNAME_NOT_BLANK)
    @Size(max = 20, message = Messages.USERNAME_TOO_LONG)
    private String username;

    @NotBlank(message = Messages.PASSWORD_NOT_BLANK)
    @Size(max = 100, message = Messages.PASSWORD_TOO_LONG)
    private String password;
}
