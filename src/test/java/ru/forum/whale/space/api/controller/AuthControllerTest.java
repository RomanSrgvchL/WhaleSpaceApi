package ru.forum.whale.space.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.forum.whale.space.api.annotation.CustomWebMvcTest;
import ru.forum.whale.space.api.dto.request.UserAuthRequestDto;
import ru.forum.whale.space.api.dto.response.ResponseDto;
import ru.forum.whale.space.api.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.forum.whale.space.api.util.TestUtil.*;

@CustomWebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private static final String BASE_URL = "/auth";

    @WithMockUser
    @Test
    void checkAuth_thenReturnSuccessfulResponseDto() throws Exception {
        mockMvc.perform(get(BASE_URL + "/check"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Пользователь аутентифицирован"));
    }

    @Test
    void checkAuth_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        createUnauthorizedResponse(mockMvc.perform(get(BASE_URL + "/check")));
    }

    @Test
    void login_thenLoginUserAndReturnSuccessfulResponseDto() throws Exception {
        UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto(USERNAME, PASSWORD);

        ResponseDto response = ResponseDto.buildSuccess("Вход выполнен успешно!");

        when(authService.login(eq(userAuthRequestDto), any(HttpServletRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAuthRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(response.getMessage()));
    }

    @Test
    void register_thenRegisterUserAndReturnSuccessfulResponseDto() throws Exception {
        UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto(USERNAME, PASSWORD);

        ResponseDto response = ResponseDto.buildSuccess("Регистрация прошла успешно!");

        when(authService.register(userAuthRequestDto)).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAuthRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(response.getMessage()));
    }

    @ValueSource(strings = {"/login", "/register"})
    @ParameterizedTest
    void auth_whenUsernameTooLong_thenReturnBadRequest(String authMethod) throws Exception {
        UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto("x".repeat(21), PASSWORD);

        createBadRequestResponse(mockMvc.perform(post(BASE_URL + authMethod)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAuthRequestDto))));
    }

    @ValueSource(strings = {"/login", "/register"})
    @ParameterizedTest
    void auth_whenUsernameIsBlank_thenReturnBadRequest(String authMethod) throws Exception {
        UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto("        ", PASSWORD);

        createBadRequestResponse(mockMvc.perform(post(BASE_URL + authMethod)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAuthRequestDto))));
    }

    @ValueSource(strings = {"/login", "/register"})
    @ParameterizedTest
    void auth_whenUsernameContains_thenReturnBadRequest(String authMethod) throws Exception {
        UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto("user#", PASSWORD);

        createBadRequestResponse(mockMvc.perform(post(BASE_URL + authMethod)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAuthRequestDto))));
    }

    @ValueSource(strings = {"/login", "/register"})
    @ParameterizedTest
    void auth_whenPasswordTooLong_thenReturnBadRequest(String authMethod) throws Exception {
        UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto(USERNAME, "x".repeat(101));

        createBadRequestResponse(mockMvc.perform(post(BASE_URL + authMethod)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAuthRequestDto))));
    }

    @ValueSource(strings = {"/login", "/register"})
    @ParameterizedTest
    void auth_whenPasswordIsBlank_thenReturnBadRequest(String authMethod) throws Exception {
        UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto(USERNAME, "        ");

        createBadRequestResponse(mockMvc.perform(post(BASE_URL + authMethod)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAuthRequestDto))));
    }
}