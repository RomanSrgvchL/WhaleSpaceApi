package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.dto.DiscussionDto;
import ru.forum.whale.space.api.dto.DiscussionWithoutRepliesDto;
import ru.forum.whale.space.api.dto.request.DiscussionRequestDto;
import ru.forum.whale.space.api.dto.response.UserResponseDto;
import ru.forum.whale.space.api.service.DiscussionService;
import ru.forum.whale.space.api.util.ErrorUtil;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/discussions")
@RequiredArgsConstructor
@Tag(name = "Обсуждения", description = "Операции с обсуждениями")
public class DiscussionController {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("title", "createdAt");
    private final DiscussionService discussionService;

    @GetMapping
    public ResponseEntity<List<DiscussionWithoutRepliesDto>> getAll(
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order) {
        Sort.Direction direction = "asc".equals(order) ? Sort.Direction.ASC : Sort.Direction.DESC;

        List<DiscussionWithoutRepliesDto> discussions;

        if (ALLOWED_SORT_FIELDS.contains(sort)) {
            discussions = discussionService.findAll(Sort.by(direction, sort));
        } else {
            discussions = discussionService.findAll(Sort.by(direction, "createdAt"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(discussions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscussionDto> getById(@PathVariable int id) {
        DiscussionDto discussionDto = discussionService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(discussionDto);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody @Valid DiscussionRequestDto discussionRequestDto,
                                                  BindingResult bindingResult) {
        ErrorUtil.ifHasErrorsBuildMessageAndThrowValidationException(bindingResult);

        discussionService.save(discussionRequestDto);

        UserResponseDto response = new UserResponseDto(true, "Обсуждение успешно создано!");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseDto> delete(@PathVariable("id") int id) {
        discussionService.deleteById(id);

        UserResponseDto response = new UserResponseDto(true, "Обсуждение успешно удалено!");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
