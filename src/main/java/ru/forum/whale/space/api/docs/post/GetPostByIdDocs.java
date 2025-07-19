package ru.forum.whale.space.api.docs.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import ru.forum.whale.space.api.dto.PostWithCommentsDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Получение поста по ID",
        description = """
                    Доступ к посту имеют все аутентифицированные пользователи.
                    Список комментариев будет отсортирован от новых к старым.
                """,
        parameters = {
                @Parameter(
                        name = "id",
                        in = ParameterIn.PATH,
                        description = "ID поста (>0)",
                        required = true
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = PostWithCommentsDto.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                  "id": 3,
                                                  "author": {
                                                    "id": 1,
                                                    "username": "User1",
                                                    "avatarFileName": "avatar-1"
                                                  },
                                                  "content": "Мой первый пост!!",
                                                  "imageFileNames": [
                                                    "post-3/f95cb388-84b0-4a2d-9be9-f2aa814ae2d9"
                                                  ],
                                                  "createdAt": "2025-07-14T18:11:01.137276",
                                                  "comments": [
                                                    {
                                                      "id": 11,
                                                      "author": {
                                                        "id": 2,
                                                        "username": "User2",
                                                        "avatarFileName": "avatar-2"
                                                      },
                                                      "content": "Поздравляю!",
                                                      "createdAt": "2025-07-17T18:07:28.024566",
                                                      "likedUserIds": []
                                                    }
                                                  ],
                                                  "likedUserIds": []
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

public @interface GetPostByIdDocs {
}
