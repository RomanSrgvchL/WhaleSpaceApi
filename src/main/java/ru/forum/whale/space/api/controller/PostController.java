package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.forum.whale.space.api.dto.PostWithCommentsDto;
import ru.forum.whale.space.api.dto.PostDto;
import ru.forum.whale.space.api.dto.request.PostRequestDto;
import ru.forum.whale.space.api.service.PostService;
import ru.forum.whale.space.api.util.ErrorUtil;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "Посты", description = "Операции с постами")
public class PostController {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt");
    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostDto>> getAll(@RequestParam(value = "sort", defaultValue = "createdAt") String sort,
                                                @RequestParam(value = "order", defaultValue = "desc") String order) {
        Sort.Direction direction = "asc".equals(order) ? Sort.Direction.ASC : Sort.Direction.DESC;

        if (!ALLOWED_SORT_FIELDS.contains(sort)) {
            sort = "createdAt";
        }

        List<PostDto> postDtos = postService.findAll(Sort.by(direction, sort));
        return ResponseEntity.ok(postDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostWithCommentsDto> getById(@PathVariable("id") Long id) {
        PostWithCommentsDto post = postService.findById(id);
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<PostDto> create(
            @RequestBody @Valid PostRequestDto postRequestDto,
            BindingResult bindingResult) {
        ErrorUtil.ifHasErrorsBuildMessageAndThrowValidationException(bindingResult);

        PostDto postDto = postService.save(postRequestDto);
        return ResponseEntity.created(URI.create("/posts/%d".formatted(postDto.getId()))).body(postDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        postService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
