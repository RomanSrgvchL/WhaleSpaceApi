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
import ru.forum.whale.space.api.dto.ChatMsgDto;
import ru.forum.whale.space.api.dto.request.MessageRequestDto;
import ru.forum.whale.space.api.service.ChatMsgService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.forum.whale.space.api.util.TestUtil.*;

@CustomWebMvcTest(ChatMsgController.class)
class ChatMsgControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatMsgService chatMsgService;

    @MockitoBean
    private SimpMessagingTemplate messagingTemplate;

    private static final String BASE_URL = "/chats/%d/messages";

    @WithMockUser
    @Test
    void create_thenSendMessageAndReturnCreatedChatMsgDto() throws Exception {
        MessageRequestDto messageRequestDto = new MessageRequestDto("new message");

        ChatMsgDto chatMsgDto = ChatMsgDto.builder()
                .content(messageRequestDto.getContent())
                .build();

        when(chatMsgService.save(CHAT_ID, messageRequestDto, null)).thenReturn(chatMsgDto);

        MockMultipartFile messagePart = createMessageMockMultipartFile(messageRequestDto);

        mockMvc.perform(multipart(BASE_URL.formatted(CHAT_ID))
                        .file(messagePart))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").value(chatMsgDto.getContent()));

        verify(messagingTemplate).convertAndSend("/chat/newMessage/" + CHAT_ID, chatMsgDto);
    }


    @WithMockUser
    @ValueSource(longs = {0, -1})
    @ParameterizedTest
    void create_whenParamNonPositive_thenReturnBadRequest(long invalidChatId) throws Exception {
        MessageRequestDto messageRequestDto = new MessageRequestDto("new message");

        MockMultipartFile messagePart = createMessageMockMultipartFile(messageRequestDto);

        createBadRequestResponse(mockMvc.perform(multipart(BASE_URL.formatted(invalidChatId))
                .file(messagePart)));
    }

    @WithMockUser
    @Test
    void create_whenMessageContentTooLong_thenReturnBadRequest() throws Exception {
        MessageRequestDto messageRequestDto = new MessageRequestDto("x".repeat(201));

        MockMultipartFile messagePart = createMessageMockMultipartFile(messageRequestDto);

        createBadRequestResponse(mockMvc.perform(multipart(BASE_URL.formatted(CHAT_ID))
                .file(messagePart)));
    }

    @WithMockUser
    @Test
    void create_whenMessageContentIsBlank_thenReturnBadRequest() throws Exception {
        MessageRequestDto messageRequestDto = new MessageRequestDto("   ");

        MockMultipartFile messagePart = createMessageMockMultipartFile(messageRequestDto);

        createBadRequestResponse(mockMvc.perform(multipart(BASE_URL.formatted(CHAT_ID))
                .file(messagePart)));
    }

    @Test
    void create_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        MessageRequestDto messageRequestDto = new MessageRequestDto("new message");

        MockMultipartFile messagePart = createMessageMockMultipartFile(messageRequestDto);

        createUnauthorizedResponse(mockMvc.perform(multipart(BASE_URL.formatted(CHAT_ID))
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