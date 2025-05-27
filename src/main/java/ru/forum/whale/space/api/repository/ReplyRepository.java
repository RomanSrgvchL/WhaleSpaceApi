package ru.forum.whale.space.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.forum.whale.space.api.model.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {
}
