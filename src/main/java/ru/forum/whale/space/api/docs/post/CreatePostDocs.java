package ru.forum.whale.space.api.docs.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import ru.forum.whale.space.api.dto.PostDto;
import ru.forum.whale.space.api.dto.request.PostMultipartRequestDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Создание поста",
        description = """
                     Размер одного отдельного файла не должен превышать 3 МБ.
                     Размер всего поста (текст + файлы) не должен превышать 5 МБ.
                """,
        requestBody = @RequestBody(
                content = @Content(
                        mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                        schema = @Schema(implementation = PostMultipartRequestDto.class)
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "201",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = PostDto.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                  "id": 11,
                                                  "author": {
                                                    "id": 1,
                                                    "username": "User1",
                                                    "avatarFileName": "avatar-1"
                                                  },
                                                  "content": "Мой первый пост!",
                                                  "imageFileNames": [
                                                    "post-3/f95cb388-84b0-4a2d-9be9-f2aa814ae2d9"
                                                  ],
                                                  "createdAt": "2025-07-17T18:07:28.024566",
                                                  "commentCount": 0,
                                                  "likedUserIds": null
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
                                                name = "Прикреплено более 3 файлов к посту",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Можно прикрепить не более 3 файлов"
                                                        }
                                                        """
                                        ),
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
                                                description = "Одно из изображений меньше 150x150 пикселей",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Минимальный размер изображения — 150x150 пикселей"
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
                                        ),
                                        @ExampleObject(
                                                name = "Слишком длинный пост",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Длина поста не должна превышать 2000 символов"
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
public @interface CreatePostDocs {
}
