package ru.forum.whale.space.api.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.forum.whale.space.api.dto.ReplyDto;
import ru.forum.whale.space.api.dto.request.ReplyRequestDto;
import ru.forum.whale.space.api.dto.response.ReplyResponseDto;
import ru.forum.whale.space.api.service.ReplyService;
import ru.forum.whale.space.api.util.ErrorUtil;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class WebSocketReplyController {
    private final ReplyService replyService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Validator validator;

    @MessageMapping("/sendReply")
    public void send(ReplyRequestDto replyRequestDto, Principal principal) {
        Set<ConstraintViolation<ReplyRequestDto>> violations = validator.validate(replyRequestDto);

        if (!violations.isEmpty()) {
            ReplyResponseDto errorResponse = new ReplyResponseDto(false,
                    ErrorUtil.buildMessage(violations));
            messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", errorResponse);

            return;
        }

        Optional<ReplyDto> savedReplyRequestDto = replyService.save(replyRequestDto);

        ReplyResponseDto response;

        if (savedReplyRequestDto.isPresent()) {
            response = new ReplyResponseDto(true, "Сообщение отправлено успешно!",
                    savedReplyRequestDto.get());
            messagingTemplate.convertAndSend("/discussion/newReply/" + replyRequestDto.getDiscussionId(),
                    response);
            return;
        }

        response = new ReplyResponseDto(false, "Обсуждение или отправитель не найдены");
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", response);
    }
}
