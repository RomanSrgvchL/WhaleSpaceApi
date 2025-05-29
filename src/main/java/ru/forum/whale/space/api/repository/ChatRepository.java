package ru.forum.whale.space.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.forum.whale.space.api.model.Chat;
import ru.forum.whale.space.api.model.Person;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
    Optional<Chat> findByUser1AndUser2(Person user1, Person user2);

    @Query("From Chat c LEFT JOIN FETCH c.messages WHERE c.id = :chatId")
    Optional<Chat> findByIdWithMessages(@Param("chatId") Integer chatId);

    @Query("FROM Chat c LEFT JOIN FETCH c.messages WHERE c.user1.id = :personId OR c.user2.id = :personId")
    List<Chat> findAllWithMessages(@Param("personId") Integer personId);

    @Query("FROM Chat c LEFT JOIN FETCH c.messages WHERE c.user1.id = :personId OR c.user2.id = :personId " +
            "ORDER BY c.createdAt DESC")
    List<Chat> findAllByCreatedAtDescWithMessages(@Param("personId") Integer personId);
}
