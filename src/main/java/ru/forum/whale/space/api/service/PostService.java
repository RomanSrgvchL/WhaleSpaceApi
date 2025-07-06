package ru.forum.whale.space.api.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.CommentDto;
import ru.forum.whale.space.api.dto.PostDetailedDto;
import ru.forum.whale.space.api.dto.PostDto;
import ru.forum.whale.space.api.dto.request.PostCreateRequestDto;
import ru.forum.whale.space.api.dto.response.PostCreatedResponseDto;
import ru.forum.whale.space.api.exception.CannotDeleteException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.Post;
import ru.forum.whale.space.api.model.Role;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.PostRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final SessionUtilService sessionUtilService;
    private final ModelMapper mapper;

    public List<PostDto> findAllSorted() {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        return posts.stream()
                .map(this::convertToPostDto)
                .toList();
    }

    @Transactional
    public void deletePost(Long id) {
        User currentUser = sessionUtilService.findCurrentUser();
        Post post = postRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Такого поста не существует"));

        boolean isAdmin = Role.ADMIN.getPrefixRole().equals(currentUser.getRole());
        boolean isAuthor = currentUser.getId().equals(post.getAuthor().getId());

        if (isAdmin || isAuthor) {
            postRepository.delete(post);
        } else {
            throw new CannotDeleteException("Вы не можете удалить данный пост");
        }
    }

    public PostDetailedDto findPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Такого поста не существует"));
        return this.convertToPostDetailedDto(post);
    }

    @Transactional
    public PostCreatedResponseDto createPost(PostCreateRequestDto requestDto) {
        User author = sessionUtilService.findCurrentUser();

        Post mappedPost = mapper.map(requestDto, Post.class);
        mappedPost.setAuthor(author);
        Post savedPost = postRepository.save(mappedPost);

        return buildPostCreatedResponse(savedPost);
    }

    private PostCreatedResponseDto buildPostCreatedResponse(Post post) {
        PostCreatedResponseDto response = mapper.map(post, PostCreatedResponseDto.class);
        response.setAuthorId(post.getAuthor().getId());
        response.setMessage("Пост успешно создан!");
        return response;
    }

    //тут и в методе ниже сделал так, чтобы вместо null возвращались пустые коллекции
    private PostDto convertToPostDto(Post post) {
        PostDto postDto = mapper.map(post, PostDto.class);
        postDto.setCommentCount(post.getComments().size());
        postDto.setLikedIds(
                Optional.ofNullable(post.getLikes())
                        .orElse(List.of())
                        .stream()
                        .map(like -> like.getAuthor().getId())
                        .toList()
        );
        return postDto;
    }

    private PostDetailedDto convertToPostDetailedDto(Post post) {
        PostDetailedDto dto = mapper.map(post, PostDetailedDto.class);
        List<CommentDto> commentDtos = post.getComments().stream()
                .map(comment -> {
                    CommentDto cDto = mapper.map(comment, CommentDto.class);
                    cDto.setLikedUserIds(
                            Optional.ofNullable(comment.getLikes())
                                    .orElse(List.of())
                                    .stream()
                                    .map(like -> like.getAuthor().getId())
                                    .toList()
                    );
                    return cDto;
                })
                .toList();
        dto.setComments(commentDtos);

        dto.setLikedIds(
                Optional.ofNullable(post.getLikes())
                        .orElse(List.of())
                        .stream()
                        .map(like -> like.getAuthor().getId())
                        .toList()
        );

        return dto;
    }
}