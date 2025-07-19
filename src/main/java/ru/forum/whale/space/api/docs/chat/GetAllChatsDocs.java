package ru.forum.whale.space.api.docs.chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import ru.forum.whale.space.api.dto.ChatWithLastMsgDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Получение всех чатов текущего пользователя",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                array = @ArraySchema(schema = @Schema(implementation = ChatWithLastMsgDto.class)),
                                examples = @ExampleObject(
                                        value = """
                                                [
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
                                                      "lastMessage": {
                                                          "id": 10,
                                                          "sender": {
                                                              "id": 1,
                                                              "username": "User1",
                                                              "avatarFileName": "avatar-1"
                                                          },
                                                          "content": "Привет!",
                                                          "imageFileNames": [],
                                                          "createdAt": "2025-07-15T23:12:49.09383"
                                                      }
                                                  }
                                                ]
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
public @interface GetAllChatsDocs {
}
