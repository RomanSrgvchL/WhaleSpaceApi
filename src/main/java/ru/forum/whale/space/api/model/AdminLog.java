package ru.forum.whale.space.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.forum.whale.space.api.util.Messages;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "admin_logs")
public class AdminLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank(message = Messages.COMMENT_NOT_BLANK)
    @Size(max = 200, message = Messages.COMMENT_TOO_LONG)
    @Column(name = "log_content")
    private String logContent;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "log_type")
    private LogType logType;

    @CreationTimestamp
    @Column(name = "created_at")
    private ZonedDateTime createdAt;
}
