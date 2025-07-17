package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.docs.chat.CreateChatDocs;
import ru.forum.whale.space.api.docs.chat.GetAllChatsDocs;
import ru.forum.whale.space.api.docs.chat.GetChatByIdDocs;
import ru.forum.whale.space.api.docs.chat.GetChatWithUserDocs;
import ru.forum.whale.space.api.dto.ChatDto;
import ru.forum.whale.space.api.dto.ChatWithLastMsgDto;
import ru.forum.whale.space.api.dto.request.ChatRequestDto;
import ru.forum.whale.space.api.service.ChatService;
import ru.forum.whale.space.api.util.Messages;

import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
@Tag(name = "Чаты", description = "Операции с чатами")
public class ChatController {
    private final ChatService chatService;

    @GetAllChatsDocs
    @GetMapping
    public ResponseEntity<List<ChatWithLastMsgDto>> getAll() {
        List<ChatWithLastMsgDto> chatDtos = chatService.findAll();
        return ResponseEntity.ok(chatDtos);
    }

    @GetChatByIdDocs
    @GetMapping("/{id}")
    public ResponseEntity<ChatDto> getById(@PathVariable @Positive(message = Messages.ID_POSITIVE) long id) {
        ChatDto chatDto = chatService.findById(id);
        return ResponseEntity.ok(chatDto);
    }

    @GetChatWithUserDocs
    @GetMapping("/with/{partnerId}")
    public ResponseEntity<ChatDto> getWithUser(@PathVariable @Positive(message = Messages.ID_POSITIVE) long partnerId) {
        ChatDto chatDto = chatService.findWithUser(partnerId);
        return ResponseEntity.ok(chatDto);
    }

    @CreateChatDocs
    @PostMapping
    public ResponseEntity<ChatDto> create(@RequestBody @Valid ChatRequestDto chatRequestDto) {
        ChatDto chatDto = chatService.save(chatRequestDto.getPartnerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(chatDto);
    }
}
