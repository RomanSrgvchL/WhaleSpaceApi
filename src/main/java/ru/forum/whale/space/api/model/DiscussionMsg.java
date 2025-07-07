package ru.forum.whale.space.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "discussion_messages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionMsg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "discussion_id")
    private Discussion discussion;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @NotBlank(message = "Сообщение не должно быть пустым")
    @Size(max = 200, message = "Длина сообщения не должна превышать 200 символов")
    @Column(name = "content")
    private String content;

    @Size(max = 3, message = "В сообщении не может быть больше 3 изображений")
    @ElementCollection
    @CollectionTable(name = "discussion_message_image_file_names",
            joinColumns = @JoinColumn(name = "discussion_message_id"))
    @Column(name = "image_file_name")
    private List<String> imageFileNames;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
