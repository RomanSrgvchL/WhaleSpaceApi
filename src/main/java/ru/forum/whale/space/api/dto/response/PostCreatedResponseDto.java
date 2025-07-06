package ru.forum.whale.space.api.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostCreatedResponseDto extends ResponseDto {
    private long id;
    private String content;
    private long authorId;
}
