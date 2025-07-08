package ru.forum.whale.space.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.dto.DiscussionMsgDto;
import ru.forum.whale.space.api.dto.request.DiscussionMsgRequestDto;
import ru.forum.whale.space.api.service.DiscussionMsgService;
import ru.forum.whale.space.api.util.ErrorUtil;

import java.util.List;

@RestController
@RequestMapping("/discussionMessages")
@RequiredArgsConstructor
public class DiscussionMsgController {
    private final DiscussionMsgService discussionMsgService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<DiscussionMsgDto> create(
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @ModelAttribute @Valid DiscussionMsgRequestDto discussionMsgRequestDto,
            BindingResult bindingResult) {
        ErrorUtil.ifHasErrorsBuildMessageAndThrowValidationException(bindingResult);

        DiscussionMsgDto discussionMsgDto = discussionMsgService.save(discussionMsgRequestDto, files);

        messagingTemplate.convertAndSend("/discussion/newMessage/" +
                discussionMsgRequestDto.getDiscussionId(), discussionMsgDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(discussionMsgDto);
    }
}
