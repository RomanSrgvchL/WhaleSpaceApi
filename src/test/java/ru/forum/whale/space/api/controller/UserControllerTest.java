package ru.forum.whale.space.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.forum.whale.space.api.annotation.CustomWebMvcTest;
import ru.forum.whale.space.api.dto.UserDto;
import ru.forum.whale.space.api.dto.UserProfileDto;
import ru.forum.whale.space.api.dto.response.PageResponseDto;
import ru.forum.whale.space.api.enums.SortOrder;
import ru.forum.whale.space.api.enums.UserSortFields;
import ru.forum.whale.space.api.model.Gender;
import ru.forum.whale.space.api.service.UserService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.forum.whale.space.api.util.TestUtil.*;

@CustomWebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private static final String BASE_URL = "/users";

    @Test
    void getAll_thenReturnUserDtoPage() throws Exception {
        int page = 0;
        int size = 6;

        UserDto userDto = createUserDto();

        List<UserDto> users = List.of(userDto);

        Page<UserDto> usersPage = new PageImpl<>(users, PageRequest.of(page, size), users.size());

        PageResponseDto<UserDto> pageResponseDto = PageResponseDto.<UserDto>builder()
                .content(usersPage.getContent())
                .build();

        when(userService.findAll(Sort.by(SortOrder.DESC.getDirection(), UserSortFields.CREATED_AT.getFieldName()),
                page, size)).thenReturn(pageResponseDto);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(userDto.getId()));
    }

    @Test
    void getAll_whenInvalidSortParam_thenReturnBadRequest() throws Exception {
        createBadRequestResponse(mockMvc.perform(get(BASE_URL)
                .param("sort", INVALID)));
    }

    @Test
    void getAll_whenInvalidOrderParam_thenReturnBadRequest() throws Exception {
        createBadRequestResponse(mockMvc.perform(get(BASE_URL)
                .param("order", INVALID)));
    }

    @Test
    void getAll_whenPageNegative_thenReturnBadRequest() throws Exception {
        long invalidPage = -1L;

        createBadRequestResponse(mockMvc.perform(get(BASE_URL)
                .param("page", String.valueOf(invalidPage))));
    }

    @ValueSource(longs = {-1L, 0L})
    @ParameterizedTest
    void getAll_whenSizeNonPositive_thenReturnBadRequest(long invalidSize) throws Exception {
        createBadRequestResponse(mockMvc.perform(get(BASE_URL)
                .param("size", String.valueOf(invalidSize))));
    }

    @WithMockUser
    @Test
    void getMe_thenReturnCurrentUser() throws Exception {
        UserDto userDto = createUserDto();

        when(userService.findYourself()).thenReturn(userDto);

        mockMvc.perform(get(BASE_URL + "/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDto.getId()));
    }

    @Test
    void getMe_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        createUnauthorizedResponse(mockMvc.perform(get(BASE_URL + "/me")));
    }


    @WithMockUser
    @Test
    void getById_thenReturnUserDto() throws Exception {
        UserDto userDto = createUserDto();

        when(userService.findById(USER_ID)).thenReturn(userDto);

        mockMvc.perform(get(BASE_URL + "/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDto.getId()));
    }

    @WithMockUser
    @ValueSource(longs = {-1L, 0L})
    @ParameterizedTest
    void getById_whenParamNonPositive_thenReturnBadRequest(long invalidUserId) throws Exception {
        createBadRequestResponse(mockMvc.perform(get(BASE_URL + "/{id}", invalidUserId)));
    }

    @Test
    void getById_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        createUnauthorizedResponse(mockMvc.perform(get(BASE_URL + "/{id}", USER_ID)));
    }

    @WithMockUser
    @Test
    void getByUsername_thenReturnUserDto() throws Exception {
        UserDto userDto = UserDto.builder()
                .username(USERNAME)
                .build();

        when(userService.findByUsername(USERNAME)).thenReturn(userDto);

        mockMvc.perform(get(BASE_URL + "/username/{username}", USERNAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(userDto.getUsername()));
    }

    @Test
    void getByUsername_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        createUnauthorizedResponse(mockMvc.perform(get(BASE_URL + "/username/{username}", USER_ID)));
    }

    @WithMockUser
    @Test
    void update_thenReturnUserProfileDto() throws Exception {
        UserProfileDto userProfileDto = createUserProfileDto();

        when(userService.update(userProfileDto)).thenReturn(userProfileDto);

        mockMvc.perform(patch(BASE_URL + "/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userProfileDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.gender").value(userProfileDto.getGender().toString()));
    }

    @WithMockUser
    @Test
    void update_whenInvalidGender_thenReturnBadRequest() throws Exception {
        String invalidJson = """
        {
            "gender": "INVALID"
        }
        """;

        createBadRequestResponse(mockMvc.perform(patch(BASE_URL + "/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)));
    }


    @WithMockUser
    @Test
    void update_whenBioTooLong_thenReturnBadRequest() throws Exception {
        UserProfileDto userProfileDto = UserProfileDto.builder()
                .bio("x".repeat(121))
                .build();

        createBadRequestResponse(mockMvc.perform(patch(BASE_URL + "/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userProfileDto))));
    }

    @Test
    void update_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        UserProfileDto userProfileDto = createUserProfileDto();

        createUnauthorizedResponse(mockMvc.perform(patch(BASE_URL + "/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userProfileDto))));
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(USER_ID)
                .build();
    }

    private UserProfileDto createUserProfileDto() {
        return UserProfileDto.builder()
                .gender(Gender.MALE)
                .build();
    }
}