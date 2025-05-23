package ru.forum.whale.space.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.forum.whale.space.api.models.Message;

public interface MessagesRepository extends JpaRepository<Message, Integer> {
}
