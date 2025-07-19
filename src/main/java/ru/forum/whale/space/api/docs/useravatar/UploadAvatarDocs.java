package ru.forum.whale.space.api.docs.useravatar;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.dto.ChatMsgDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Загрузка аватара текущего пользователя",
        description = """
                    Файл должен быть формата PNG или JPG/JPEG.
                    Минимальное разрешение - 400x400 пикселей.
                    Максимальный размер файла - 3 МБ.
                """,
        requestBody = @RequestBody(
                content = @Content(
                        mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                        schema = @Schema(format = "binary", implementation = MultipartFile.class)
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "201",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = ChatMsgDto.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "fileName": "avatar-1"
                                                 }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = ResponseDto.class),
                                examples = {
                                        @ExampleObject(
                                                name = "Один из файлов не соответствует нужному формату",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Файлы должны быть формата PNG или JPG/JPEG"
                                                        }
                                                        """
                                        ),
                                        @ExampleObject(
                                                name = "Одно из изображений меньше 400x400 пикселей",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Минимальный размер изображения — 400x400 пикселей"
                                                        }
                                                        """
                                        ),
                                        @ExampleObject(
                                                name = "Один из файлов имеет неподдерживаемый формат",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Невалидный файл изображения"
                                                        }
                                                        """
                                        ),
                                        @ExampleObject(
                                                name = "Один из файлов повреждён или не может быть прочитан",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Файл повреждён или не может быть прочитан: ..."
                                                        }
                                                        """
                                        )
                                }
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = ResponseDto.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "success": false,
                                                    "message": "Пользователь не аутентифицирован"
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "413",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = ResponseDto.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "success": false,
                                                    "message": "Размер файла не должен превышать 3 МБ"
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = ResponseDto.class),
                                examples = {
                                        @ExampleObject(
                                                name = "Возникла ошибка при загрузке одного из файлов",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Не удалось загрузить файл 'some_file': ..."
                                                        }
                                                        """
                                        ),
                                        @ExampleObject(
                                                name = "Возникла неизвестная ошибка",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Неизвестная ошибка: ..."
                                                        }
                                                        """
                                        )
                                }
                        )
                )
        }
)
public @interface UploadAvatarDocs {
}
