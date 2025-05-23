package ru.forum.whale.space.api.services;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.ChatDto;
import ru.forum.whale.space.api.models.Chat;
import ru.forum.whale.space.api.models.Message;
import ru.forum.whale.space.api.models.Person;
import ru.forum.whale.space.api.repositories.ChatsRepository;
import ru.forum.whale.space.api.repositories.PeopleRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatsService {
    private final ChatsRepository chatsRepository;
    private final PeopleRepository peopleRepository;
    private final ModelMapper modelMapper;

    public Optional<ChatDto> findById(int id) {
        Chat chat = chatsRepository.findById(id).orElse(null);
        if (chat != null) {
            Hibernate.initialize(chat.getMessages());
            chat.getMessages().sort(Comparator.comparing(Message::getCreatedAt));
            return Optional.ofNullable(convertToChatDto(chat));
        }
        return Optional.empty();
    }

    public Optional<Integer> findChatIdByUsers(int userId1, int userId2) {
        int minUserId = Math.min(userId1, userId2);
        int maxUserId = Math.max(userId1, userId2);
        Person person1 = peopleRepository.findById(minUserId).orElse(null);
        Person person2 = peopleRepository.findById(maxUserId).orElse(null);
        if (person1 != null && person2 != null) {
            Chat chat = chatsRepository.findByUser1AndUser2(person1, person2).orElse(null);
            if (chat == null) {
                return Optional.empty();
            } else {
                return Optional.of(chat.getId());
            }
        }
        return Optional.empty();
    }

    public ChatDto convertToChatDto(Chat chat) {
        return modelMapper.map(chat, ChatDto.class);
    }

    @Transactional
    public Integer save(int userId1, int userId2) {
        int minUserId = Math.min(userId1, userId2);
        int maxUserId = Math.max(userId1, userId2);
        Chat chat = new Chat();
        chat.setUser1(peopleRepository.findById(minUserId).orElse(null));
        chat.setUser2(peopleRepository.findById(maxUserId).orElse(null));
        chat.setCreatedAt(LocalDateTime.now());
        if (chat.getUser1() != null && chat.getUser2() != null) {
            chatsRepository.save(chat);
            return chat.getId() ;
        }
        return null;
    }
}
