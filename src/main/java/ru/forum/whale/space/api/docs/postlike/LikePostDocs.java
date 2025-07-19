package ru.forum.whale.space.api.docs.postlike;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import ru.forum.whale.space.api.dto.response.ResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Лайк поста по его ID",
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
                        description = "Успешно, без содержимого",
                        responseCode = "204"
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
                        responseCode = "409",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = ResponseDto.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "success": false,
                                                    "message": "Вы уже ставили лайк на этот пост"
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
public @interface LikePostDocs {
}
