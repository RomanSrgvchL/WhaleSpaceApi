package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.forum.whale.space.api.dto.PostDetailedDto;
import ru.forum.whale.space.api.dto.PostDto;
import ru.forum.whale.space.api.service.PostService;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "Посты", description = "Операции с постами")
public class PostController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts() {
        List<PostDto> posts = postService.findAllSorted();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<PostDetailedDto> getPost(@PathVariable Long id) {
        PostDetailedDto post = postService.findPost(id);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent()
                .build();
    }
}
