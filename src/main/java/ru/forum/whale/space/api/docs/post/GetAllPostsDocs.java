package ru.forum.whale.space.api.docs.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import ru.forum.whale.space.api.dto.PostDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Получение всех постов",
        description = """
                    Доступно в том числе неаутентифицированным пользователям.
                    Возвращает список постов без комментариев, но с их количеством.
                    Итоговый список постов не включает посты текущего пользователя.
                """,
        parameters = {
                @Parameter(
                        name = "sort",
                        description = "Поле для сортировки"
                ),
                @Parameter(
                        name = "order",
                        description = "Направление сортировки"
                ),
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                array = @ArraySchema(schema = @Schema(implementation = PostDto.class)),
                                examples = @ExampleObject(
                                        value = """
                                                [
                                                  {
                                                    "id": 4,
                                                    "author": {
                                                      "id": 2,
                                                      "username": "User1",
                                                      "avatarFileName": "avatar-1"
                                                    },
                                                    "content": "Мой первый пост!!",
                                                    "imageFileNames": [],
                                                    "createdAt": "2025-07-15T18:16:05.702593",
                                                    "commentCount": 2,
                                                    "likedUserIds": [
                                                      1
                                                    ]
                                                  }
                                                ]
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
public @interface GetAllPostsDocs {
}
