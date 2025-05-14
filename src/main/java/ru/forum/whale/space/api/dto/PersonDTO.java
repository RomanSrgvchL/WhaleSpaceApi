package ru.forum.whale.space.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PersonDTO {
    @NotBlank
    @Size(max = 20)
    private String username;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private String role;
}
