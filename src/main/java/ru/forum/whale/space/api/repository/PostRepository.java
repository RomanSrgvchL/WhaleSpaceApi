package ru.forum.whale.space.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.forum.whale.space.api.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    @EntityGraph(attributePaths = {"comments", "likes"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Post> findAllBy(Sort sort);

    @EntityGraph(attributePaths = {"comments", "likes"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Post> findDetailedById(Long id);
}