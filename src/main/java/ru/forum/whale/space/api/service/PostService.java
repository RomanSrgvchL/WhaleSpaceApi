package ru.forum.whale.space.api.service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.dto.CommentDto;
import ru.forum.whale.space.api.dto.PostWithCommentsDto;
import ru.forum.whale.space.api.dto.PostDto;
import ru.forum.whale.space.api.dto.request.PostRequestDto;
import ru.forum.whale.space.api.exception.CannotDeleteException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.mapper.CommentMapper;
import ru.forum.whale.space.api.mapper.PostMapper;
import ru.forum.whale.space.api.model.*;
import ru.forum.whale.space.api.repository.PostRepository;
import ru.forum.whale.space.api.repository.UserRepository;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import ru.forum.whale.space.api.util.FileUtil;
import ru.forum.whale.space.api.enums.StorageBucket;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SessionUtilService sessionUtilService;
    private final MinioService minioService;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    private static final String FOLDER_PATTERN = "post-%d";

    private static final String postsBucket = StorageBucket.POST_FILES_BUCKET.getBucketName();

    @PostConstruct
    private void initPostsBucket() {
        minioService.initBucket(postsBucket);
    }

    public List<PostDto> findAll(Sort sort) {
        return sessionUtilService.findAuthenticatedUser()
                .map(currentUser -> postRepository.findAllByAuthorNot(currentUser, sort))
                .orElseGet(() -> postRepository.findAllBy(sort))
                .stream()
                .map(this::buildPostDto)
                .toList();
    }

    public PostWithCommentsDto findById(long id) {
        Post post = postRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пост с указанным ID не найден"));

        return buildPostWithCommentsDto(post);
    }

    public List<PostDto> findByUserId(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Пользователь с указанным ID не найден");
        }

        return postRepository.findAllByAuthorId(userId, Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(this::buildPostDto)
                .toList();
    }

    @Transactional
    public PostDto save(PostRequestDto postRequestDto, List<MultipartFile> files) {
        FileUtil.validateFiles(files);

        User author = sessionUtilService.findCurrentUser();

        Post post = Post.builder()
                .content(postRequestDto.getContent())
                .author(author)
                .build();

        Post postWithoutFiles = postRepository.save(post);

        List<String> fileNames = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            String folder = FOLDER_PATTERN.formatted(post.getId());
            fileNames = minioService.uploadImages(postsBucket, files, folder);
        }

        postWithoutFiles.setImageFileNames(fileNames);

        return convertToPostDto(postRepository.save(postWithoutFiles));
    }

    @Transactional
    public void deleteById(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пост с указанным ID не найден"));

        User currentUser = sessionUtilService.findCurrentUser();

        boolean isAdmin = Role.ADMIN.getRoleName().equals(currentUser.getRole());
        boolean isAuthor = currentUser.getId().equals(post.getAuthor().getId());

        if (isAdmin || isAuthor) {
            minioService.deleteFiles(postsBucket, post.getImageFileNames());
            postRepository.delete(post);
        } else {
            throw new CannotDeleteException("Вы не можете удалить данный пост");
        }
    }

    private PostDto convertToPostDto(Post post) {
        return postMapper.postToPostDto(post);
    }

    private PostWithCommentsDto convertToPostWithCommentsDto(Post post) {
        return postMapper.postToPostWithCommentsDto(post);
    }

    private PostDto buildPostDto(Post post) {
        PostDto postDto = convertToPostDto(post);

        postDto.setCommentCount(post.getComments().size());

        postDto.setLikedUserIds(post.getLikes().stream()
                .map(like -> like.getAuthor().getId())
                .toList()
        );

        return postDto;
    }

    private PostWithCommentsDto buildPostWithCommentsDto(Post post) {
        PostWithCommentsDto postWithCommentsDto = convertToPostWithCommentsDto(post);

        postWithCommentsDto.setComments(post.getComments().stream()
                .map(comment -> {
                    CommentDto commentDto = commentMapper.commentToCommentDto(comment);

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