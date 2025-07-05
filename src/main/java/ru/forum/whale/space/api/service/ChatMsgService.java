package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.ChatMsgDto;
import ru.forum.whale.space.api.dto.request.ChatMsgRequestDto;
import ru.forum.whale.space.api.exception.IllegalOperationException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.Chat;
import ru.forum.whale.space.api.model.ChatMsg;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.ChatRepository;
import ru.forum.whale.space.api.repository.ChatMsgRepository;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatMsgService {
    private final ChatMsgRepository chatMsgRepository;
    private final ChatRepository chatRepository;
    private final ModelMapper modelMapper;
    private final SessionUtilService sessionUtilService;

    @Transactional
    public ChatMsgDto save(ChatMsgRequestDto chatMsgRequestDto) {
        User currentUser = sessionUtilService.findCurrentUser();
        long currentUserId = currentUser.getId();

        Chat chat = chatRepository.findById(chatMsgRequestDto.getChatId())
                .orElseThrow(() -> new ResourceNotFoundException("Чат не найден"));

        if (currentUserId != chat.getUser1().getId() && currentUserId != chat.getUser2().getId()) {
            throw new IllegalOperationException("Доступ к чужому чату запрещён");
        }

        ChatMsg chatMsg = ChatMsg.builder()
                .content(chatMsgRequestDto.getContent())
                .sender(currentUser)
                .chat(chat)
                .createdAt(LocalDateTime.now())
                .build();

        return convertToChatMsgDto(chatMsgRepository.save(chatMsg));
    }

    private ChatMsgDto convertToChatMsgDto(ChatMsg chatMsg) {
        return modelMapper.map(chatMsg, ChatMsgDto.class);
    }
}
