package ru.forum.whale.space.api.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.MessageDto;
import ru.forum.whale.space.api.dto.request.MessageRequestDto;
import ru.forum.whale.space.api.models.Chat;
import ru.forum.whale.space.api.models.Message;
import ru.forum.whale.space.api.models.Person;
import ru.forum.whale.space.api.repositories.ChatsRepository;
import ru.forum.whale.space.api.repositories.MessagesRepository;
import ru.forum.whale.space.api.repositories.PeopleRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessagesService {
    private final MessagesRepository messagesRepository;
    private final PeopleRepository peopleRepository;
    private final ChatsRepository chatsRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Optional<MessageDto> saveAndReturn(MessageRequestDto messageRequestDto) {
        Message message = convertToMessage(messageRequestDto);
        Optional<Person> person = peopleRepository.findById(messageRequestDto.getSenderId());
        Optional<Chat> chat = chatsRepository.findById(messageRequestDto.getChatId());
        if (person.isPresent() && chat.isPresent()) {
            message.setSender(person.get());
            message.setChat(chat.get());
            message.setCreatedAt(LocalDateTime.now());
            messagesRepository.save(message);
            return Optional.of(convertToMessageDto(message));
        }
        return Optional.empty();
    }

    public Message convertToMessage(MessageRequestDto messageRequestDto) {
        Message message = new Message();
        message.setContent(messageRequestDto.getContent());
        return message;
    }

    public MessageDto convertToMessageDto(Message message) {
        return modelMapper.map(message, MessageDto.class);
    }
}
