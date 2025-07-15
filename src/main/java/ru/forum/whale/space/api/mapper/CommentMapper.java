package ru.forum.whale.space.api.mapper;

import org.mapstruct.Mapper;
import ru.forum.whale.space.api.dto.CommentDto;
import ru.forum.whale.space.api.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentDto commentToCommentDto(Comment comment);
}
