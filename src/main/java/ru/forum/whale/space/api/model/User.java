package ru.forum.whale.space.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.forum.whale.space.api.util.Messages;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Pattern(regexp = "^(?!.*[;\\\\/?&#]).*$", message = Messages.USERNAME_CANNOT_CONTAIN)
    @NotBlank(message = Messages.USERNAME_NOT_BLANK)
    @Size(max = 20, message = Messages.USERNAME_TOO_LONG)
    @Column(name = "username")
    private String username;

    @NotBlank(message = Messages.PASSWORD_NOT_BLANK)
    @Size(max = 100, message = Messages.PASSWORD_TOO_LONG)
    @Column(name = "password")
    private String password;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "role")
    private String role;

    @Column(name = "avatar_file_name")
    private String avatarFileName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Size(max = 120, message = Messages.BIO_TOO_LONG)
    @Column(name = "bio")
    private String bio;
}
