package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.docs.discussion.CreateDiscussionDocs;
import ru.forum.whale.space.api.docs.discussion.DeleteDiscussionByIdDocs;
import ru.forum.whale.space.api.docs.discussion.GetAllDiscussionsDocs;
import ru.forum.whale.space.api.docs.discussion.GetDiscussionByIdDocs;
import ru.forum.whale.space.api.dto.DiscussionDto;
import ru.forum.whale.space.api.dto.DiscussionMetaDto;
import ru.forum.whale.space.api.dto.request.DiscussionRequestDto;
import ru.forum.whale.space.api.service.DiscussionService;
import ru.forum.whale.space.api.util.Messages;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/discussions")
@RequiredArgsConstructor
@Tag(name = "Обсуждения", description = "Операции с обсуждениями")
public class DiscussionController {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("title", "createdAt");
    private final DiscussionService discussionService;

    @GetAllDiscussionsDocs
    @GetMapping
    public ResponseEntity<List<DiscussionMetaDto>> getAll(
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order) {
        Sort.Direction direction = "asc".equals(order) ? Sort.Direction.ASC : Sort.Direction.DESC;

        if (!ALLOWED_SORT_FIELDS.contains(sort)) {
            sort = "createdAt";
        }

        List<DiscussionMetaDto> discussionDtos = discussionService.findAll(Sort.by(direction, sort));
        return ResponseEntity.ok(discussionDtos);
    }

    @GetDiscussionByIdDocs
    @GetMapping("/{id}")
    public ResponseEntity<DiscussionDto> getById(@PathVariable @Positive(message = Messages.ID_POSITIVE) long id) {
        DiscussionDto discussionDto = discussionService.findById(id);
        return ResponseEntity.ok(discussionDto);
    }

    @CreateDiscussionDocs
    @PostMapping
    public ResponseEntity<DiscussionDto> create(@RequestBody @Valid DiscussionRequestDto discussionRequestDto) {
        DiscussionDto discussionDto = discussionService.save(discussionRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(discussionDto);
    }

    @DeleteDiscussionByIdDocs
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable @Positive(message = Messages.ID_POSITIVE) long id) {
        discussionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
