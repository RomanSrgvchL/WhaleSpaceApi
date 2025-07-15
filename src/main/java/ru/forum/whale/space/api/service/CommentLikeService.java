package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.Comment;
import ru.forum.whale.space.api.model.CommentLike;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.CommentLikeRepository;
import ru.forum.whale.space.api.repository.CommentRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final SessionUtilService sessionUtilService;

    @Transactional
    public void like(long commentId) {
        Comment comment = findCommentOrElseThrow(commentId);
        User currentUser = sessionUtilService.findCurrentUser();

        if (commentLikeRepository.existsByAuthorAndComment(currentUser, comment)) {
            throw new ResourceAlreadyExistsException("Вы уже ставили лайк на этот комментарий");
        }

        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .author(currentUser)
                .build();

        commentLikeRepository.save(commentLike);
    }

    @Transactional
    public void unlike(long commentId) {
        Comment comment = findCommentOrElseThrow(commentId);

        User currentUser = sessionUtilService.findCurrentUser();

        CommentLike commentLike = commentLikeRepository.findByAuthorAndComment(currentUser, comment)
                .orElseThrow(() -> new ResourceNotFoundException("Лайк на комментарии с указанным ID не найден"));

        commentLikeRepository.delete(commentLike);
    }

    private Comment findCommentOrElseThrow(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Комментарий с указанным ID не найден"));
    }
}
