package ru.forum.whale.space.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLiteDto {
    @NotNull
    private Long id;

    @Pattern(
            regexp = "^(?!.*[;\\\\/?&#]).*$",
            message = "Имя пользователя не должно содержать символы ; \\ / ? & #"
    )
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    @Size(max = 20, message = "Имя пользователя не должно содержать более 20 символов")
    private String username;

    private String avatarFileName;
}