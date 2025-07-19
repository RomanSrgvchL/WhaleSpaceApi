package ru.forum.whale.space.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.forum.whale.space.api.util.Messages;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "discussions")
public class Discussion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = Messages.DISCUSSION_NOT_BLANK)
    @Size(min = 5, max = 100, message = Messages.DISCUSSION_TITLE_RANGE)
    @Column(name = "title")
    private String title;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "discussion", cascade = CascadeType.REMOVE)
    private List<DiscussionMsg> messages;
}
