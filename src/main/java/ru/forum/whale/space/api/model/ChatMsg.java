package ru.forum.whale.space.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMsg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    private Chat chat;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private User sender;

    @NotBlank(message = "Сообщение не должно быть пустым")
    @Size(max = 200, message = "Длина сообщения не должна превышать 200 символов")
    @Column(name = "content")
    private String content;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
