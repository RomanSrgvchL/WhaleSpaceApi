package ru.forum.whale.space.api.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.forum.whale.space.api.model.Comment;
import ru.forum.whale.space.api.model.CommentLike;
import ru.forum.whale.space.api.model.User;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByAuthorAndComment(User author, Comment comment);
}
