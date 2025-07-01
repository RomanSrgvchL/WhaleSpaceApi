package ru.forum.whale.space.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chats")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user1_id")
    private User user1;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user2_id")
    private User user2;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "chat")
    private List<Message> messages;
}
