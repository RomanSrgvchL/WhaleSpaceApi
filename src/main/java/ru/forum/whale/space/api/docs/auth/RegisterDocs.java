package ru.forum.whale.space.api.docs.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import ru.forum.whale.space.api.dto.request.UserAuthRequestDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Регистрация",
        requestBody = @RequestBody(
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = UserAuthRequestDto.class),
                        examples = {
                                @ExampleObject(
                                        name = "Валидный запрос",
                                        value = """
                                                {
                                                    "username": "username123",
                                                    "password": "admin123"
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "Невалидный запрос - слишком длинные имя пользователя и пароль",
                                        description = "Плейсхолдеры описывают данные, нарушающие правила валидации",
                                        value = """
                                                {
                                                    "username": "[>20 символов]",
                                                    "password": "[>100 символов]"
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "Невалидный запрос - пустые пользователя и пароль",
                                        value = """
                                                {
                                                    "username": "       ",
                                                    "password": "       "
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "Невалидный запрос - имя содержит запрещённые символы",
                                        value = """
                                                {
                                                    "username": "#username&123;",
                                                    "password": "admin123"
                                                }
                                                """
                                )
                        }
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = ResponseDto.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                  "success": true,
                                                  "message": "Регистрация прошла успешно!"
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
                                                    "message": "Имя пользователя не должно содержать более 20 символов"
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
                                                    "message": "Это имя уже занято"
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
public @interface RegisterDocs {
}
