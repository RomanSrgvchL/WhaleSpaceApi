package ru.forum.whale.space.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.forum.whale.space.api.model.Discussion;

import java.util.Optional;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
    boolean existsByTitle(String title);

    @Query("From Discussion d LEFT JOIN FETCH d.messages WHERE d.id = :discussionId")
    Optional<Discussion> findByIdWithMessages(@Param("discussionId") long discussionId);
}
