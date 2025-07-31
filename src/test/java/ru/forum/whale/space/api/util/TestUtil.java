package ru.forum.whale.space.api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.handler.AccessDeniedExceptionHandler;
import ru.forum.whale.space.api.handler.AuthExceptionHandler;
import ru.forum.whale.space.api.model.Chat;
import ru.forum.whale.space.api.model.User;

import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestUtil {
    public static final long USER_ID = 1L;
    public static final long CURRENT_USER_ID = 1L;
    public static final long PARTNER_ID = 2L;
    public static final long POST_ID = 1L;
    public static final long COMMENT_ID = 1L;
    public static final long CHAT_ID = 1L;
    public static final long DISCUSSION_ID = 1L;

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String FILENAME = "file";
    public static final String INVALID = "invalid";

    public static MultipartFile createMockMultipartFile(String contentType) {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn(contentType);
        return file;
    }

    public static List<MultipartFile> createMockFiles(int count) {
        return IntStream.range(0, count)
                .mapToObj(num -> mock(MultipartFile.class))
                .toList();
    }

    public static User createUser(long userId) {
        return User.builder()
                .id(userId)
                .build();
    }

    public static User createUser(long userId, String role) {
        return User.builder()
                .id(userId)
                .role(role)
                .build();
    }

    public static Chat createChat(User user1, User user2) {
        return Chat.builder()
                .user1(user1)
                .user2(user2)
                .build();
    }

    public static void createUnauthorizedResponse(ResultActions result) throws Exception {
        result.andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(AuthExceptionHandler.UNAUTHORIZED));
    }

    public static void createForbiddenResponse(ResultActions result) throws Exception {
        result.andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(AccessDeniedExceptionHandler.FORBIDDEN));
    }

    public static void createBadRequestResponse(ResultActions result) throws Exception {
        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false));
    }
}
