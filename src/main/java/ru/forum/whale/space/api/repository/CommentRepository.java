package ru.forum.whale.space.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.forum.whale.space.api.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
