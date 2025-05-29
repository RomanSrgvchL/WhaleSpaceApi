package ru.forum.whale.space.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.dto.ChatDto;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.service.ChatService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatDto>> getAll() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ChatDto> chats = chatService.findAllWithMessagesForUser(username);
        return ResponseEntity.status(HttpStatus.OK).body(chats);
    }

    @GetMapping("/createdAtDesc")
    public ResponseEntity<List<ChatDto>> getAllByCreatedAtDesc() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ChatDto> chats = chatService.findAllByCreatedAtDescWithMessagesForUser(username);
        return ResponseEntity.status(HttpStatus.OK).body(chats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatDto> getById(@PathVariable int id) {
        Optional<ChatDto> chatDto = chatService.findById(id);
        if (chatDto.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(chatDto.get());
        }
        throw new ResourceNotFoundException("Чат с указанным id не найден");
    }

    @GetMapping("/byUsers/{userId1}-{userId2}")
    public ResponseEntity<Integer> getChatIdByUsers(@PathVariable int userId1, @PathVariable int userId2) {
        Optional<Integer> chatId = chatService.findChatIdByUsers(userId1, userId2);
        if (chatId.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(chatId.get());
        }
        throw new ResourceNotFoundException("Чат между указанными пользователями не найден");
    }

    @PostMapping("/create/{userId1}-{userId2}")
    public ResponseEntity<Integer> create(@PathVariable int userId1, @PathVariable int userId2) {
        Optional<Integer> chatId = chatService.findChatIdByUsers(userId1, userId2);
        if (chatId.isEmpty()) {
            Integer createdChatId = chatService.save(userId1, userId2);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdChatId);
        }
        throw new ResourceAlreadyExistsException("Чат между указанными пользователями уже существует");
    }
}
