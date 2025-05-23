package ru.forum.whale.space.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.dto.ChatDto;
import ru.forum.whale.space.api.services.ChatsService;

import java.util.Optional;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatsService chatsService;

    @GetMapping("/{id}")
    public ResponseEntity<ChatDto> getChat(@PathVariable int id) {
        Optional<ChatDto> chatDto = chatsService.findById(id);
        return chatDto.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/byUsers/{userId1}-{userId2}")
    public ResponseEntity<Integer> getChatId(@PathVariable int userId1, @PathVariable int userId2) {
        Optional<Integer> chatId = chatsService.findChatIdByUsers(userId1, userId2);
        return chatId.map(id -> new ResponseEntity<>(id, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/create/{userId1}-{userId2}")
    public ResponseEntity<Integer> createChat(@PathVariable int userId1, @PathVariable int userId2) {
        Optional<Integer> chatId = chatsService.findChatIdByUsers(userId1, userId2);
        if (chatId.isEmpty()) {
            Integer createdChatId = chatsService.save(userId1, userId2);
            if (createdChatId != null) {
                return new ResponseEntity<>(createdChatId, HttpStatus.CREATED);
            }
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}
