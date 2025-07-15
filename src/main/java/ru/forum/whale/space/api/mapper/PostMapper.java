package ru.forum.whale.space.api.mapper;

import org.mapstruct.Mapper;
import ru.forum.whale.space.api.dto.PostDto;
import ru.forum.whale.space.api.dto.PostWithCommentsDto;
import ru.forum.whale.space.api.model.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostDto postToPostDto(Post post);

    PostWithCommentsDto postToPostWithCommentsDto(Post post);
}
