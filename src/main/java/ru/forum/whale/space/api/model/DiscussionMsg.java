package ru.forum.whale.space.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.forum.whale.space.api.util.Messages;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"discussion"})
@Entity
@Table(name = "discussion_messages")
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

    @NotBlank(message = Messages.MSG_NOT_BLANK)
    @Size(max = 200, message = Messages.MSG_TOO_LONG)
    @Column(name = "content")
    private String content;

    @Size(max = 3, message = Messages.MSG_IMAGES_LIMIT)
    @ElementCollection
    @CollectionTable(
            name = "discussion_message_image_file_names",
            joinColumns = @JoinColumn(name = "discussion_message_id")
    )
    @Column(name = "image_file_name")
    private List<String> imageFileNames;

    @CreationTimestamp
    @Column(name = "created_at")
    private ZonedDateTime createdAt;
}
