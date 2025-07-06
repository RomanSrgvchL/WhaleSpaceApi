package ru.forum.whale.space.api.service;

import java.util.Optional;
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
@RequiredArgsConstructor
public class CommentLikeService {

    private final SessionUtilService sessionUtilService;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void likeComment(Long commentId) {
        Comment comment = findCommentOrElseThrow(commentId);
        User currentUser = sessionUtilService.findCurrentUser();

        Optional<CommentLike> maybeLike = commentLikeRepository.findByAuthorAndComment(currentUser, comment);
        if (maybeLike.isPresent()) {
            throw new ResourceAlreadyExistsException("Вы уже ставили лайк на этот комментарий");
        }

        CommentLike commentLike = buildCommentLike(currentUser, comment);

        commentLikeRepository.save(commentLike);
    }

    @Transactional
    public void unlikeComment(Long commentId) {
        Comment comment = findCommentOrElseThrow(commentId);

        User currentUser = sessionUtilService.findCurrentUser();
        Optional<CommentLike> maybeLike = commentLikeRepository.findByAuthorAndComment(currentUser, comment);
        if (maybeLike.isEmpty()) {
            return;
        }

        CommentLike postLike = maybeLike.get();
        commentLikeRepository.delete(postLike);
    }

    private Comment findCommentOrElseThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Комментарий с таким id не найден"));
    }

    private CommentLike buildCommentLike(User author, Comment comment) {
        CommentLike commentLike = new CommentLike();
        commentLike.setComment(comment);
        commentLike.setAuthor(author);
        return commentLike;
    }
}