package ru.forum.whale.space.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.dto.CommentDto;
import ru.forum.whale.space.api.dto.PostDto;
import ru.forum.whale.space.api.dto.PostWithCommentsDto;
import ru.forum.whale.space.api.dto.request.PostRequestDto;
import ru.forum.whale.space.api.enums.PostSortFields;
import ru.forum.whale.space.api.enums.StorageBucket;
import ru.forum.whale.space.api.exception.CannotDeleteException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.mapper.CommentMapper;
import ru.forum.whale.space.api.mapper.PostMapper;
import ru.forum.whale.space.api.model.*;
import ru.forum.whale.space.api.repository.PostRepository;
import ru.forum.whale.space.api.repository.UserRepository;
import ru.forum.whale.space.api.util.FileUtil;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.forum.whale.space.api.util.TestUtil.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionUtilService sessionUtilService;

    @Mock
    private MinioService minioService;

    @Mock
    private PostMapper postMapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private PostService postService;

    @Test
    void findAll_whenUserIsAuthenticated_thenReturnPostDtoList() {
        long likeAuthorId = 1L;

        User authenticatedUser = new User();

        Post post = createPostWithLikeAndComment(likeAuthorId);

        List<Post> posts = List.of(post);

        PostDto postDto = new PostDto();

        Sort sort = Sort.unsorted();

        when(sessionUtilService.findAuthenticatedUser()).thenReturn(Optional.of(authenticatedUser));
        when(postRepository.findAllByAuthorNot(authenticatedUser, sort)).thenReturn(posts);
        when(postMapper.postToPostDto(post)).thenReturn(postDto);

        List<PostDto> result = postService.findAll(sort);

        verify(postRepository).findAllByAuthorNot(authenticatedUser, sort);
        verify(postRepository, never()).findAllBy(any(Sort.class));

        assertEquals(posts.size(), result.size());
        assertEquals(postDto, result.getFirst());
        assertEquals(post.getLikes().size(), postDto.getLikedUserIds().size());
        assertEquals(post.getComments().size(), postDto.getCommentCount());
        assertEquals(likeAuthorId, postDto.getLikedUserIds().getFirst());
    }

    @Test
    void findAll_whenUserIsNotAuthenticated_thenReturnPostDtoList() {
        Post post = createPostWithLikeAndComment(USER_ID);

        List<Post> posts = List.of(post);

        PostDto postDto = new PostDto();

        Sort sort = Sort.unsorted();

        when(sessionUtilService.findAuthenticatedUser()).thenReturn(Optional.empty());
        when(postRepository.findAllBy(sort)).thenReturn(posts);
        when(postMapper.postToPostDto(post)).thenReturn(postDto);

        List<PostDto> result = postService.findAll(sort);

        verify(postRepository).findAllBy(sort);
        verify(postRepository, never()).findAllByAuthorNot(any(User.class), any(Sort.class));

        assertEquals(posts.size(), result.size());
        assertEquals(postDto, result.getFirst());
        assertEquals(post.getLikes().size(), postDto.getLikedUserIds().size());
        assertEquals(post.getComments().size(), postDto.getCommentCount());
        assertEquals(USER_ID, postDto.getLikedUserIds().getFirst());
    }

    @Test
    void findById_whenPostNotFound_thenThrowResourceNotFoundException() {
        when(postRepository.findDetailedById(POST_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> postService.findById(POST_ID));

        assertEquals("Пост с указанным ID не найден", e.getMessage());
    }

    @Test
    void findById_whenReturnPostWithSortedCommentsDto() {
        User user = User.builder()
                .id(USER_ID)
                .build();

        PostLike postLike = createPostLike(user);

        CommentLike commentLike = CommentLike.builder()
                .author(user)
                .build();

        Comment comment1 = createComment(List.of(commentLike), "2025-01-01T10:00:00Z");
        Comment comment2 = createComment(Collections.emptyList(), "2025-01-02T10:00:00Z");

        Post post = Post.builder()
                .likes(Set.of(postLike))
                .comments(Set.of(comment1, comment2))
                .build();

        PostWithCommentsDto postWithCommentsDto = new PostWithCommentsDto();

        CommentDto commentDto1 = createCommentDto("2025-01-01T10:00:00Z");
        CommentDto commentDto2 = createCommentDto("2025-01-02T10:00:00Z");

        when(postRepository.findDetailedById(POST_ID)).thenReturn(Optional.of(post));
        when(postMapper.postToPostWithCommentsDto(post)).thenReturn(postWithCommentsDto);
        when(commentMapper.commentToCommentDto(comment1)).thenReturn(commentDto1);
        when(commentMapper.commentToCommentDto(comment2)).thenReturn(commentDto2);

        PostWithCommentsDto result = postService.findById(POST_ID);

        LinkedHashSet<CommentDto> commentDtos = (LinkedHashSet<CommentDto>) result.getComments();

        assertEquals(postWithCommentsDto, result);
        assertEquals(post.getLikes().size(), result.getLikedUserIds().size());
        assertEquals(post.getComments().size(), result.getComments().size());
        assertTrue(result.getLikedUserIds().contains(user.getId()));
        assertEquals(commentDtos.getFirst(), commentDto2);
        assertEquals(commentDtos.getLast(), commentDto1);
        assertEquals(comment1.getLikes().size(), commentDto1.getLikedUserIds().size());
        assertEquals(user.getId(), commentDto1.getLikedUserIds().getFirst());
    }

    @Test
    void findByUserId_whenUserNotFound_thenThrowResourceNotFoundException() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> postService.findByUserId(USER_ID));

        assertEquals("Пользователь с указанным ID не найден", e.getMessage());
    }

    @Test
    void findByUserId_thenReturnPostDtoList() {
        Post post = createPostWithLikeAndComment(USER_ID);

        List<Post> posts = List.of(post);

        PostDto postDto = new PostDto();

        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(postRepository.findAllByAuthorId(USER_ID, Sort.by(Sort.Direction.DESC,
                PostSortFields.CREATED_AT.getFieldName()))).thenReturn(posts);
        when(postMapper.postToPostDto(post)).thenReturn(postDto);

        List<PostDto> result = postService.findByUserId(USER_ID);

        assertEquals(posts.size(), result.size());
        assertEquals(postDto, result.getFirst());
        assertEquals(post.getLikes().size(), postDto.getLikedUserIds().size());
        assertEquals(post.getComments().size(), postDto.getCommentCount());
        assertEquals(USER_ID, postDto.getLikedUserIds().getFirst());
    }

    @Test
    void save_thenReturnPostDto() {
        PostRequestDto postRequestDto = new PostRequestDto("new post");

        List<MultipartFile> files = createMockFiles(2);

        List<String> imageFileNames = List.of("file1", "file2");

        User currentUser = new User();

        PostDto expected = new PostDto();

        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(minioService.uploadImages(eq(PostService.POST_FILES_BUCKET), eq(files), anyString()))
                .thenReturn(imageFileNames);
        when(postMapper.postToPostDto(any(Post.class))).thenReturn(expected);

        try (MockedStatic<FileUtil> mockedFileUtil = mockStatic(FileUtil.class)) {
            PostDto result = postService.save(postRequestDto, files);

            ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);

            mockedFileUtil.verify(() -> FileUtil.validateFiles(files));
            verify(postRepository, times(2)).save(postCaptor.capture());

            Post savePost = postCaptor.getAllValues().get(1);

            assertEquals(expected, result);
            assertEquals(currentUser, savePost.getAuthor());
            assertEquals(postRequestDto.getContent(), savePost.getContent());
            assertEquals(imageFileNames, savePost.getImageFileNames());
        }
    }

    @Test
    void deleteById_whenPostNotFound_thenThrowResourceNotFoundException() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> postService.deleteById(POST_ID));

        assertEquals("Пост с указанным ID не найден", e.getMessage());
    }

    @Test
    void deleteById_whenUserIsNotAuthorOrAdmin_thenThrowCannotDeleteException() {
        User currentUser = createUser(USER_ID, Role.USER.getRoleName());
        User author = createUser(2L);

        Post post = Post.builder()
                .author(author)
                .build();

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);

        CannotDeleteException e = assertThrows(CannotDeleteException.class,
                () -> postService.deleteById(POST_ID));

        assertEquals("Вы не можете удалить данный пост", e.getMessage());
    }

    @Test
    void deleteById_thenDeletePost() {
        User currentUser = createUser(USER_ID, Role.ADMIN.getRoleName());

        Post post = Post.builder()
                .author(currentUser)
                .imageFileNames(List.of("file1", "file2"))
                .build();

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);

        postService.deleteById(POST_ID);

        verify(minioService).deleteFiles(StorageBucket.POST_FILES_BUCKET.getBucketName(), post.getImageFileNames());
        verify(postRepository).delete(post);
    }

    private Post createPostWithLikeAndComment(long authorId) {
        User user = User.builder()
                .id(authorId)
                .build();

        PostLike like = createPostLike(user);

        Comment comment = new Comment();

        return Post.builder()
                .likes(Set.of(like))
                .comments(Set.of(comment))
                .build();
    }

    private Comment createComment(List<CommentLike> likes, String createdAt) {
        return Comment.builder()
                .likes(likes)
                .createdAt(ZonedDateTime.parse(createdAt))
                .build();
    }

    private CommentDto createCommentDto(String createdAt) {
        return CommentDto.builder()
                .createdAt(ZonedDateTime.parse(createdAt))
                .build();
    }

    private PostLike createPostLike(User author) {
        return PostLike.builder()
                .author(author)
                .build();
    }
}