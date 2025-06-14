package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.dto.ChatDto;
import ru.forum.whale.space.api.dto.request.ChatRequestDto;
import ru.forum.whale.space.api.service.ChatService;
import ru.forum.whale.space.api.util.ErrorUtil;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
@Tag(name = "Чаты", description = "Операции с чатами")
public class ChatController {
    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatDto>> getAll(@RequestParam(value = "sortBy", defaultValue = "") String sortBy) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        List<ChatDto> chats;

        if (Objects.equals(sortBy, "createdAtDesc")) {
            chats = chatService.findAllByUsernameOrderByCreatedAtDescWithMessages(username);
        } else {
            chats = chatService.findAllByUsernameWithMessages(username);
        }

        return ResponseEntity.status(HttpStatus.OK).body(chats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatDto> getById(@PathVariable int id) {
        ChatDto chatDto = chatService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(chatDto);
    }

    @GetMapping("/with/{partnerId}")
    public ResponseEntity<ChatDto> getWithUser(@PathVariable("partnerId") int partnerId) {
        ChatDto chatDto = chatService.findWithUser(partnerId);
        return ResponseEntity.status(HttpStatus.OK).body(chatDto);
    }

    @PostMapping
    public ResponseEntity<ChatDto> create(@RequestBody @Valid ChatRequestDto chatRequestDto,
                                                  BindingResult bindingResult) {
        ErrorUtil.ifHasErrorsBuildMessageAndThrowValidationException(bindingResult);

        ChatDto chatDto = chatService.save(chatRequestDto.getPartnerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(chatDto);
    }
}
