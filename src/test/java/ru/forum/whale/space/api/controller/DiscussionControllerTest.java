package ru.forum.whale.space.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.forum.whale.space.api.annotation.CustomWebMvcTest;
import ru.forum.whale.space.api.dto.DiscussionDto;
import ru.forum.whale.space.api.dto.DiscussionMetaDto;
import ru.forum.whale.space.api.dto.request.DiscussionRequestDto;
import ru.forum.whale.space.api.enums.PostSortFields;
import ru.forum.whale.space.api.enums.SortOrder;
import ru.forum.whale.space.api.service.DiscussionService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.forum.whale.space.api.util.TestUtil.*;

@CustomWebMvcTest(DiscussionController.class)
class DiscussionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DiscussionService discussionService;

    private static final String BASE_URL = "/discussions";

    @Test
    void getAll_thenReturnDiscussionMetaDtoList() throws Exception {
        DiscussionMetaDto discussionMetaDto = createDiscussionMetaDto();

        when(discussionService.findAll(Sort.by(SortOrder.DESC.getDirection(),
                PostSortFields.CREATED_AT.getFieldName()))).thenReturn(List.of(discussionMetaDto));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(discussionMetaDto.getId()));
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
        DiscussionDto discussionDto = DiscussionDto.builder()
                .id(DISCUSSION_ID)
                .build();

        when(discussionService.findById(DISCUSSION_ID)).thenReturn(discussionDto);

        mockMvc.perform(get(BASE_URL + "/{id}", DISCUSSION_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(discussionDto.getId()));
    }

    @WithMockUser
    @ValueSource(longs = {-1L, 0L})
    @ParameterizedTest
    void getById_whenParamNonPositive_thenReturnBadRequest(long invalidDiscussionId) throws Exception {
        createBadRequestResponse(mockMvc.perform(get(BASE_URL + "/{id}", invalidDiscussionId)));
    }

    @Test
    void getById_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        createUnauthorizedResponse(mockMvc.perform(get(BASE_URL + "/{id}", DISCUSSION_ID)));
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    void create_thenReturnCreatedDiscussionDto() throws Exception {
        DiscussionRequestDto discussionRequestDto = new DiscussionRequestDto("new discussion");

        DiscussionDto discussionDto = DiscussionDto.builder()
                .title(discussionRequestDto.getTitle())
                .build();

        when(discussionService.save(discussionRequestDto)).thenReturn(discussionDto);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(discussionRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(discussionDto.getTitle()));
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    void create_whenTitleNotInRange_thenReturnBadRequest() throws Exception {
        DiscussionRequestDto discussionRequestDto1 = new DiscussionRequestDto("x".repeat(4));
        DiscussionRequestDto discussionRequestDto2 = new DiscussionRequestDto("x".repeat(101));

        List<DiscussionRequestDto> discussionRequestDtos = List.of(discussionRequestDto1, discussionRequestDto2);

        for (var discussionRequestDto : discussionRequestDtos) {
            createBadRequestResponse(mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(discussionRequestDto))));
        }
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    void create_whenTitleIsBlank_thenReturnBadRequest() throws Exception {
        DiscussionRequestDto discussionRequestDto = new DiscussionRequestDto(" ".repeat(10));

        createBadRequestResponse(mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(discussionRequestDto))));
    }

    @WithMockUser
    @Test
    void create_whenUserNotAdmin_thenReturnForbidden() throws Exception {
        DiscussionRequestDto discussionRequestDto = new DiscussionRequestDto("new discussion");

        createForbiddenResponse(mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(discussionRequestDto))));
    }

    @Test
    void create_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        DiscussionRequestDto discussionRequestDto = new DiscussionRequestDto("new discussion");

        createUnauthorizedResponse(mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(discussionRequestDto))));
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    void deleteById_thenDeletePost() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", DISCUSSION_ID))
                .andExpect(status().isNoContent());

        verify(discussionService).deleteById(DISCUSSION_ID);
    }

    @WithMockUser(roles = {"ADMIN"})
    @ValueSource(longs = {-1L, 0L})
    @ParameterizedTest
    void deleteById_whenParamNonPositive_thenReturnBadRequest(long invalidDiscussionId) throws Exception {
        createBadRequestResponse(mockMvc.perform(delete(BASE_URL + "/{id}", invalidDiscussionId)));
    }

    @WithMockUser
    @Test
    void deleteById_whenUserNotAdmin_thenReturnForbidden() throws Exception {
        createForbiddenResponse(mockMvc.perform(delete(BASE_URL + "/{id}", DISCUSSION_ID)));
    }

    @Test
    void deleteById_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        createUnauthorizedResponse(mockMvc.perform(delete(BASE_URL + "/{id}", DISCUSSION_ID)));
    }

    private DiscussionMetaDto createDiscussionMetaDto() {
        return DiscussionMetaDto.builder()
                .id(DISCUSSION_ID)
                .build();
    }
}