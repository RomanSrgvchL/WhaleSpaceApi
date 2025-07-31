package ru.forum.whale.space.api.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.forum.whale.space.api.annotation.CustomWebMvcTest;
import ru.forum.whale.space.api.service.CommentLikeService;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.forum.whale.space.api.util.TestUtil.*;

@CustomWebMvcTest(CommentLikeController.class)
class CommentLikeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentLikeService commentLikeService;

    private static final String BASE_URL = "/comments/%d/likes";

    @WithMockUser
    @Test
    void like_thenLikeComment() throws Exception {
        mockMvc.perform(post(BASE_URL.formatted(COMMENT_ID)))
                .andExpect(status().isCreated());

        verify(commentLikeService).like(COMMENT_ID);
    }

    @WithMockUser
    @ValueSource(longs = {-1L, 0L})
    @ParameterizedTest
    void like_whenParamNonPositive_thenReturnBadRequest(long invalidCommentId) throws Exception {
        createBadRequestResponse(mockMvc.perform(post(BASE_URL.formatted(invalidCommentId))));
    }

    @Test
    void like_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        createUnauthorizedResponse(mockMvc.perform(post(BASE_URL.formatted(COMMENT_ID))));
    }

    @WithMockUser
    @Test
    void unlike_thenUnlikeComment() throws Exception {
        mockMvc.perform(delete(BASE_URL.formatted(COMMENT_ID)))
                .andExpect(status().isNoContent());

        verify(commentLikeService).unlike(COMMENT_ID);
    }

    @WithMockUser
    @ValueSource(longs = {-1L, 0L})
    @ParameterizedTest
    void unlike_whenParamNonPositive_thenReturnBadRequest(long invalidCommentId) throws Exception {
        createBadRequestResponse(mockMvc.perform(delete(BASE_URL.formatted(invalidCommentId))));
    }

    @Test
    void unlike_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        createUnauthorizedResponse(mockMvc.perform(delete(BASE_URL.formatted(COMMENT_ID))));
    }
}