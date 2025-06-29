package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.MessageDto;
import ru.forum.whale.space.api.dto.request.MessageRequestDto;
import ru.forum.whale.space.api.model.Chat;
import ru.forum.whale.space.api.model.Message;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.ChatRepository;
import ru.forum.whale.space.api.repository.MessageRepository;
import ru.forum.whale.space.api.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Optional<MessageDto> save(MessageRequestDto messageRequestDto) {
        Optional<User> user = userRepository.findById(messageRequestDto.getSenderId());
        Optional<Chat> chat = chatRepository.findById(messageRequestDto.getChatId());

        if (user.isPresent() && chat.isPresent()) {
            Message message = Message.builder()
                    .content(messageRequestDto.getContent())
                    .sender(user.get())
                    .chat(chat.get())
                    .createdAt(LocalDateTime.now())
                    .build();

            messageRepository.save(message);

            return Optional.of(convertToMessageDto(message));
        }

        return Optional.empty();
    }

    private MessageDto convertToMessageDto(Message message) {
        return modelMapper.map(message, MessageDto.class);
    }
}
