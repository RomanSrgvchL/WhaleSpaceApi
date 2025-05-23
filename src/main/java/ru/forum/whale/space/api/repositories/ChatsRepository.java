package ru.forum.whale.space.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.forum.whale.space.api.models.Chat;
import ru.forum.whale.space.api.models.Person;

import java.util.Optional;

public interface ChatsRepository extends JpaRepository<Chat, Integer> {
    Optional<Chat> findByUser1AndUser2(Person user1, Person user2);
}
