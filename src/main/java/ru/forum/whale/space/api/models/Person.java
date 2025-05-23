package ru.forum.whale.space.api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "person")
@Getter
@Setter
@NoArgsConstructor
public class Person implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotBlank(message = "Имя пользователя не должно быть пустым")
    @Size(max = 20, message = "Имя пользователя не должно содержать более 20 символов")
    @Column(name = "username")
    private String username;

    @NotBlank(message = "Пароль не должен быть пустым")
    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "role")
    private String role;
}
