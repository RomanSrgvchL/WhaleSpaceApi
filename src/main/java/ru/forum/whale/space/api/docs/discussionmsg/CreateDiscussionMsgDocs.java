package ru.forum.whale.space.api.docs.discussionmsg;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import ru.forum.whale.space.api.dto.ChatMsgDto;
import ru.forum.whale.space.api.dto.request.MessageMultipartRequestDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Создание сообщения в обсуждении и рассылка его по WebSocket участникам обсуждения",
        description = """
                    Отправлять сообщение в обсуждение может любой авторизованный пользователь.
                    Размер одного отдельного файла не должен превышать 3 МБ.
                    Размер всего сообщения (текст + файлы) не должен превышать 5 МБ.
                """,
        parameters = {
                @Parameter(
                        name = "discussionId",
                        in = ParameterIn.PATH,
                        description = "ID обсуждения (>0)",
                        required = true
                ),
        },
        requestBody = @RequestBody(
                content = @Content(
                        mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                        schema = @Schema(implementation = MessageMultipartRequestDto.class)
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "201",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = ChatMsgDto.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                     "id": 1,
                                                     "sender": {
                                                         "id": 1,
                                                         "username": "User1",
                                                         "avatarFileName": "avatar-1"
                                                     },
                                                     "content": "Привет!",
                                                     "imageFileNames": [],
                                                     "createdAt": "2025-07-17T13:07:50.612119"
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
                                                name = "Прикреплено более 3 файлов к сообщению",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Можно прикрепить не более 3 файлов"
                                                        }
                                                        """
                                        ),
                                        @ExampleObject(
                                                name = "Один из файлов не соответствует нужному формату",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Файлы должны быть формата PNG или JPG/JPEG"
                                                        }
                                                        """
                                        ),
                                        @ExampleObject(
                                                name = "Одно из изображений меньше 150x150 пикселей",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Минимальный размер изображения — 150x150 пикселей"
                                                        }
                                                        """
                                        ),
                                        @ExampleObject(
                                                name = "Один из файлов имеет неподдерживаемый формат",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Невалидный файл изображения"
                                                        }
                                                        """
                                        ),
                                        @ExampleObject(
                                                name = "Один из файлов повреждён или не может быть прочитан",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Файл повреждён или не может быть прочитан: ..."
                                                        }
                                                        """
                                        ),
                                        @ExampleObject(
                                                name = "Слишком длинное сообщение",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Длина сообщения не должна превышать 200 символов"
                                                        }
                                                        """
                                        )
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
                                                    "message": "Обсуждение с указанным ID не найдено"
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "413",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = ResponseDto.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "success": false,
                                                    "message": "Размер файла не должен превышать 3 МБ"
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
                                                name = "Возникла ошибка при загрузке одного из файлов",
                                                value = """
                                                        {
                                                            "success": false,
                                                            "message": "Не удалось загрузить файл 'some_file': ..."
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
public @interface CreateDiscussionMsgDocs {
}
