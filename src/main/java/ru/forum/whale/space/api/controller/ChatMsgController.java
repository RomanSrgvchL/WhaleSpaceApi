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
import ru.forum.whale.space.api.dto.ChatMsgDto;
import ru.forum.whale.space.api.dto.request.ChatMsgRequestDto;
import ru.forum.whale.space.api.service.ChatMsgService;
import ru.forum.whale.space.api.util.ErrorUtil;

@RestController
@RequestMapping("/chatMessages")
@RequiredArgsConstructor
public class ChatMsgController {
    private final ChatMsgService chatMsgService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<ChatMsgDto> create(@RequestBody @Valid ChatMsgRequestDto chatMsgRequestDto,
                                                   BindingResult bindingResult) {
        ErrorUtil.ifHasErrorsBuildMessageAndThrowValidationException(bindingResult);

        ChatMsgDto chatMsgDto = chatMsgService.save(chatMsgRequestDto);

        messagingTemplate.convertAndSend("/chat/newMessage/" + chatMsgRequestDto.getChatId(), chatMsgDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(chatMsgDto);
    }
}
