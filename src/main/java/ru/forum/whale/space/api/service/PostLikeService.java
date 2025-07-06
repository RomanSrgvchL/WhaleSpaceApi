package ru.forum.whale.space.api.service;

import java.util.Optional;
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
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final SessionUtilService sessionUtilService;

    @Transactional
    public void likePost(Long postId) {
        Post post = findPostOrElseThrow(postId);

        User currentUser = sessionUtilService.findCurrentUser();
        Optional<PostLike> maybeLike = postLikeRepository.findByAuthorAndPost(currentUser, post);
        if (maybeLike.isPresent()) {
            throw new ResourceAlreadyExistsException("Вы уже ставили лайк на этот пост");
        }

        PostLike like = buildPostLike(currentUser, post);

        postLikeRepository.save(like);
    }

    private PostLike buildPostLike(User user, Post post) {
        PostLike like = new PostLike();
        like.setAuthor(user);
        like.setPost(post);
        return like;
    }

    @Transactional
    public void unlikePost(Long postId) {
        Post post = findPostOrElseThrow(postId);

        User currentUser = sessionUtilService.findCurrentUser();
        Optional<PostLike> maybeLike = postLikeRepository.findByAuthorAndPost(currentUser, post);
        if (maybeLike.isEmpty()) {
            return;
        }

        PostLike postLike = maybeLike.get();
        postLikeRepository.delete(postLike);
    }

    private Post findPostOrElseThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Пост с таким id не найден"));
    }
}
