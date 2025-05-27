package ru.forum.whale.space.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.forum.whale.space.api.model.Discussion;

import java.util.List;
import java.util.Optional;

public interface DiscussionRepository extends JpaRepository<Discussion, Integer> {
    Optional<Discussion> findByTitle(String title);
    List<Discussion> findAllByOrderByCreatedAtDesc();
}
