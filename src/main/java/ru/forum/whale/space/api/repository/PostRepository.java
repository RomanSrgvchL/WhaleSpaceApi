package ru.forum.whale.space.api.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.forum.whale.space.api.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    @EntityGraph(attributePaths = {"author", "comments", "likes", "likes.author"})
    List<Post> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"author", "comments", "comments.author", "likes", "likes.author"})
    Optional<Post> findDetailedById(Long id);
}