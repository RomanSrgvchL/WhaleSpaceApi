package ru.forum.whale.space.api.docs.chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import ru.forum.whale.space.api.dto.ChatDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Получение чата по ID собеседника",
        description = "Список сообщений будет отсортирован от новых к старым",
        parameters = {
                @Parameter(
                        name = "id",
                        in = ParameterIn.PATH,
                        description = "ID собеседника (>0)",
                        required = true
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = ChatDto.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "id": 1,
                                                    "user1": {
                                                        "id": 1,
                                                        "username": "User1",
                                                        "avatarFileName": "avatar-1"
                                                    },
                                                    "user2": {
                                                        "id": 2,
                                                        "username": "User2",
                                                        "avatarFileName": "avatar-2"
                                                    },
                                                    "createdAt": "2025-07-14T18:03:48.325541",
                                                    "messages": [
                                                        {
                                                            "id": 1,
                                                            "sender": {
                                                                "id": 2,
                                                                "username": "User2",
                                                                "avatarFileName": "avatar-2"
                                                            },
                                                            "content": "Привет!",
                                                            "imageFileNames": [],
                                                            "createdAt": "2025-07-14T18:03:49.673638"
                                                        },
                                                        {
                                                            "id": 2,
                                                            "sender": {
                                                                "id": 2,
                                                                "username": "User2",
                                                                "avatarFileName": "avatar-2"
                                                            },
                                                            "content": "Смотри какой вид из окна",
                                                            "imageFileNames": [
                                                                "chat-1/1d8090c5-5c3c-4b80-a6b8-246ee1758cdf",
                                                                "chat-1/34a19f08-4845-467d-84f0-fdc41f3ca387"
                                                            ],
                                                            "createdAt": "2025-07-14T18:03:50.294742"
                                                        }
                                                    ]
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
                                        value = """
                                                {
                                                    "success": false,
                                                    "message": "Нельзя получить чат с самим собой"
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
                                examples = {
                                        @ExampleObject(
                                                name = "Указан несуществующий ID пользователя",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Пользователь с указанным ID не найден"
                                                        }
                                                        """
                                        ),
                                        @ExampleObject(
                                                name = "Указан несуществующий ID чата",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Чат с указанным пользователем не найден"
                                                        }
                                                        """
                                        )
                                }
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
public @interface GetChatWithUserDocs {
}
