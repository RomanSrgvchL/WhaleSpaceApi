package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.ChatDto;
import ru.forum.whale.space.api.dto.ChatWithLastMessageDto;
import ru.forum.whale.space.api.dto.MessageDto;
import ru.forum.whale.space.api.exception.IllegalOperationException;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.Chat;
import ru.forum.whale.space.api.model.Message;
import ru.forum.whale.space.api.model.Person;
import ru.forum.whale.space.api.repository.ChatRepository;
import ru.forum.whale.space.api.repository.PersonRepository;
import ru.forum.whale.space.api.util.SessionUtil;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final PersonRepository personRepository;
    private final ModelMapper modelMapper;

    public List<ChatWithLastMessageDto> findAll() {
        return chatRepository.findAllByUserIdWithMessages(SessionUtil.getCurrentUserId())
                .stream()
                .filter(chat -> !chat.getMessages().isEmpty())
                .map(chat -> {
                    ChatWithLastMessageDto dto = modelMapper.map(chat, ChatWithLastMessageDto.class);

                    chat.getMessages().stream()
                            .max(Comparator.comparing(Message::getCreatedAt))
                            .ifPresent(msg -> dto.setLastMessage(modelMapper.map(msg, MessageDto.class)));

                    return dto;
                })
                .toList();
    }

    public ChatDto findById(int id) {
        Chat chat = chatRepository.findByIdWithMessages(id)
                .orElseThrow(() -> new ResourceNotFoundException("Чат с указанным ID не найден"));

        int currentUserId = SessionUtil.getCurrentUserId();

        if (currentUserId != chat.getUser1().getId() && currentUserId != chat.getUser2().getId()) {
            throw new IllegalOperationException("Доступ к чужому чату запрещён");
        }

        chat.getMessages().sort(Comparator.comparing(Message::getCreatedAt));
        return convertToChatDto(chat);
    }

    public ChatDto findWithUser(int partnerId) {
        Pair<Person, Person> users = resolveParticipants(partnerId, "Нельзя получить чат с самим собой");

        Person first = users.getFirst();
        Person second = users.getSecond();

        Chat chat = chatRepository.findByUser1AndUser2(first, second)
                .orElseThrow(() -> new ResourceNotFoundException("Чат с указанным пользователем не найден"));

        return convertToChatDto(chat);
    }

    @Transactional
    public ChatDto save(int partnerId) {
        Pair<Person, Person> users = resolveParticipants(partnerId, "Нельзя создать чат с самим собой");

        Person first = users.getFirst();
        Person second = users.getSecond();

        if (chatRepository.findByUser1AndUser2(first, second).isPresent()) {
            throw new ResourceAlreadyExistsException("Чат с указанным пользователем уже существует");
        }

        Chat chat = Chat.builder()
                .user1(first)
                .user2(second)
                .createdAt(LocalDateTime.now())
                .build();

        return convertToChatDto(chatRepository.save(chat));
    }

    private Pair<Person, Person> resolveParticipants(int partnerId, String sameUserMsg) {
        Person currentUser = SessionUtil.getCurrentUser();
        int currentUserId = currentUser.getId();

        if (currentUserId == partnerId) {
            throw new IllegalOperationException(sameUserMsg);
        }

        Person partner = personRepository.findById(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с указанным ID не найден"));

        return currentUserId < partnerId ? Pair.of(currentUser, partner) : Pair.of(partner, currentUser);
    }

    private ChatDto convertToChatDto(Chat chat) {
        return modelMapper.map(chat, ChatDto.class);
    }
}
