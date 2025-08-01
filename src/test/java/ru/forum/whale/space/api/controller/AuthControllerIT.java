package ru.forum.whale.space.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import ru.forum.whale.space.api.IntegrationTestBase;
import ru.forum.whale.space.api.dto.request.UserAuthRequestDto;
import ru.forum.whale.space.api.handler.AuthExceptionHandler;
import ru.forum.whale.space.api.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.forum.whale.space.api.util.TestUtil.*;

@AutoConfigureMockMvc
@SpringBootTest
class AuthControllerIT extends IntegrationTestBase {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String BASE_URL = "/auth";

    @Nested
    class LoginTest {
        @BeforeEach
        void setUp() {
            createAndSaveUser(userRepository, passwordEncoder);
        }

        @Test
        void login_thenLoginUserAndReturnSuccessfulResponseDto() throws Exception {
            UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto(USERNAME, PASSWORD);

            mockMvc.perform(post(BASE_URL + "/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userAuthRequestDto)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Вход выполнен успешно!"));
        }

        @Test
        void login_whenInvalidUsernameOrPassword_thenReturnUnauthorized() throws Exception {
            UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto(INVALID, INVALID);

            mockMvc.perform(post(BASE_URL + "/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userAuthRequestDto)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(AuthExceptionHandler.BAD_CREDENTIALS));
        }
    }

    @Nested
    class RegisterTest {
        @Test
        void register_thenRegisterUserAndReturnSuccessfulResponseDto() throws Exception {
            UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto(USERNAME, PASSWORD);

            mockMvc.perform(post(BASE_URL + "/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userAuthRequestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Регистрация прошла успешно!"));
        }
    }
}
