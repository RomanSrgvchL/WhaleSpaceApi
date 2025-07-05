package ru.forum.whale.space.api.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.forum.whale.space.api.dto.DiscussionMsgDto;
import ru.forum.whale.space.api.dto.request.DiscussionMsgRequestDto;
import ru.forum.whale.space.api.dto.response.DiscussionMsgResponseDto;
import ru.forum.whale.space.api.service.DiscussionMsgService;
import ru.forum.whale.space.api.util.ErrorUtil;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class WebSocketDiscussionMsgController {
    private final DiscussionMsgService discussionMsgService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Validator validator;

    @MessageMapping("/sendDiscussionMsg")
    public void send(DiscussionMsgRequestDto discussionMsgRequestDto, Principal principal) {
        Set<ConstraintViolation<DiscussionMsgRequestDto>> violations = validator.validate(discussionMsgRequestDto);

        if (!violations.isEmpty()) {
            DiscussionMsgResponseDto errorResponse = DiscussionMsgResponseDto
                    .buildFailure(ErrorUtil.buildMessage(violations));
            messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", errorResponse);
            return;
        }

        Optional<DiscussionMsgDto> savedDiscussionMsgRequestDto = discussionMsgService.save(discussionMsgRequestDto);

        DiscussionMsgResponseDto response;

        if (savedDiscussionMsgRequestDto.isPresent()) {
            response = DiscussionMsgResponseDto.buildSuccess("Сообщение отправлено успешно!",
                    savedDiscussionMsgRequestDto.get());
            messagingTemplate.convertAndSend("/discussion/newDiscussionMsg/" +
                            discussionMsgRequestDto.getDiscussionId(), response);
            return;
        }

        response = DiscussionMsgResponseDto.buildFailure("Обсуждение или отправитель не найдены");
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", response);
    }
}
