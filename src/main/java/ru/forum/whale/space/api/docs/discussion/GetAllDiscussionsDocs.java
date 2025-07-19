package ru.forum.whale.space.api.docs.discussion;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import ru.forum.whale.space.api.dto.DiscussionMetaDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Получение всех обсуждений",
        description = """
                    Доступно в том числе неаутентифицированным пользователям.
                    Возвращает список обсуждений без сообщений.
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
                                array = @ArraySchema(schema = @Schema(implementation = DiscussionMetaDto.class)),
                                examples = @ExampleObject(
                                        value = """
                                                [
                                                    {
                                                        "id": 3,
                                                        "title": "Что думаете о проекте?",
                                                        "creator": {
                                                            "id": 1,
                                                            "username": "User1",
                                                            "avatarFileName": "avatar-1"
                                                        },
                                                        "createdAt": "2025-07-15T17:47:31.841956"
                                                    },
                                                    {
                                                        "id": 2,
                                                        "title": "Идеи для улучшения",
                                                        "creator": {
                                                            "id": 2,
                                                            "username": "User2",
                                                            "avatarFileName": "avatar-2"
                                                        },
                                                        "createdAt": "2025-07-14T18:11:24.356385"
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
public @interface GetAllDiscussionsDocs {
}
