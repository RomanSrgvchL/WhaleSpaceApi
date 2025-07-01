package ru.forum.whale.space.api.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.forum.whale.space.api.model.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    @NotNull
    private Long id;

    @Pattern(
            regexp = "^(?!.*[;\\\\/?&#]).*$",
            message = "Имя пользователя не должно содержать символы ; \\ / ? & #"
    )
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    @Size(max = 20, message = "Имя пользователя не должно содержать более 20 символов")
    private String username;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private String role;

    private String avatarFileName;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Pattern(regexp = "^(?!\\s*$).+", message = "Био не должен быть пустым")
    @Size(max = 120, message = "Био не должен содержать более 120 символов")
    private String bio;
}
