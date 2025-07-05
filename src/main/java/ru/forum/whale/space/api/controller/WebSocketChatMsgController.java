package ru.forum.whale.space.api.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.forum.whale.space.api.dto.ChatMsgDto;
import ru.forum.whale.space.api.dto.request.ChatMsgRequestDto;
import ru.forum.whale.space.api.dto.response.ChatMsgResponseDto;
import ru.forum.whale.space.api.service.ChatMsgService;
import ru.forum.whale.space.api.util.ErrorUtil;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class WebSocketChatMsgController {
    private final ChatMsgService chatMsgService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Validator validator;

    @MessageMapping("/sendChatMsg")
    public void send(ChatMsgRequestDto chatMsgRequestDto, Principal principal) {
        Set<ConstraintViolation<ChatMsgRequestDto>> violations = validator.validate(chatMsgRequestDto);

        if (!violations.isEmpty()) {
            ChatMsgResponseDto errorResponse = ChatMsgResponseDto.buildFailure(ErrorUtil.buildMessage(violations));
            messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", errorResponse);
            return;
        }

        Optional<ChatMsgDto> savedChatMsgRequestDto = chatMsgService.save(chatMsgRequestDto);

        ChatMsgResponseDto response;

        if (savedChatMsgRequestDto.isPresent()) {
            response = ChatMsgResponseDto.buildSuccess("Сообщение отправлено успешно!",
                    savedChatMsgRequestDto.get());
            messagingTemplate.convertAndSend("/chat/newChatMsg/" + chatMsgRequestDto.getChatId(), response);
            return;
        }

        response = ChatMsgResponseDto.buildFailure("Чат или отправитель не найдены");
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", response);
    }
}
