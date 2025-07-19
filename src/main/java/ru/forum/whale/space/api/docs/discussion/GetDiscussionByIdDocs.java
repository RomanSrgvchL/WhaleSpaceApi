package ru.forum.whale.space.api.docs.discussion;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import ru.forum.whale.space.api.dto.DiscussionDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Получение обсуждения по ID",
        description = """
                    Доступ к обсуждению имеют все аутентифицированные пользователи.
                    Список сообщений будет отсортирован от новых к старым.
                """,
        parameters = {
                @Parameter(
                        name = "id",
                        in = ParameterIn.PATH,
                        description = "ID обсуждения (>0)",
                        required = true
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = DiscussionDto.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                  "id": 7,
                                                  "title": "Идеи для улучшения",
                                                  "creator": {
                                                    "id": 1,
                                                    "username": "User1",
                                                    "avatarFileName": "avatar-1"
                                                  },
                                                  "createdAt": "2025-07-18T15:02:08.705626",
                                                  "messages": [
                                                    {
                                                      "id": 15,
                                                      "sender": {
                                                        "id": 1,
                                                        "username": "User1",
                                                        "avatarFileName": "avatar-1"
                                                      },
                                                      "content": "Предлагаю добавить следующий функционал:",
                                                      "imageFileNames": [
                                                        "discussion-7/a4ed7568-c8ba-4d7f-8dfa-07ec78c082b9"
                                                      ],
                                                      "createdAt": "2025-07-18T15:02:38.16436"
                                                    }
                                                  ]
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
                                                    "message": "Обсуждение с указанным ID не найдено"
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
public @interface GetDiscussionByIdDocs {
}
