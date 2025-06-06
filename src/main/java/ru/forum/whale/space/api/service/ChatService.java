package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.ChatDto;
import ru.forum.whale.space.api.exception.InvalidInputDataException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.Chat;
import ru.forum.whale.space.api.model.Message;
import ru.forum.whale.space.api.model.Person;
import ru.forum.whale.space.api.repository.ChatRepository;
import ru.forum.whale.space.api.repository.PersonRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final PersonRepository personRepository;
    private final ModelMapper modelMapper;

    public List<ChatDto> findAllWithMessagesForUser(String username) {
        Person person = personRepository.findByUsername(username).orElse(null);
        if (person == null) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        return chatRepository.findAllWithMessages(person.getId()).stream()
                .map(this::convertToChatDto)
                .collect(Collectors.toList());
    }

    public List<ChatDto> findAllByCreatedAtDescWithMessagesForUser(String username) {
        Person person = personRepository.findByUsername(username).orElse(null);
        if (person == null) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        return chatRepository.findAllByCreatedAtDescWithMessages(person.getId()).stream()
                .map(this::convertToChatDto)
                .collect(Collectors.toList());
    }

    public Optional<ChatDto> findById(int id) {
        Chat chat = chatRepository.findByIdWithMessages(id).orElse(null);
        if (chat != null) {
            chat.getMessages().sort(Comparator.comparing(Message::getCreatedAt));
            return Optional.ofNullable(convertToChatDto(chat));
        }
        return Optional.empty();
    }

    public Optional<Integer> findChatIdByUsers(int userId1, int userId2) {
        int minUserId = Math.min(userId1, userId2);
        int maxUserId = Math.max(userId1, userId2);
        Person person1 = personRepository.findById(minUserId).orElse(null);
        Person person2 = personRepository.findById(maxUserId).orElse(null);
        if (person1 != null && person2 != null) {
            Chat chat = chatRepository.findByUser1AndUser2(person1, person2).orElse(null);
            if (chat == null) {
                return Optional.empty();
            } else {
                return Optional.of(chat.getId());
            }
        }
        throw new InvalidInputDataException("Невозможно найти чат: один или оба указанных пользователя не найдены");
    }

    @Transactional
    public Integer save(int userId1, int userId2) {
        int minUserId = Math.min(userId1, userId2);
        int maxUserId = Math.max(userId1, userId2);
        Chat chat = new Chat();
        chat.setUser1(personRepository.findById(minUserId).orElse(null));
        chat.setUser2(personRepository.findById(maxUserId).orElse(null));
        chat.setCreatedAt(LocalDateTime.now());
        if (chat.getUser1() != null && chat.getUser2() != null) {
            chatRepository.save(chat);
            return chat.getId();
        }
        throw new InvalidInputDataException("Невозможно создать чат: один или оба указанных пользователя не найдены");
    }

    public ChatDto convertToChatDto(Chat chat) {
        return modelMapper.map(chat, ChatDto.class);
    }
}
