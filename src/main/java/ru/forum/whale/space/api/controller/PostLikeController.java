package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.forum.whale.space.api.docs.postlike.LikePostDocs;
import ru.forum.whale.space.api.docs.postlike.UnlikePostDocs;
import ru.forum.whale.space.api.service.PostLikeService;
import ru.forum.whale.space.api.util.Messages;

@Validated
@RestController
@RequestMapping("/posts/{postId}/likes")
@RequiredArgsConstructor
@Tag(name = "Лайки на постах", description = "Операции с лайками на постах")
public class PostLikeController {
    private final PostLikeService postLikeService;

    @LikePostDocs
    @PostMapping
    public ResponseEntity<Void> like(@PathVariable @Positive(message = Messages.ID_POSITIVE) long postId) {
        postLikeService.like(postId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @UnlikePostDocs
    @DeleteMapping
    public ResponseEntity<Void> unlike(@PathVariable @Positive(message = Messages.ID_POSITIVE) long postId) {
        postLikeService.unlike(postId);
        return ResponseEntity.noContent().build();
    }
}
