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
        description = "Multipart-форма создания сообщения",
        requiredProperties = "message"
)
public class MessageMultipartRequestDto {
    @Schema(
            name = "message",
            description = "Текст сообщения, не более 200 символов, непустой",
            implementation = MessageRequestDto.class
    )
    private MessageRequestDto message;

    @Schema(
            name = "files",
            description = "Прикреплённые файлы (до 3 штук, необязательно)",
            type = "array",
            implementation = MultipartFile.class
    )
    private List<MultipartFile> files;
}
