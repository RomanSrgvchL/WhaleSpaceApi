package ru.forum.whale.space.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.forum.whale.space.api.model.Chat;
import ru.forum.whale.space.api.model.Person;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
    Optional<Chat> findByUser1AndUser2(Person user1, Person user2);
}
