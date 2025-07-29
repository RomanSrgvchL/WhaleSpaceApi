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
import ru.forum.whale.space.api.docs.chatmsg.CreateChatMsgDocs;
import ru.forum.whale.space.api.dto.ChatMsgDto;
import ru.forum.whale.space.api.dto.request.MessageRequestDto;
import ru.forum.whale.space.api.service.ChatMsgService;
import ru.forum.whale.space.api.util.Messages;

import java.util.List;

@Validated
@RestController
@RequestMapping("/chats/{chatId}/messages")
@RequiredArgsConstructor
@Tag(name = "Сообщения в чатах", description = "Операции с сообщениями в чатах (отправка)")
public class ChatMsgController {
    private final ChatMsgService chatMsgService;
    private final SimpMessagingTemplate messagingTemplate;

    @CreateChatMsgDocs
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ChatMsgDto> create(
            @PathVariable @Positive(message = Messages.ID_POSITIVE) long chatId,
            @RequestPart(value = "message") @Valid MessageRequestDto messageRequestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        ChatMsgDto chatMsgDto = chatMsgService.save(chatId, messageRequestDto, files);

        messagingTemplate.convertAndSend("/chat/newMessage/" + chatId, chatMsgDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(chatMsgDto);
    }
}
