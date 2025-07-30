package ru.forum.whale.space.api.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.forum.whale.space.api.model.Gender;
import ru.forum.whale.space.api.util.Messages;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserProfileDto {
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Size(max = 120, message = Messages.BIO_TOO_LONG)
    private String bio;
}
