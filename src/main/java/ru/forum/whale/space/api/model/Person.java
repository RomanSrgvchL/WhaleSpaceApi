package ru.forum.whale.space.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "person")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Pattern(
            regexp = "^(?!.*[;\\\\/?&#]).*$",
            message = "Имя пользователя не должно содержать символы ; \\ / ? & #"
    )
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    @Size(max = 20, message = "Имя пользователя не должно содержать более 20 символов")
    @Column(name = "username")
    private String username;

    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(max = 100, message = "Пароль не должен содержать более 100 символов")
    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "role")
    private String role;

    @Column(name = "avatar_file_name")
    private String avatarFileName;
}
