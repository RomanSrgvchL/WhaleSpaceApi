package ru.forum.whale.space.api.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.forum.whale.space.api.model.Gender;
import ru.forum.whale.space.api.util.Messages;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileDto {
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Size(max = 120, message = Messages.BIO_TOO_LONG)
    private String bio;
}
