package ru.forum.whale.space.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "Multipart-форма создания поста",
        requiredProperties = "post"
)
public class PostMultipartRequestDto {
    @Schema(
            name = "post",
            description = "Текст поста, не более 2000 символов, непустой",
            implementation = PostRequestDto.class
    )
    private PostRequestDto post;

    @Schema(
            name = "files",
            description = "Прикреплённые файлы (до 3 штук, необязательно)",
            type = "array",
            implementation = MultipartFile.class
    )
    private List<MultipartFile> files;
}
