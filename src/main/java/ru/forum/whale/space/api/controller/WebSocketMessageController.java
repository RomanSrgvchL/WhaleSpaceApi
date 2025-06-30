package ru.forum.whale.space.api.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.forum.whale.space.api.dto.MessageDto;
import ru.forum.whale.space.api.dto.request.MessageRequestDto;
import ru.forum.whale.space.api.dto.response.MessageResponseDto;
import ru.forum.whale.space.api.service.MessageService;
import ru.forum.whale.space.api.util.ErrorUtil;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class WebSocketMessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Validator validator;

    @MessageMapping("/sendMessage")
    public void send(MessageRequestDto messageRequestDto, Principal principal) {
        Set<ConstraintViolation<MessageRequestDto>> violations = validator.validate(messageRequestDto);

        if (!violations.isEmpty()) {
            MessageResponseDto errorResponse = MessageResponseDto.buildFailure(ErrorUtil.buildMessage(violations));
            messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", errorResponse);
            return;
        }

        Optional<MessageDto> savedMessageRequestDto = messageService.save(messageRequestDto);

        MessageResponseDto response;

        if (savedMessageRequestDto.isPresent()) {
            response = MessageResponseDto.buildSuccess("Сообщение отправлено успешно!",
                    savedMessageRequestDto.get());
            messagingTemplate.convertAndSend("/chat/newMessage/" + messageRequestDto.getChatId(), response);
            return;
        }

        response = MessageResponseDto.buildFailure("Чат или отправитель не найдены");
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", response);
    }
}
