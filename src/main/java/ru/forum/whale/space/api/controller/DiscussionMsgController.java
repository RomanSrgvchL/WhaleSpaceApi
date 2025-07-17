package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.docs.discussionmsg.CreateDiscussionMsgDocs;
import ru.forum.whale.space.api.dto.DiscussionMsgDto;
import ru.forum.whale.space.api.service.DiscussionMsgService;
import ru.forum.whale.space.api.util.Messages;

import java.util.List;

@Validated
@RestController
@RequestMapping("/discussions/{discussionId}/messages")
@RequiredArgsConstructor
@Tag(name = "Сообщения в обсуждениях", description = "Операции с сообщениями в обсуждениях (отправка)")
public class DiscussionMsgController {
    private final DiscussionMsgService discussionMsgService;
    private final SimpMessagingTemplate messagingTemplate;

    @CreateDiscussionMsgDocs
    @PostMapping
    public ResponseEntity<DiscussionMsgDto> create(
            @PathVariable @Positive(message = Messages.ID_POSITIVE) long discussionId,
            @RequestParam(value = "content") @NotBlank(message = Messages.MSG_NOT_BLANK)
            @Size(max = 200, message = Messages.MSG_TOO_LONG) String content,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        DiscussionMsgDto discussionMsgDto = discussionMsgService.save(discussionId, content, files);

        messagingTemplate.convertAndSend("/discussion/newMessage/" + discussionId, discussionMsgDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(discussionMsgDto);
    }
}
