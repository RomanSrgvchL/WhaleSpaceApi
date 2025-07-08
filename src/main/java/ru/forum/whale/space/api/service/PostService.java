package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.CommentDto;
import ru.forum.whale.space.api.dto.PostWithCommentsDto;
import ru.forum.whale.space.api.dto.PostDto;
import ru.forum.whale.space.api.dto.request.PostRequestDto;
import ru.forum.whale.space.api.exception.CannotDeleteException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.*;
import ru.forum.whale.space.api.repository.PostRepository;
import ru.forum.whale.space.api.repository.UserRepository;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SessionUtilService sessionUtilService;
    private final ModelMapper mapper;

    public List<PostDto> findAll(Sort sort) {
        return postRepository.findAllBy(sort).stream()
                .map(this::convertToPostDto)
                .toList();
    }

    public PostWithCommentsDto findById(long id) {
        Post post = postRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пост с указанным ID не найден"));

        return convertToPostWithCommentsDto(post);
    }

    public List<PostDto> findByUserId(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Пользователь с указанным ID не найден");
        }

        return postRepository.findAllByAuthorId(userId, Sort.by(Sort.Direction.ASC, "createdAt")).stream()
                .map(this::convertToPostDto)
                .toList();
    }

    @Transactional
    public PostDto save(PostRequestDto postRequestDto) {
        User author = sessionUtilService.findCurrentUser();

        Post post = Post.builder()
                .content(postRequestDto.getContent())
                .author(author)
                .build();

        return convertToPostDto(postRepository.save(post));
    }

    @Transactional
    public void deleteById(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пост с указанным ID не найден"));

        User currentUser = sessionUtilService.findCurrentUser();

        boolean isAdmin = Role.ADMIN.getPrefixRole().equals(currentUser.getRole());
        boolean isAuthor = currentUser.getId().equals(post.getAuthor().getId());

        if (isAdmin || isAuthor) {
            postRepository.delete(post);
        } else {
            throw new CannotDeleteException("Вы не можете удалить данный пост");
        }
    }

    private PostDto convertToPostDto(Post post) {
        PostDto postDto = mapper.map(post, PostDto.class);

        postDto.setCommentCount(post.getComments().size());

        postDto.setLikedUserIds(post.getLikes().stream()
                .map(like -> like.getAuthor().getId())
                .toList()
        );

        return postDto;
    }

    private PostWithCommentsDto convertToPostWithCommentsDto(Post post) {
        PostWithCommentsDto postWithCommentsDto = mapper.map(post, PostWithCommentsDto.class);

        postWithCommentsDto.setComments(post.getComments().stream()
                .map(comment -> {
                    CommentDto commentDto = mapper.map(comment, CommentDto.class);

                    commentDto.setLikedUserIds(comment.getLikes().stream()
                            .map(like -> like.getAuthor().getId())
                            .toList()
                    );

                    return commentDto;
                })
                .sorted(Comparator.comparing(CommentDto::getCreatedAt).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new))
        );

        postWithCommentsDto.setLikedUserIds(post.getLikes().stream()
                .map(like -> like.getAuthor().getId())
                .collect(Collectors.toSet())
        );

        return postWithCommentsDto;
    }
}