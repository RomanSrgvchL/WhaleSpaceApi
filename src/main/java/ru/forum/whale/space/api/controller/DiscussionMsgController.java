package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.dto.DiscussionMsgDto;
import ru.forum.whale.space.api.dto.request.DiscussionMsgRequestDto;
import ru.forum.whale.space.api.service.DiscussionMsgService;

import java.util.List;

@RestController
@RequestMapping("/discussionMessages")
@RequiredArgsConstructor
@Tag(name = "Сообщения в обсуждениях", description = "Операции с сообщениями в обсуждениях (отправка)")
public class DiscussionMsgController {
    private final DiscussionMsgService discussionMsgService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<DiscussionMsgDto> create(
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @ModelAttribute @Valid DiscussionMsgRequestDto discussionMsgRequestDto) {
        DiscussionMsgDto discussionMsgDto = discussionMsgService.save(discussionMsgRequestDto, files);

        messagingTemplate.convertAndSend("/discussion/newMessage/" +
                discussionMsgRequestDto.getDiscussionId(), discussionMsgDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(discussionMsgDto);
    }
}
