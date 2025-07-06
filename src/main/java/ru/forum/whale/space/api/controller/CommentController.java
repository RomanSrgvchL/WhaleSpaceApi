package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.forum.whale.space.api.dto.request.CommentCreateRequestDto;
import ru.forum.whale.space.api.dto.response.CommentCreatedResponseDto;
import ru.forum.whale.space.api.service.CommentService;
import ru.forum.whale.space.api.util.ErrorUtil;

@RestController
@RequestMapping("/posts/{postId:\\d+}/comments")
@RequiredArgsConstructor
@Tag(name = "Комментарии", description = "Операции с комментариями")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentCreatedResponseDto> createComment(
            @RequestBody @Valid CommentCreateRequestDto commentCreateRequestDto,
            BindingResult bindingResult,
            @PathVariable Long postId) {
        ErrorUtil.ifHasErrorsBuildMessageAndThrowValidationException(bindingResult);
        commentCreateRequestDto.setPostId(postId);

        CommentCreatedResponseDto comment = commentService.createComment(commentCreateRequestDto);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId:\\d+}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent()
                .build();
    }
}