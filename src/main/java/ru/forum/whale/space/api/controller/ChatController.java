package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.dto.ChatDto;
import ru.forum.whale.space.api.dto.ChatWithLastMsgDto;
import ru.forum.whale.space.api.dto.request.ChatRequestDto;
import ru.forum.whale.space.api.service.ChatService;

import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
@Tag(name = "Чаты", description = "Операции с чатами")
public class ChatController {
    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatWithLastMsgDto>> getAll() {
        List<ChatWithLastMsgDto> chatDtos = chatService.findAll();
        return ResponseEntity.ok(chatDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatDto> getById(@PathVariable long id) {
        ChatDto chatDto = chatService.findById(id);
        return ResponseEntity.ok(chatDto);
    }

    @GetMapping("/with/{partnerId}")
    public ResponseEntity<ChatDto> getWithUser(@PathVariable long partnerId) {
        ChatDto chatDto = chatService.findWithUser(partnerId);
        return ResponseEntity.ok(chatDto);
    }

    @PostMapping
    public ResponseEntity<ChatDto> create(@RequestBody @Valid ChatRequestDto chatRequestDto) {
        ChatDto chatDto = chatService.save(chatRequestDto.getPartnerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(chatDto);
    }
}
