package ru.forum.whale.space.api.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.forum.whale.space.api.dto.MessageDto;
import ru.forum.whale.space.api.dto.request.MessageRequestDto;
import ru.forum.whale.space.api.dto.response.MessageResponseDto;
import ru.forum.whale.space.api.services.MessagesService;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class WebSocketMessageController {
    private final MessagesService messagesService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Validator validator;

    @MessageMapping("/sendMessage")
    public void sendMessage(MessageRequestDto messageRequestDto, Principal principal) {
        int chatId = messageRequestDto.getChatId();

        Set<ConstraintViolation<MessageRequestDto>> violations = validator.validate(messageRequestDto);

        if (!violations.isEmpty()) {
            String errorMsg;

            Optional<ConstraintViolation<MessageRequestDto>> contentViolation = violations.stream()
                    .filter(v -> "content".equals(v.getPropertyPath().toString()))
                    .findFirst();

            if (contentViolation.isPresent()) {
                errorMsg = contentViolation.get().getMessage();
            } else {
                errorMsg = "Не указан чат или отправитель";
            }
            MessageResponseDto errorResponse = new MessageResponseDto(false, errorMsg);

            messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", errorResponse);
            return;
        }

        Optional<MessageDto> savedMessageRequestDto = messagesService.saveAndReturn(messageRequestDto);

        MessageResponseDto response;

        if (savedMessageRequestDto.isPresent()) {
            response = new MessageResponseDto(true, "Сообщение отправлено успешно",
                    savedMessageRequestDto.get());
            messagingTemplate.convertAndSend("/chat/newMessage/" + chatId, response);
            return;
        }

        response = new MessageResponseDto(false, "Несуществующий чат или отправитель");
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", response);
    }
}
