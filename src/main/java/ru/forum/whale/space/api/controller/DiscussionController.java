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

        List<DiscussionWithoutRepliesDto> discussionDtos;

        if (ALLOWED_SORT_FIELDS.contains(sort)) {
            discussionDtos = discussionService.findAll(Sort.by(direction, sort));
        } else {
            discussionDtos = discussionService.findAll(Sort.by(direction, "createdAt"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(discussionDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscussionDto> getById(@PathVariable long id) {
        DiscussionDto discussionDto = discussionService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(discussionDto);
    }

    @PostMapping
    public ResponseEntity<DiscussionDto> create(@RequestBody @Valid DiscussionRequestDto discussionRequestDto,
                                                BindingResult bindingResult) {
        ErrorUtil.ifHasErrorsBuildMessageAndThrowValidationException(bindingResult);

        DiscussionDto discussionDto = discussionService.save(discussionRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(discussionDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        discussionService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
