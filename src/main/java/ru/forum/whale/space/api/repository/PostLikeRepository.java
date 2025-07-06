package ru.forum.whale.space.api.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.forum.whale.space.api.model.Post;
import ru.forum.whale.space.api.model.PostLike;
import ru.forum.whale.space.api.model.User;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByAuthorAndPost(User user, Post post);
}