package ru.forum.whale.space.api.docs.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import ru.forum.whale.space.api.dto.CommentDto;
import ru.forum.whale.space.api.dto.request.CommentRequestDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Создание комментария",
        requestBody = @RequestBody(
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = CommentRequestDto.class),
                        examples = {
                                @ExampleObject(
                                        name = "Валидный запрос",
                                        value = """
                                                {
                                                    "postId": 3,
                                                    "content": "Классный пост!"
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "Невалидный запрос - слишком длинный комментарий",
                                        description = "Плейсхолдер описывает данные, нарушающие правила валидации",
                                        value = """
                                                {
                                                    "postId": 3,
                                                    "content": "[>1000 символов]"
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "Невалидный запрос - пустой комментарий",
                                        value = """
                                                {
                                                    "postId": 3,
                                                    "content": ""
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "Невалидный запрос - указан неположительный ID",
                                        value = """
                                                {
                                                     "postId": -1,
                                                     "content": "Классный пост!"
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "Невалидный запрос - не указан ID поста",
                                        value = """
                                                {
                                                    "postId": null,
                                                    "content": "Классный пост!"
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "Невалидный запрос - не указан ID поста",
                                        value = """
                                                {
                                                    "postId": null,
                                                    "content": "Классный пост!"
                                                }
                                                """
                                )
                        }
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "201",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = CommentDto.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                   "id": 11,
                                                   "author": {
                                                     "id": 1,
                                                     "username": "User1",
                                                     "avatarFileName": "avatar-1"
                                                   },
                                                   "content": "Классный пост!",
                                                   "createdAt": "2025-07-17T18:07:28.024566",
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
                                examples = @ExampleObject(
                                        name = "Указан неположительный ID",
                                        value = """
                                                {
                                                    "success": false,
                                                    "message": "ID должен быть > 0"
                                                }
                                                """
                                )
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
                        responseCode = "404",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = ResponseDto.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "success": false,
                                                    "message": "Пост с указанным ID не найден"
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
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "success": false,
                                                    "message": "Неизвестная ошибка: ..."
                                                }
                                                """
                                )
                        )
                )
        }
)
public @interface CreateCommentDocs {
}
