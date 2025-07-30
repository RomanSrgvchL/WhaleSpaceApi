package ru.forum.whale.space.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.*;
import ru.forum.whale.space.api.util.Messages;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PostDto {
    @NotNull
    private Long id;

    @NotNull
    private UserLiteDto author;

    @NotBlank(message = Messages.POST_NOT_BLANK)
    @Size(max = 2000, message = Messages.POST_TOO_LONG)
    private String content;

    @Size(max = 3, message = Messages.POST_IMAGES_LIMIT)
    private List<String> imageFileNames;

    @NotNull
    private ZonedDateTime createdAt;

    private int commentCount;

    private List<Long> likedUserIds;
}
