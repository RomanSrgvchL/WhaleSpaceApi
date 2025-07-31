package ru.forum.whale.space.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.Comment;
import ru.forum.whale.space.api.model.CommentLike;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.CommentLikeRepository;
import ru.forum.whale.space.api.repository.CommentRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.forum.whale.space.api.util.TestUtil.COMMENT_ID;

@ExtendWith(MockitoExtension.class)
class CommentLikeServiceTest {
    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private SessionUtilService sessionUtilService;

    @InjectMocks
    private CommentLikeService commentLikeService;

    @Test
    void like_whenCommentNotFound_thenThrowResourceNotFoundException() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> commentLikeService.like(COMMENT_ID));

        assertEquals("Комментарий с указанным ID не найден", e.getMessage());
    }

    @Test
    void like_whenLikeExists_thenThrowResourceAlreadyExistsException() {
        User currentUser = new User();
        Comment comment = new Comment();

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(commentLikeRepository.existsByAuthorAndComment(currentUser, comment)).thenReturn(true);

        ResourceAlreadyExistsException e = assertThrows(ResourceAlreadyExistsException.class,
                () -> commentLikeService.like(COMMENT_ID));

        assertEquals("Вы уже ставили лайк на этот комментарий", e.getMessage());
    }

    @Test
    void like_thenSaveLike() {
        User currentUser = new User();
        Comment comment = new Comment();

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(commentLikeRepository.existsByAuthorAndComment(currentUser, comment)).thenReturn(false);

        commentLikeService.like(COMMENT_ID);

        ArgumentCaptor<CommentLike> commentLikeCaptor = ArgumentCaptor.forClass(CommentLike.class);

        verify(commentLikeRepository).save(commentLikeCaptor.capture());

        CommentLike savedCommentLike = commentLikeCaptor.getValue();

        assertEquals(currentUser, savedCommentLike.getAuthor());
        assertEquals(comment, savedCommentLike.getComment());
    }

    @Test
    void unlike_whenCommentNotFound_thenThrowResourceNotFoundException() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> commentLikeService.unlike(COMMENT_ID));

        assertEquals("Комментарий с указанным ID не найден", e.getMessage());
    }

    @Test
    void unlike_whenLikeNotFound_thenThrowResourceNotFoundException() {
        User currentUser = new User();
        Comment comment = new Comment();

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(commentLikeRepository.findByAuthorAndComment(currentUser, comment)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> commentLikeService.unlike(COMMENT_ID));

        assertEquals("Лайк на комментарии с указанным ID не найден", e.getMessage());
    }

    @Test
    void unlike_thenDeleteLike() {
        User currentUser = new User();
        Comment comment = new Comment();

        CommentLike commentLike = CommentLike.builder()
                .author(currentUser)
                .comment(comment)
                .build();

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(commentLikeRepository.findByAuthorAndComment(currentUser, comment)).thenReturn(Optional.of(commentLike));

        commentLikeService.unlike(COMMENT_ID);

        verify(commentLikeRepository).delete(commentLike);
    }
}