package ru.forum.whale.space.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.forum.whale.space.api.service.CommentLikeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments/{commentId}/likes")
public class CommentLikeController {
    private final CommentLikeService commentLikeService;

    @PostMapping
    public ResponseEntity<Void> like(@PathVariable long commentId) {
        commentLikeService.like(commentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> unlike(@PathVariable long commentId) {
        commentLikeService.unlike(commentId);
        return ResponseEntity.noContent().build();
    }
}