package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.docs.post.*;
import ru.forum.whale.space.api.dto.PostDto;
import ru.forum.whale.space.api.dto.PostWithCommentsDto;
import ru.forum.whale.space.api.dto.request.PostRequestDto;
import ru.forum.whale.space.api.service.PostService;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "Посты", description = "Операции с постами")
public class PostController {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt");
    private final PostService postService;

    @GetAllPostsDocs
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

    @GetPostByIdDocs
    @GetMapping("/{id}")
    public ResponseEntity<PostWithCommentsDto> getById(@PathVariable long id) {
        PostWithCommentsDto postWithCommentsDto = postService.findById(id);
        return ResponseEntity.ok(postWithCommentsDto);
    }

    @GetPostByUserIdDocs
    @GetMapping("user/{userId}")
    public ResponseEntity<List<PostDto>> getByUserId(@PathVariable long userId) {
        List<PostDto> postDtos = postService.findByUserId(userId);
        return ResponseEntity.ok(postDtos);
    }

    @CreatePostDocs
    @PostMapping
    public ResponseEntity<PostDto> create(@RequestParam(value = "files", required = false) List<MultipartFile> files,
                                          @ModelAttribute @Valid PostRequestDto postRequestDto) {
        PostDto postDto = postService.save(postRequestDto, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(postDto);
    }

    @DeletePostByIdDocs
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id) {
        postService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
