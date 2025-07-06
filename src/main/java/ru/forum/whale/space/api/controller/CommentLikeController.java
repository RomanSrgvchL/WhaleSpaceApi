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
@RequestMapping("/posts/{postId:\\d+}/comments/{commentId:\\d+}/likes")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @PostMapping
    public ResponseEntity<Void> likeComment(@PathVariable Long commentId) {
        commentLikeService.likeComment(commentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> unlikeComment(@PathVariable Long commentId) {
        commentLikeService.unlikeComment(commentId);
        return ResponseEntity.noContent().build();
    }
}