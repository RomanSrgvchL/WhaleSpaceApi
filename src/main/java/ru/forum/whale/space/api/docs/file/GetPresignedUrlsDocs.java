package ru.forum.whale.space.api.docs.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
        summary = "Получение временной ссылки на файлы",
        description = """
                    Доступно в том числе неаутентифицированным пользователям.
                    Ссылки действует в течении 12 часов.
                """,
        parameters = {
                @Parameter(
                        name = "fileName",
                        in = ParameterIn.PATH,
                        description = "Список имён файлов, для которых надо надо сгенерировать временную ссылку",
                        required = true
                ),
                @Parameter(
                        name = "bucketName",
                        in = ParameterIn.PATH,
                        description = "Имя бакета (категории), в котором хранятся файлы",
                        required = true
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                examples = @ExampleObject(
                                        name = "Ссылки успешно сгенерированы",
                                        description = "Плейсхолдеры описывают список URL-адресов файлов",
                                        value = """
                                                [
                                                  "some_url_1",
                                                  "some_url_2"
                                                ]
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
                                                name = "Возникла ошибка при генерации ссылки на один из файлов",
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
public @interface GetPresignedUrlsDocs {
}
