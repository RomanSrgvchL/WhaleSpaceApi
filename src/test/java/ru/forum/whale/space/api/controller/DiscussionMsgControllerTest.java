package ru.forum.whale.space.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.forum.whale.space.api.annotation.CustomWebMvcTest;
import ru.forum.whale.space.api.dto.DiscussionMsgDto;
import ru.forum.whale.space.api.dto.request.MessageRequestDto;
import ru.forum.whale.space.api.service.DiscussionMsgService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.forum.whale.space.api.util.TestUtil.*;

@CustomWebMvcTest(DiscussionMsgController.class)
class DiscussionMsgControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DiscussionMsgService discussionMsgService;

    @MockitoBean
    private SimpMessagingTemplate messagingTemplate;

    private static final String BASE_URL = "/discussions/%d/messages";

    @WithMockUser
    @Test
    void create_thenSendMessageAndReturnCreatedDiscussionMsgDto() throws Exception {
        MessageRequestDto messageRequestDto = new MessageRequestDto("new message");

        DiscussionMsgDto discussionMsgDto = DiscussionMsgDto.builder()
                .content(messageRequestDto.getContent())
                .build();

        when(discussionMsgService.save(DISCUSSION_ID, messageRequestDto, null)).thenReturn(discussionMsgDto);

        MockMultipartFile messagePart = createMessageMockMultipartFile(messageRequestDto);

        mockMvc.perform(multipart(BASE_URL.formatted(DISCUSSION_ID))
                        .file(messagePart))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").value(discussionMsgDto.getContent()));

        verify(messagingTemplate).convertAndSend("/discussion/newMessage/" + DISCUSSION_ID, discussionMsgDto);
    }


    @WithMockUser
    @ValueSource(longs = {0, -1})
    @ParameterizedTest
    void create_whenParamNonPositive_thenReturnBadRequest(long invalidDiscussionId) throws Exception {
        MessageRequestDto messageRequestDto = new MessageRequestDto("new message");

        MockMultipartFile messagePart = createMessageMockMultipartFile(messageRequestDto);

        createBadRequestResponse(mockMvc.perform(multipart(BASE_URL.formatted(invalidDiscussionId))
                .file(messagePart)));
    }

    @WithMockUser
    @Test
    void create_whenMessageContentTooLong_thenReturnBadRequest() throws Exception {
        MessageRequestDto messageRequestDto = new MessageRequestDto("x".repeat(201));

        MockMultipartFile messagePart = createMessageMockMultipartFile(messageRequestDto);

        createBadRequestResponse(mockMvc.perform(multipart(BASE_URL.formatted(DISCUSSION_ID))
                .file(messagePart)));
    }

    @WithMockUser
    @Test
    void create_whenMessageContentIsBlank_thenReturnBadRequest() throws Exception {
        MessageRequestDto messageRequestDto = new MessageRequestDto("   ");

        MockMultipartFile messagePart = createMessageMockMultipartFile(messageRequestDto);

        createBadRequestResponse(mockMvc.perform(multipart(BASE_URL.formatted(DISCUSSION_ID))
                .file(messagePart)));
    }

    @Test
    void create_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        MessageRequestDto messageRequestDto = new MessageRequestDto("new message");

        MockMultipartFile messagePart = createMessageMockMultipartFile(messageRequestDto);

        createUnauthorizedResponse(mockMvc.perform(multipart(BASE_URL.formatted(DISCUSSION_ID))
                .file(messagePart)));
    }

    private MockMultipartFile createMessageMockMultipartFile(MessageRequestDto messageRequestDto)
            throws JsonProcessingException {
        return new MockMultipartFile(
                "message",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(messageRequestDto)
        );
    }
}