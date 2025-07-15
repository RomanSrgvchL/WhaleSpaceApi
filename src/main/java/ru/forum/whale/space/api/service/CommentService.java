package ru.forum.whale.space.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final SessionUtilService sessionUtilService;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentDto save(CommentRequestDto commentRequestDto) {
        Post post = postRepository.findById(commentRequestDto.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Пост с указанным ID не найден"));

        User author = sessionUtilService.findCurrentUser();

        Comment comment = Comment.builder()
                .content(commentRequestDto.getContent())
                .author(author)
                .post(post)
                .build();

        return convertToCommentDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteById(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Комментарий с указанным ID не найден"));

        User currentUser = sessionUtilService.findCurrentUser();

        boolean isAdmin = Role.ADMIN.getRoleName().equals(currentUser.getRole());
        boolean isAuthor = currentUser.getId().equals(comment.getAuthor().getId());

        if (isAdmin || isAuthor) {
            commentRepository.deleteById(commentId);
        } else {
            throw new CannotDeleteException("Вы не можете удалить данный комментарий");
        }
    }

    private CommentDto convertToCommentDto(Comment comment) {
        return commentMapper.commentToCommentDto(comment);
    }
}
