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
import ru.forum.whale.space.api.util.Messages;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    @NotNull
    private Long id;

    @Pattern(regexp = "^(?!.*[;\\\\/?&#]).*$", message = Messages.USERNAME_CANNOT_CONTAIN)
    @NotBlank(message = Messages.USERNAME_NOT_BLANK)
    @Size(max = 20, message = Messages.USERNAME_TOO_LONG)
    private String username;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private String role;

    private String avatarFileName;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Size(max = 120, message = Messages.BIO_TOO_LONG)
    private String bio;
}
