package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.ChatDto;
import ru.forum.whale.space.api.exception.IllegalOperationException;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.Chat;
import ru.forum.whale.space.api.model.Message;
import ru.forum.whale.space.api.model.Person;
import ru.forum.whale.space.api.repository.ChatRepository;
import ru.forum.whale.space.api.repository.PersonRepository;
import ru.forum.whale.space.api.security.PersonDetails;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final PersonRepository personRepository;
    private final ModelMapper modelMapper;

    public List<ChatDto> findAllByUsernameWithMessages(String username) {
        return findAllByUsername(username, chatRepository::findAllByUsernameWithMessages);
    }

    public List<ChatDto> findAllByUsernameOrderByCreatedAtDescWithMessages(String username) {
        return findAllByUsername(username, chatRepository::findAllByUsernameOrderByCreatedAtDescWithMessages);
    }

    private List<ChatDto> findAllByUsername(String username, Function<Integer, List<Chat>> fetcher) {
        Person person = personRepository.findByUsername(username).orElse(null);

        if (person != null) {
            return fetcher.apply(person.getId()).stream()
                    .map(this::convertToChatDto)
                    .collect(Collectors.toList());
        }

        throw new ResourceNotFoundException("Пользователь не найден");
    }

    public ChatDto findById(int id) {
        Chat chat = chatRepository.findByIdWithMessages(id).orElse(null);

        if (chat != null) {
            selfChatCheck(chat.getUser1().getId(), chat.getUser2().getId());

            chat.getMessages().sort(Comparator.comparing(Message::getCreatedAt));
            return convertToChatDto(chat);
        }

        throw new ResourceNotFoundException("Чат с указанным ID не найден");
    }

    public ChatDto findBetweenUsers(int user1Id, int user2Id) {
        selfChatCheck(user1Id, user2Id);

        int minUserId = Math.min(user1Id, user2Id);
        int maxUserId = Math.max(user1Id, user2Id);

        Person person1 = personRepository.findById(minUserId).orElse(null);
        Person person2 = personRepository.findById(maxUserId).orElse(null);

        if (person1 != null && person2 != null) {
            Optional<Chat> chat = chatRepository.findByUser1AndUser2(person1, person2);

            if (chat.isEmpty()) {
                throw new ResourceNotFoundException("Чат между указанными пользователями не найден");
            }

            return convertToChatDto(chat.get());
        }

        throw new ResourceNotFoundException("Один или оба указанных пользователя не найдены");
    }

    @Transactional
    public ChatDto save(int user1Id, int user2Id) {
        selfChatCheck(user1Id, user2Id);

        int minUserId = Math.min(user1Id, user2Id);
        int maxUserId = Math.max(user1Id, user2Id);

        Person user1 = personRepository.findById(minUserId).orElse(null);
        Person user2 = personRepository.findById(maxUserId).orElse(null);

        if (user1 != null && user2 != null) {
            if (chatRepository.findByUser1AndUser2(user1, user2).isPresent()) {
                throw new ResourceAlreadyExistsException("Чат между указанными пользователями уже существует");
            }

            if (Objects.equals(user1.getId(), user2.getId())) {
                throw new IllegalOperationException("Нельзя создать чат с самим собой");
            }

            Chat chat = Chat.builder()
                    .user1(user1)
                    .user2(user2)
                    .createdAt(LocalDateTime.now())
                    .build();

            return convertToChatDto(chatRepository.save(chat));
        }

        throw new ResourceNotFoundException("Один или оба указанных пользователя не найдены");
    }

    private void selfChatCheck(int user1Id, int user2Id) {
        int userId = ((PersonDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getPerson().getId();

        if (userId != user1Id && userId != user2Id) {
            throw new IllegalOperationException("Доступ к чужому чату запрещён");
        }
    }

    private ChatDto convertToChatDto(Chat chat) {
        return modelMapper.map(chat, ChatDto.class);
    }
}
