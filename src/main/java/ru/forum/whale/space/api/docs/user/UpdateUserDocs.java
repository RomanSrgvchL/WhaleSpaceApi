package ru.forum.whale.space.api.docs.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import ru.forum.whale.space.api.dto.UserProfileDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Обновление данных текущего пользователя",
        requestBody = @RequestBody(
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = UserProfileDto.class),
                        examples = {
                                @ExampleObject(
                                        name = "Валидный запрос",
                                        value = """
                                                {
                                                  "birthDate": "2025-07-19",
                                                  "gender": "MALE",
                                                  "bio": "что-то о себе..."
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "Невалидный запрос - слишком короткое или длинное название",
                                        description = "Плейсхолдер описывает данные, нарушающие правила валидации",
                                        value = """
                                                {
                                                  "birthDate": "2025-07-19",
                                                  "gender": "MALE",
                                                  "bio": "[>120 символов]"
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "Невалидный запрос - указанный пол не из допустимого спсика  ",
                                        value = """
                                                {
                                                  "birthDate": "2025-07-19",
                                                  "gender": "AGENDER",
                                                  "bio": "что-то о себе..."
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
                                schema = @Schema(implementation = UserProfileDto.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                  "birthDate": "2025-07-19",
                                                  "gender": "MALE",
                                                  "bio": "что-то о себе..."
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
                                                    "message": "Био не должен содержать более 120 символов"
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
public @interface UpdateUserDocs {
}
