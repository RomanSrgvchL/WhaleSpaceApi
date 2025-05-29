package ru.forum.whale.space.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRequestDto {
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    @Size(max = 20, message = "Имя пользователя не должно содержать более 20 символов")
    String username;

    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(max = 100, message = "Пароль не должен содержать более 100 символов")
    String password;
}
