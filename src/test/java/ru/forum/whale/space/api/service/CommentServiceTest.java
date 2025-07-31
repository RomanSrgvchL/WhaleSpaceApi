package ru.forum.whale.space.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.forum.whale.space.api.dto.CommentDto;
import ru.forum.whale.space.api.dto.request.CommentRequestDto;
import ru.forum.whale.space.api.exception.CannotDeleteException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.mapper.CommentMapper;
import ru.forum.whale.space.api.model.Comment;
import ru.forum.whale.space.api.model.Post;
import ru.forum.whale.space.api.model.Role;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.CommentRepository;
import ru.forum.whale.space.api.repository.PostRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.forum.whale.space.api.util.TestUtil.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private SessionUtilService sessionUtilService;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    @Test
    void save_whenPostNotFound_thenThrowResourceNotFoundException() {
        CommentRequestDto commentRequestDto = new CommentRequestDto(POST_ID, "new comment");

        when(postRepository.findById(commentRequestDto.getPostId())).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> commentService.save(commentRequestDto));

        assertEquals("Пост с указанным ID не найден", e.getMessage());
    }

    @Test
    void save_thenReturnCommentDto() {
        User currentUser = new User();
        Post post = new Post();

        CommentRequestDto commentRequestDto = new CommentRequestDto(POST_ID, "new comment");

        Comment comment = Comment.builder()
                .content(commentRequestDto.getContent())
                .author(currentUser)
                .post(post)
                .build();

        CommentDto expected = new CommentDto();

        when(postRepository.findById(commentRequestDto.getPostId())).thenReturn(Optional.of(post));
        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.commentToCommentDto(comment)).thenReturn(expected);

        CommentDto result = commentService.save(commentRequestDto);

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);

        verify(commentRepository).save(commentCaptor.capture());

        Comment savedComment = commentCaptor.getValue();

        assertEquals(expected, result);
        assertEquals(currentUser, savedComment.getAuthor());
        assertEquals(post, savedComment.getPost());
        assertEquals(commentRequestDto.getContent(), savedComment.getContent());
    }

    @Test
    void deleteById_whenCommentNotFound_thenThrowResourceNotFoundException() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> commentService.deleteById(COMMENT_ID));

        assertEquals("Комментарий с указанным ID не найден", e.getMessage());
    }

    @Test
    void deleteById_whenUserIsNotAuthorOrAdmin_thenThrowCannotDeleteException() {
        User currentUser = createUser(USER_ID, Role.USER.getRoleName());
        User author = createUser(2L);

        Comment comment = createComment(author);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);

        CannotDeleteException e = assertThrows(CannotDeleteException.class,
                () -> commentService.deleteById(COMMENT_ID));

        assertEquals("Вы не можете удалить данный комментарий", e.getMessage());
    }

    @Test
    void deleteById_thenDeleteComment() {
        User currentUser = createUser(USER_ID, Role.ADMIN.getRoleName());

        Comment comment = createComment(currentUser);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);

        commentService.deleteById(COMMENT_ID);

        verify(commentRepository).delete(comment);
    }

    private Comment createComment(User author) {
        return Comment.builder()
                .author(author)
                .build();
    }
}