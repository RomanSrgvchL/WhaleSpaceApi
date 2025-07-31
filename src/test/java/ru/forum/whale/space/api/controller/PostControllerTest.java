package ru.forum.whale.space.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.forum.whale.space.api.annotation.CustomWebMvcTest;
import ru.forum.whale.space.api.dto.PostDto;
import ru.forum.whale.space.api.dto.PostWithCommentsDto;
import ru.forum.whale.space.api.dto.request.PostRequestDto;
import ru.forum.whale.space.api.enums.PostSortFields;
import ru.forum.whale.space.api.enums.SortOrder;
import ru.forum.whale.space.api.service.PostService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.forum.whale.space.api.util.TestUtil.*;

@CustomWebMvcTest(PostController.class)
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    private  static final String BASE_URL = "/posts";

    @Test
    void getAll_thenReturnPostDtoList() throws Exception {
        PostDto postDto = createPostDto();

        when(postService.findAll(Sort.by(SortOrder.DESC.getDirection(), PostSortFields.CREATED_AT.getFieldName())))
                .thenReturn(List.of(postDto));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(postDto.getId()));
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

    @WithMockUser
    @Test
    void getById_thenReturnPostDto() throws Exception {
        PostWithCommentsDto postWithCommentsDto = PostWithCommentsDto.builder()
                .id(POST_ID)
                .build();

        when(postService.findById(POST_ID)).thenReturn(postWithCommentsDto);

        mockMvc.perform(get(BASE_URL + "/{id}", POST_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(postWithCommentsDto.getId()));
    }

    @WithMockUser
    @ValueSource(longs = {-1L, 0L})
    @ParameterizedTest
    void getById_whenParamNonPositive_thenReturnBadRequest(long invalidPostId) throws Exception {
        createBadRequestResponse(mockMvc.perform(get(BASE_URL + "/{id}", invalidPostId)));
    }

    @Test
    void getById_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        createUnauthorizedResponse(mockMvc.perform(get(BASE_URL + "/{id}", POST_ID)));
    }

    @WithMockUser
    @Test
    void getByUserId_thenReturnPostDtoList() throws Exception {
        PostDto postDto = createPostDto();

        when(postService.findByUserId(POST_ID)).thenReturn(List.of(postDto));

        mockMvc.perform(get(BASE_URL + "/user/{userId}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(postDto.getId()));
    }

    @WithMockUser
    @ValueSource(longs = {-1L, 0L})
    @ParameterizedTest
    void getByUserId_whenParamNonPositive_thenReturnBadRequest(long invalidUserId) throws Exception {
        createBadRequestResponse(mockMvc.perform(get(BASE_URL + "/user/{userId}", invalidUserId)));
    }

    @Test
    void getByUserId_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        createUnauthorizedResponse(mockMvc.perform(get(BASE_URL + "/user/{userId}", USER_ID)));
    }

    @WithMockUser
    @Test
    void create_thenReturnCreatedPostDto() throws Exception {
        PostRequestDto postRequestDto = new PostRequestDto("new post");

        PostDto postDto = PostDto.builder()
                .content(postRequestDto.getContent())
                .build();

        when(postService.save(postRequestDto, null)).thenReturn(postDto);

        MockMultipartFile postPart = createPostMockMultipartFile(postRequestDto);

        mockMvc.perform(multipart(BASE_URL)
                        .file(postPart))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").value(postDto.getContent()));
    }

    @WithMockUser
    @Test
    void create_whenMessageContentTooLong_thenReturnBadRequest() throws Exception {
        PostRequestDto postRequestDto = new PostRequestDto("x".repeat(2001));

        MockMultipartFile postPart = createPostMockMultipartFile(postRequestDto);

        createBadRequestResponse(mockMvc.perform(multipart(BASE_URL)
                .file(postPart)));
    }

    @WithMockUser
    @Test
    void create_whenMessageContentIsBlank_thenReturnBadRequest() throws Exception {
        PostRequestDto postRequestDto = new PostRequestDto("   ");

        MockMultipartFile postPart = createPostMockMultipartFile(postRequestDto);

        createBadRequestResponse(mockMvc.perform(multipart(BASE_URL)
                .file(postPart)));
    }

    @Test
    void create_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        PostRequestDto postRequestDto = new PostRequestDto("new post");

        MockMultipartFile postPart = createPostMockMultipartFile(postRequestDto);

        createUnauthorizedResponse(mockMvc.perform(multipart(BASE_URL)
                .file(postPart)));
    }

    @WithMockUser
    @Test
    void deleteById_thenDeletePost() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", POST_ID))
                .andExpect(status().isNoContent());

        verify(postService).deleteById(POST_ID);
    }

    @WithMockUser
    @ValueSource(longs = {-1L, 0L})
    @ParameterizedTest
    void deleteById_whenParamNonPositive_thenReturnBadRequest(long invalidPostId) throws Exception {
        createBadRequestResponse(mockMvc.perform(delete(BASE_URL + "/{id}", invalidPostId)));
    }

    @Test
    void deleteById_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        createUnauthorizedResponse(mockMvc.perform(delete(BASE_URL + "/{id}", POST_ID)));
    }

    private PostDto createPostDto() {
        return PostDto.builder()
                .id(POST_ID)
                .build();
    }

    private MockMultipartFile createPostMockMultipartFile(PostRequestDto postRequestDto)
            throws JsonProcessingException {
        return new MockMultipartFile(
                "post",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(postRequestDto)
        );
    }
}