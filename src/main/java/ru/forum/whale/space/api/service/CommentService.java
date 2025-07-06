package ru.forum.whale.space.api.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.forum.whale.space.api.dto.request.CommentCreateRequestDto;
import ru.forum.whale.space.api.dto.response.CommentCreatedResponseDto;
import ru.forum.whale.space.api.exception.CannotDeleteException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.model.Comment;
import ru.forum.whale.space.api.model.Post;
import ru.forum.whale.space.api.model.Role;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.CommentRepository;
import ru.forum.whale.space.api.repository.PostRepository;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final SessionUtilService sessionUtilService;
    private final ModelMapper mapper;

    @Transactional
    public CommentCreatedResponseDto createComment(CommentCreateRequestDto commentCreateRequestDto) {
        Post post = postRepository.findById(commentCreateRequestDto.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Пост не найден"));

        User author = sessionUtilService.findCurrentUser();

        //отдельный метод без modelMapper т.к. он почему-то берет айди поста как айди коммента
        Comment comment = convertDtoToEntity(commentCreateRequestDto);
        System.out.println(comment.getId());
        comment.setAuthor(author);
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);
        return mapper.map(savedComment, CommentCreatedResponseDto.class);
    }

    private Comment convertDtoToEntity(CommentCreateRequestDto commentCreateRequestDto) {
        Comment comment = new Comment();
        comment.setContent(commentCreateRequestDto.getContent());
        return comment;
    }

    @Transactional
    public void deleteComment(Long commentId) {
        User currentUser = sessionUtilService.findCurrentUser();

        Optional<Comment> maybeComment = commentRepository.findById(commentId);

        if (maybeComment.isEmpty()) {
            return;
        }

        Comment comment = maybeComment.get();
        System.out.println("CurrentUser ID: " + currentUser.getId());
        System.out.println("Comment Author ID: " + comment.getAuthor().getId());
        boolean isAdmin = Role.ADMIN.getPrefixRole().equals(currentUser.getRole());
        boolean isAuthor = currentUser.getId().equals(comment.getAuthor().getId());

        if (isAdmin || isAuthor) {
            commentRepository.deleteById(commentId);
        } else {
            throw new CannotDeleteException("Вы не можете удалить данный комментарий");
        }
    }
}
