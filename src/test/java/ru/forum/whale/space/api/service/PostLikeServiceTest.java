package ru.forum.whale.space.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.*;
import ru.forum.whale.space.api.repository.PostLikeRepository;
import ru.forum.whale.space.api.repository.PostRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.forum.whale.space.api.util.TestUtil.POST_ID;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {
    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private SessionUtilService sessionUtilService;

    @InjectMocks
    private PostLikeService postLikeService;

    @Test
    void like_whenPostNotFound_thenThrowResourceNotFoundException() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> postLikeService.like(POST_ID));

        assertEquals("Пост с указанным ID не найден", e.getMessage());
    }

    @Test
    void like_whenLikeExists_thenThrowResourceAlreadyExistsException() {
        User currentUser = new User();
        Post post = new Post();

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(postLikeRepository.existsByAuthorAndPost(currentUser, post)).thenReturn(true);

        ResourceAlreadyExistsException e = assertThrows(ResourceAlreadyExistsException.class,
                () -> postLikeService.like(POST_ID));

        assertEquals("Вы уже ставили лайк на этот пост", e.getMessage());
    }

    @Test
    void like_thenSaveLike() {
        User currentUser = new User();
        Post post = new Post();

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(postLikeRepository.existsByAuthorAndPost(currentUser, post)).thenReturn(false);

        postLikeService.like(POST_ID);

        ArgumentCaptor<PostLike> postLikeCaptor = ArgumentCaptor.forClass(PostLike.class);

        verify(postLikeRepository).save(postLikeCaptor.capture());

        PostLike savedPostLike = postLikeCaptor.getValue();

        assertEquals(currentUser, savedPostLike.getAuthor());
        assertEquals(post, savedPostLike.getPost());
    }

    @Test
    void unlike_whenPostNotFound_thenThrowResourceNotFoundException() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> postLikeService.unlike(POST_ID));

        assertEquals("Пост с указанным ID не найден", e.getMessage());
    }

    @Test
    void unlike_whenLikeNotFound_thenThrowResourceNotFoundException() {
        User currentUser = new User();
        Post post = new Post();

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(postLikeRepository.findByAuthorAndPost(currentUser, post)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> postLikeService.unlike(POST_ID));

        assertEquals("Лайк на посте с указанным ID не найден", e.getMessage());
    }

    @Test
    void unlike_thenDeleteLike() {
        User currentUser = new User();
        Post post = new Post();

        PostLike postLike = PostLike.builder()
                .author(currentUser)
                .post(post)
                .build();

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(postLikeRepository.findByAuthorAndPost(currentUser, post)).thenReturn(Optional.of(postLike));

        postLikeService.unlike(POST_ID);

        verify(postLikeRepository).delete(postLike);
    }
}