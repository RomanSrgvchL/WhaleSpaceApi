package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.ChatMsgDto;
import ru.forum.whale.space.api.dto.request.ChatMsgRequestDto;
import ru.forum.whale.space.api.model.Chat;
import ru.forum.whale.space.api.model.ChatMsg;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.ChatRepository;
import ru.forum.whale.space.api.repository.ChatMsgRepository;
import ru.forum.whale.space.api.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatMsgService {
    private final ChatMsgRepository chatMsgRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Optional<ChatMsgDto> save(ChatMsgRequestDto chatMsgRequestDto) {
        Optional<User> user = userRepository.findById(chatMsgRequestDto.getSenderId());
        Optional<Chat> chat = chatRepository.findById(chatMsgRequestDto.getChatId());

        if (user.isPresent() && chat.isPresent()) {
            ChatMsg chatMsg = ChatMsg.builder()
                    .content(chatMsgRequestDto.getContent())
                    .sender(user.get())
                    .chat(chat.get())
                    .createdAt(LocalDateTime.now())
                    .build();

            chatMsgRepository.save(chatMsg);

            return Optional.of(convertToMessageDto(chatMsg));
        }

        return Optional.empty();
    }

    private ChatMsgDto convertToMessageDto(ChatMsg chatMsg) {
        return modelMapper.map(chatMsg, ChatMsgDto.class);
    }
}
