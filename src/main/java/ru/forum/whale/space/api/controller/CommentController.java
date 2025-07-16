package ru.forum.whale.space.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.forum.whale.space.api.docs.comment.CreateCommentDocs;
import ru.forum.whale.space.api.docs.comment.DeleteCommentByIdDocs;
import ru.forum.whale.space.api.dto.CommentDto;
import ru.forum.whale.space.api.dto.request.CommentRequestDto;
import ru.forum.whale.space.api.service.CommentService;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Tag(name = "Комментарии", description = "Операции с комментариями")
public class CommentController {
    private final CommentService commentService;

    @CreateCommentDocs
    @PostMapping
    public ResponseEntity<CommentDto> create(@RequestBody @Valid CommentRequestDto commentRequestDto) {
        CommentDto commentDto = commentService.save(commentRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentDto);
    }

    @DeleteCommentByIdDocs
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id) {
        commentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}