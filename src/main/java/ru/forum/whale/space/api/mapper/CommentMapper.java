package ru.forum.whale.space.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.forum.whale.space.api.dto.CommentDto;
import ru.forum.whale.space.api.model.Comment;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    CommentDto commentToCommentDto(Comment comment);
}
