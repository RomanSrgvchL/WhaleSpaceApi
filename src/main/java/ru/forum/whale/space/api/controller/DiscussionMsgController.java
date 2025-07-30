package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.docs.discussionmsg.CreateDiscussionMsgDocs;
import ru.forum.whale.space.api.dto.DiscussionMsgDto;
import ru.forum.whale.space.api.dto.request.MessageRequestDto;
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DiscussionMsgDto> create(
            @PathVariable @Positive(message = Messages.ID_POSITIVE) long discussionId,
            @RequestPart("message") @Valid MessageRequestDto messageRequestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        DiscussionMsgDto discussionMsgDto = discussionMsgService.save(discussionId, messageRequestDto, files);

        messagingTemplate.convertAndSend("/discussion/newMessage/" + discussionId, discussionMsgDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(discussionMsgDto);
    }
}
