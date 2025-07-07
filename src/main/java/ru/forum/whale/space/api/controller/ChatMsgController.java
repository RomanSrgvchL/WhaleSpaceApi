package ru.forum.whale.space.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.dto.ChatMsgDto;
import ru.forum.whale.space.api.dto.request.ChatMsgRequestDto;
import ru.forum.whale.space.api.service.ChatMsgService;
import ru.forum.whale.space.api.util.ErrorUtil;

import java.util.List;

@RestController
@RequestMapping("/chatMessages")
@RequiredArgsConstructor
public class ChatMsgController {
    private final ChatMsgService chatMsgService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<ChatMsgDto> create(@RequestParam(value = "files", required = false) List<MultipartFile> files,
                                             @ModelAttribute @Valid ChatMsgRequestDto chatMsgRequestDto,
                                             BindingResult bindingResult) {
        ErrorUtil.ifHasErrorsBuildMessageAndThrowValidationException(bindingResult);

        ChatMsgDto chatMsgDto = chatMsgService.save(chatMsgRequestDto, files);

        messagingTemplate.convertAndSend("/chat/newMessage/" + chatMsgRequestDto.getChatId(), chatMsgDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(chatMsgDto);
    }
}
