package ru.forum.whale.space.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "discussions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Discussion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "Тема не должна быть пустой")
    @Size(min = 5, max = 100, message = "Длина темы должна быть в диапазоне от 5 до 100 символов")
    @Column(name = "title")
    private String title;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "discussion")
    private List<Reply> replies;
}
