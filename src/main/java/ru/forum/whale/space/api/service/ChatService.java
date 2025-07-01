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
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.ChatRepository;
import ru.forum.whale.space.api.repository.UserRepository;
import ru.forum.whale.space.api.util.SessionUtil;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final SessionUtilService sessionUtilService;

    public List<ChatWithLastMessageDto> findAll() {
        return chatRepository.findAllByUserIdWithMessages(SessionUtil.getCurrentUserId())
                .stream()
                .filter(chat -> !chat.getMessages().isEmpty())
                .map(chat -> {
                    ChatWithLastMessageDto chatDto = modelMapper.map(chat, ChatWithLastMessageDto.class);

                    chat.getMessages().stream()
                            .max(Comparator.comparing(Message::getCreatedAt))
                            .ifPresent(message -> chatDto.setLastMessage(modelMapper.map(message, MessageDto.class)));

                    return chatDto;
                })
                .sorted(Comparator.comparing(chatDto -> chatDto.getLastMessage().getCreatedAt()))
                .collect(Collectors.toList())
                .reversed();
    }

    public ChatDto findById(long id) {
        Chat chat = chatRepository.findByIdWithMessages(id)
                .orElseThrow(() -> new ResourceNotFoundException("Чат с указанным ID не найден"));

        long currentUserId = SessionUtil.getCurrentUserId();

        if (currentUserId != chat.getUser1().getId() && currentUserId != chat.getUser2().getId()) {
            throw new IllegalOperationException("Доступ к чужому чату запрещён");
        }

        chat.getMessages().sort(Comparator.comparing(Message::getCreatedAt));
        return convertToChatDto(chat);
    }

    public ChatDto findWithUser(long partnerId) {
        Pair<User, User> users = resolveParticipants(partnerId, "Нельзя получить чат с самим собой");

        User first = users.getFirst();
        User second = users.getSecond();

        Chat chat = chatRepository.findByUser1AndUser2(first, second)
                .orElseThrow(() -> new ResourceNotFoundException("Чат с указанным пользователем не найден"));

        return convertToChatDto(chat);
    }

    @Transactional
    public ChatDto save(long partnerId) {
        Pair<User, User> users = resolveParticipants(partnerId, "Нельзя создать чат с самим собой");

        User first = users.getFirst();
        User second = users.getSecond();

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

    private Pair<User, User> resolveParticipants(long partnerId, String sameUserMsg) {
        User currentUser = sessionUtilService.findCurrentUser();
        long currentUserId = currentUser.getId();

        if (currentUserId == partnerId) {
            throw new IllegalOperationException(sameUserMsg);
        }

        User partner = userRepository.findById(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с указанным ID не найден"));

        return currentUserId < partnerId ? Pair.of(currentUser, partner) : Pair.of(partner, currentUser);
    }

    private ChatDto convertToChatDto(Chat chat) {
        return modelMapper.map(chat, ChatDto.class);
    }
}
