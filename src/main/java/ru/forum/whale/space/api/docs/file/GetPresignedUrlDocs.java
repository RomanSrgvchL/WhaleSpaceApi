package ru.forum.whale.space.api.docs.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import ru.forum.whale.space.api.dto.response.ResponseDto;
import ru.forum.whale.space.api.dto.response.UrlResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Получение временной ссылки на файл",
        description = """
                    Доступно в том числе неаутентифицированным пользователям.
                    Ссылка действует в течении 12 часов.
                """,
        parameters = {
                @Parameter(
                        name = "fileName",
                        in = ParameterIn.PATH,
                        description = "Имя файла, для которого надо надо сгенерировать временную ссылку",
                        required = true
                ),
                @Parameter(
                        name = "bucketName",
                        in = ParameterIn.PATH,
                        description = "Имя бакета (категории), в котором хранится файл",
                        required = true
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = UrlResponseDto.class),
                                examples = @ExampleObject(
                                        name = "Ссылка успешно сгенерирована",
                                        description = "Плейсхолдер описывает URL-адрес файла",
                                        value = """
                                                {
                                                  "url": "some_url"
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
                                                    "message": "Файл 'some_file' не найден"
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
                                examples = {
                                        @ExampleObject(
                                                name = "Возникла ошибка при генерации ссылки",
                                                value = """
                                                {
                                                    "success": false,
                                                    "message": "Ошибка при генерации временной ссылки на файл 'file_name': ..."
                                                }
                                                """
                                        ),
                                        @ExampleObject(
                                                name = "Возникла неизвестная ошибка",
                                                value = """
                                                {
                                                    "success": false,
                                                    "message": "Неизвестная ошибка: ..."
                                                }
                                                """
                                        )
                                }
                        )
                )
        }
)
public @interface GetPresignedUrlDocs {
}
