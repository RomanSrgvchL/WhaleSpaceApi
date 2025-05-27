package ru.forum.whale.space.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reply")
@Getter
@Setter
@NoArgsConstructor
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "discussion_id", referencedColumnName = "id")
    private Discussion discussion;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private Person sender;

    @NotBlank(message = "Сообщение не должно быть пустым")
    @Size(max = 200, message = "Длина сообщения не должна превышать 200 символов")
    @Column(name = "content")
    private String content;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
