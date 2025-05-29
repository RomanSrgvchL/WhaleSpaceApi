package ru.forum.whale.space.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.dto.DiscussionDto;
import ru.forum.whale.space.api.dto.request.DiscussionRequestDto;
import ru.forum.whale.space.api.dto.response.UserResponseDto;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.service.DiscussionService;
import ru.forum.whale.space.api.util.ErrorUtil;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/discussions")
@RequiredArgsConstructor
public class DiscussionController {
    private final DiscussionService discussionService;

    @GetMapping("/{id}")
    public ResponseEntity<DiscussionDto> getById(@PathVariable int id) {
        Optional<DiscussionDto> discussionDto = discussionService.findById(id);
        if (discussionDto.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(discussionDto.get());
        }
        throw new ResourceNotFoundException("Обсуждение с указанным id не найдено");
    }

    @GetMapping
    public ResponseEntity<List<DiscussionDto>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(discussionService.findAll());
    }

    @GetMapping("/createdAtDesc")
    public ResponseEntity<List<DiscussionDto>> getAllByCreatedAtDesc() {
        return ResponseEntity.status(HttpStatus.OK).body(discussionService.findAllByCreatedAtDesc());
    }

    @PostMapping("/byTitle")
    public ResponseEntity<Integer> getDiscussionIdByTitle(@Valid @RequestBody DiscussionRequestDto requestDto) {
        Optional<DiscussionDto> discussionDto = discussionService.findByTitle(requestDto.getTitle());
        if (discussionDto.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(discussionDto.get().getId());
        }
        throw new ResourceNotFoundException("Обсуждение не найдено");
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> create(@RequestBody @Valid
                                                  DiscussionRequestDto discussionRequestDto,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ErrorUtil.buildMessageAndThrowValidationException(bindingResult);
        }

        Optional<DiscussionDto> discussionDto = discussionService.findByTitle(discussionRequestDto.getTitle());

        if (discussionDto.isPresent()) {
            throw new ResourceAlreadyExistsException("Обсуждение с таким названием уже сущесвтует");
        }

        discussionService.save(discussionRequestDto);

        UserResponseDto response = new UserResponseDto(true, "Обсуждение успешно создано!");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/delete")
    public ResponseEntity<UserResponseDto> deleteByTitle(@Valid @RequestBody
                                                         DiscussionRequestDto discussionRequestDto,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ErrorUtil.buildMessageAndThrowValidationException(bindingResult);
        }

        Optional<DiscussionDto> discussionDto = discussionService.findByTitle(discussionRequestDto.getTitle());

        if (discussionDto.isEmpty()) {
            throw new ResourceNotFoundException("Обсуждение не найдено");
        }

        discussionService.deleteByTitle(discussionRequestDto.getTitle());

        UserResponseDto response = new UserResponseDto(true, "Обсуждение успешно удалено");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
