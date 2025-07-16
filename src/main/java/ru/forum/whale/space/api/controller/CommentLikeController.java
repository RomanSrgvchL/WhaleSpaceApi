package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.forum.whale.space.api.docs.commentlike.LikeCommentDocs;
import ru.forum.whale.space.api.docs.commentlike.UnlikeCommentDocs;
import ru.forum.whale.space.api.service.CommentLikeService;

@RestController
@RequestMapping("/comments/{commentId}/likes")
@RequiredArgsConstructor
@Tag(name = "Лайки на комментариях", description = "Операции с лайками на комменатриях")
public class CommentLikeController {
    private final CommentLikeService commentLikeService;

    @LikeCommentDocs
    @PostMapping
    public ResponseEntity<Void> like(@PathVariable long commentId) {
        commentLikeService.like(commentId);
        return ResponseEntity.ok().build();
    }

    @UnlikeCommentDocs
    @DeleteMapping
    public ResponseEntity<Void> unlike(@PathVariable long commentId) {
        commentLikeService.unlike(commentId);
        return ResponseEntity.noContent().build();
    }
}