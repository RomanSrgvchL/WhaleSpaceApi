package ru.forum.whale.space.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.forum.whale.space.api.dto.DiscussionMsgDto;
import ru.forum.whale.space.api.dto.request.DiscussionMsgRequestDto;
import ru.forum.whale.space.api.service.DiscussionMsgService;
import ru.forum.whale.space.api.util.ErrorUtil;

@RestController
@RequestMapping("/discussionMessages")
@RequiredArgsConstructor
public class DiscussionMsgController {
    private final DiscussionMsgService discussionMsgService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<DiscussionMsgDto> create(@RequestBody @Valid DiscussionMsgRequestDto discussionMsgRequestDto,
                                             BindingResult bindingResult) {
        ErrorUtil.ifHasErrorsBuildMessageAndThrowValidationException(bindingResult);

        DiscussionMsgDto discussionMsgDto = discussionMsgService.save(discussionMsgRequestDto);

        messagingTemplate.convertAndSend("/discussion/newMessage/" +
                discussionMsgRequestDto.getDiscussionId(), discussionMsgDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(discussionMsgDto);
    }
}
