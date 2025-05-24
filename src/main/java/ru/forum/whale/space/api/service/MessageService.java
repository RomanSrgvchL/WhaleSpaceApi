package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.MessageDto;
import ru.forum.whale.space.api.dto.request.MessageRequestDto;
import ru.forum.whale.space.api.model.Chat;
import ru.forum.whale.space.api.model.Message;
import ru.forum.whale.space.api.model.Person;
import ru.forum.whale.space.api.repository.ChatRepository;
import ru.forum.whale.space.api.repository.MessageRepository;
import ru.forum.whale.space.api.repository.PersonRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final PersonRepository personRepository;
    private final ChatRepository chatRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Optional<MessageDto> saveAndReturn(MessageRequestDto messageRequestDto) {
        Message message = convertToMessage(messageRequestDto);
        Optional<Person> person = personRepository.findById(messageRequestDto.getSenderId());
        Optional<Chat> chat = chatRepository.findById(messageRequestDto.getChatId());
        if (person.isPresent() && chat.isPresent()) {
            message.setSender(person.get());
            message.setChat(chat.get());
            message.setCreatedAt(LocalDateTime.now());
            messageRepository.save(message);
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
