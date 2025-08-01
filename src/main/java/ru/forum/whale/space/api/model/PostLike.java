package ru.forum.whale.space.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"post"})
@Entity
@Table(name = "post_likes")
public class PostLike {
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

    @CreationTimestamp
    @Column(name = "created_at")
    private ZonedDateTime createdAt;
}
