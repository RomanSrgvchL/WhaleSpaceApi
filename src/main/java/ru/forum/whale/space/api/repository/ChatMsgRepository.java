package ru.forum.whale.space.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.forum.whale.space.api.model.ChatMsg;

public interface ChatMsgRepository extends JpaRepository<ChatMsg, Long> {
}
