package ru.forum.whale.space.api.docs.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import ru.forum.whale.space.api.dto.response.PageResponseDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Получение всех пользователей",
        description = "Доступно также неаутентифицированным пользователям",
        parameters = {
                @Parameter(
                        name = "sort",
                        description = "Поле для сортировки"
                ),
                @Parameter(
                        name = "order",
                        description = "Направление сортировки"
                ),
                @Parameter(
                        name = "page",
                        description = "Номер страницы (>=0)"
                ),
                @Parameter(
                        name = "size",
                        description = "Количество пользователей на странице (>0)"
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = PageResponseDto.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                  "content": [
                                                    {
                                                      "id": 3,
                                                      "username": "User1",
                                                      "createdAt": "2025-07-16T17:53:25.664317",
                                                      "role": "ROLE_USER",
                                                      "avatarFileName": null,
                                                      "birthDate": null,
                                                      "gender": null,
                                                      "bio": null
                                                    },
                                                    {
                                                      "id": 4,
                                                      "username": "User2",
                                                      "createdAt": "2025-07-15T19:56:09.205258",
                                                      "role": "ROLE_ADMIN",
                                                      "avatarFileName": null,
                                                      "birthDate": "2025-07-01",
                                                      "gender": "FEMALE",
                                                      "bio": "что-то о себе..."
                                                    }
                                                  ],
                                                  "page": 0,
                                                  "size": 2,
                                                  "totalPages": 2,
                                                  "totalElements": 4,
                                                  "last": false
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
public @interface GetAllUsersDocs {
}
