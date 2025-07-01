package ru.forum.whale.space.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.forum.whale.space.api.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
