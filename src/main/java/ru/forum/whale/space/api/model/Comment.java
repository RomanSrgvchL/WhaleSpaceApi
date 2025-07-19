package ru.forum.whale.space.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.forum.whale.space.api.util.Messages;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @NotBlank(message = Messages.COMMENT_NOT_BLANK)
    @Size(max = 1000, message = Messages.COMMENT_TOO_LONG)
    @Column(name = "content")
    private String content;

    @OneToMany(mappedBy = "comment")
    private List<CommentLike> likes;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
