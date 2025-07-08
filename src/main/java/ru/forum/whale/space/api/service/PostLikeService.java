package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.Post;
import ru.forum.whale.space.api.model.PostLike;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.PostLikeRepository;
import ru.forum.whale.space.api.repository.PostRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final SessionUtilService sessionUtilService;

    @Transactional
    public void like(long postId) {
        Post post = findPostOrElseThrow(postId);
        User currentUser = sessionUtilService.findCurrentUser();

        if (postLikeRepository.findByAuthorAndPost(currentUser, post).isPresent()) {
            throw new ResourceAlreadyExistsException("Вы уже ставили лайк на этот пост");
        }

        PostLike like = PostLike.builder()
                .author(currentUser)
                .post(post)
                .build();

        postLikeRepository.save(like);
    }

    @Transactional
    public void unlike(long postId) {
        Post post = findPostOrElseThrow(postId);

        User currentUser = sessionUtilService.findCurrentUser();

        PostLike postLike = postLikeRepository.findByAuthorAndPost(currentUser, post)
                .orElseThrow(() -> new ResourceNotFoundException("Лайк на посте с указанным ID не найден"));

        postLikeRepository.delete(postLike);
    }

    private Post findPostOrElseThrow(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Пост с указанным ID не найден"));
    }
}
