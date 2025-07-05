package ru.forum.whale.space.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.forum.whale.space.api.model.DiscussionMsg;

public interface DiscussionMsgRepository extends JpaRepository<DiscussionMsg, Long> {
}
