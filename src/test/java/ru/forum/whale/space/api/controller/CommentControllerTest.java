package ru.forum.whale.space.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.forum.whale.space.api.annotation.CustomWebMvcTest;
import ru.forum.whale.space.api.dto.CommentDto;
import ru.forum.whale.space.api.dto.request.CommentRequestDto;
import ru.forum.whale.space.api.service.CommentService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.forum.whale.space.api.util.TestUtil.*;

@CustomWebMvcTest(CommentController.class)
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    private static final String BASE_URL = "/comments";

    @WithMockUser
    @Test
    void create_thenReturnCreatedCommentDto() throws Exception {
        CommentRequestDto commentRequestDto = createCommentRequestDto(POST_ID);

        CommentDto commentDto = CommentDto.builder()
                .id(COMMENT_ID)
                .build();

        when(commentService.save(commentRequestDto)).thenReturn(commentDto);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(commentDto.getId()));
    }

    @WithMockUser
    @ValueSource(longs = {-1L, 0L})
    @ParameterizedTest
    void create_whenPostIdNonPositive_thenReturnBadRequest(long invalidPostId) throws Exception {
        CommentRequestDto commentRequestDto = createCommentRequestDto(invalidPostId);

        createBadRequestResponse(mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequestDto))));
    }

    @WithMockUser
    @Test
    void create_whenPostIdIsNull_thenReturnBadRequest() throws Exception {
        CommentRequestDto commentRequestDto = createCommentRequestDto((Long) null);

        createBadRequestResponse(mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequestDto))));
    }

    @WithMockUser
    @Test
    void create_whenMessageContentTooLong_thenReturnBadRequest() throws Exception {
        CommentRequestDto commentRequestDto = createCommentRequestDto("x".repeat(1001));

        createBadRequestResponse(mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequestDto))));
    }

    @WithMockUser
    @Test
    void create_whenMessageContentIsBlank_thenReturnBadRequest() throws Exception {
        CommentRequestDto commentRequestDto = createCommentRequestDto("   ");

        createBadRequestResponse(mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequestDto))));
    }

    @Test
    void create_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        CommentRequestDto commentRequestDto = createCommentRequestDto(POST_ID);

        createUnauthorizedResponse(mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequestDto))));
    }

    @WithMockUser
    @Test
    void deleteById_thenDeleteComment() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", COMMENT_ID))
                .andExpect(status().isNoContent());

        verify(commentService).deleteById(COMMENT_ID);
    }

    @WithMockUser
    @ValueSource(longs = {-1L, 0L})
    @ParameterizedTest
    void deleteById_whenParamNonPositive_thenReturnBadRequest(long invalidCommentId) throws Exception {
        createBadRequestResponse(mockMvc.perform(delete(BASE_URL + "/{id}", invalidCommentId)));
    }

    @Test
    void deleteById_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        createUnauthorizedResponse(mockMvc.perform(delete(BASE_URL + "/{id}", COMMENT_ID)));
    }

    private CommentRequestDto createCommentRequestDto(Long postId) {
        return CommentRequestDto.builder()
                .postId(postId)
                .content("new comment")
                .build();
    }

    private CommentRequestDto createCommentRequestDto(String content) {
        return CommentRequestDto.builder()
                .postId(POST_ID)
                .content(content)
                .build();
    }
}