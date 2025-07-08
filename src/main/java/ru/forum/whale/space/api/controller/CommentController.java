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
import ru.forum.whale.space.api.dto.CommentDto;
import ru.forum.whale.space.api.dto.request.CommentRequestDto;
import ru.forum.whale.space.api.service.CommentService;
import ru.forum.whale.space.api.util.ErrorUtil;

import java.net.URI;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Tag(name = "Комментарии", description = "Операции с комментариями")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> create(@RequestBody @Valid CommentRequestDto commentRequestDto,
                                             BindingResult bindingResult) {
        ErrorUtil.ifHasErrorsBuildMessageAndThrowValidationException(bindingResult);

        CommentDto commentDto = commentService.save(commentRequestDto);
        return ResponseEntity.created(URI.create("/comments/%d".formatted(commentDto.getId()))).body(commentDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id) {
        commentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}