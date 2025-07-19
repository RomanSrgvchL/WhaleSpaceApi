package ru.forum.whale.space.api.docs.chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import ru.forum.whale.space.api.dto.ChatDto;
import ru.forum.whale.space.api.dto.request.ChatRequestDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Создание чата",
        requestBody = @RequestBody(
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ChatRequestDto.class),
                        examples = {
                                @ExampleObject(
                                        name = "Валидный запрос",
                                        value = """
                                                {
                                                    "partnerId": 3
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "Невалидный запрос - указан неположительный ID",
                                        value = """
                                                {
                                                     "partnerId": -1
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "Невалидный запрос - не указан ID собеседника",
                                        value = """
                                                {
                                                    "partnerId": null
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
                                schema = @Schema(implementation = ChatDto.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "id": 3,
                                                    "user1": {
                                                        "id": 1,
                                                        "username": "User1",
                                                        "avatarFileName": "avatar-1"
                                                    },
                                                    "user2": {
                                                        "id": 2,
                                                        "username": "User2",
                                                        "avatarFileName": null
                                                    },
                                                    "createdAt": "2025-07-16T17:55:04.563225",
                                                    "messages": null
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
                                                name = "Указан неположительный ID",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "ID должен быть > 0"
                                                        }
                                                        """
                                        ),
                                        @ExampleObject(
                                                name = "Указан ID текущего пользователя",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Нельзя создать чат с самим собой"
                                                        }
                                                        """
                                        ),
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
                        responseCode = "404",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = ResponseDto.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "success": false,
                                                    "message": "Пользователь с указанным ID не найден"
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "409",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = ResponseDto.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "success": false,
                                                    "message": "Чат с указанным пользователем уже существует"
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
public @interface CreateChatDocs {
}
