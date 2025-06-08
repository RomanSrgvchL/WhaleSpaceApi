package ru.forum.whale.space.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.forum.whale.space.api.model.Discussion;

import java.util.List;
import java.util.Optional;

public interface DiscussionRepository extends JpaRepository<Discussion, Integer> {
    List<Discussion> findAllByOrderByCreatedAtDesc();

    Optional<Discussion> findByTitle(String title);

    @Query("From Discussion d LEFT JOIN FETCH d.replies WHERE d.id = :discussionId")
    Optional<Discussion> findByIdWithReplies(@Param("discussionId") Integer discussionId);
}
