package ru.forum.whale.space.api.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.forum.whale.space.api.model.Gender;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileDto {
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Size(max = 120, message = "Био не должен содержать более 120 символов")
    @Column(name = "bio")
    private String bio;
}
