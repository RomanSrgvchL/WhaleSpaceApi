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
import ru.forum.whale.space.api.dto.ChatDto;
import ru.forum.whale.space.api.dto.ChatWithLastMsgDto;
import ru.forum.whale.space.api.dto.request.ChatRequestDto;
import ru.forum.whale.space.api.service.ChatService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.forum.whale.space.api.util.TestUtil.*;

@CustomWebMvcTest(ChatController.class)
class ChatControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatService chatService;

    private static final String BASE_URL = "/chats";

    @WithMockUser
    @Test
    void getAll_thenReturnChatWithLastMsgDtoList() throws Exception {
        ChatWithLastMsgDto chatWithLastMsgDto = ChatWithLastMsgDto.builder()
                .id(CHAT_ID)
                .build();

        when(chatService.findAll()).thenReturn(List.of(chatWithLastMsgDto));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(chatWithLastMsgDto.getId()));
    }

    @Test
    void getAll_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        createUnauthorizedResponse(mockMvc.perform(get(BASE_URL)));
    }

    @WithMockUser
    @Test
    void getById_thenReturnChatDto() throws Exception {
        ChatDto chatDto = createChatDto();

        when(chatService.findById(CHAT_ID)).thenReturn(chatDto);

        mockMvc.perform(get(BASE_URL + "/{id}", CHAT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(CHAT_ID));
    }

    @WithMockUser
    @ValueSource(longs = {-1L, 0L})
    @ParameterizedTest
    void getById_whenParamNonPositive_thenReturnBadRequest(long invalidChatId) throws Exception {
        createBadRequestResponse(mockMvc.perform(get(BASE_URL + "/{id}", invalidChatId)));
    }

    @Test
    void getById_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        createUnauthorizedResponse(mockMvc.perform(get(BASE_URL + "/{id}", CHAT_ID)));
    }

    @WithMockUser
    @Test
    void getWithUser_thenReturnChatDto() throws Exception {
        ChatDto chatDto = createChatDto();

        when(chatService.findWithUser(PARTNER_ID)).thenReturn(chatDto);

        mockMvc.perform(get(BASE_URL + "/with/{partnerId}", PARTNER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(CHAT_ID));
    }

    @WithMockUser
    @ValueSource(longs = {-1L, 0L})
    @ParameterizedTest
    void getWithUser_whenParamNonPositive_thenReturnBadRequest(long invalidPartnerId) throws Exception {
        createBadRequestResponse(mockMvc.perform(get(BASE_URL + "/with/{partnerId}", invalidPartnerId)));
    }

    @Test
    void getWithUser_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        createUnauthorizedResponse(mockMvc.perform(get(BASE_URL + "/with/{partnerId}", PARTNER_ID)));
    }

    @WithMockUser
    @Test
    void create_thenReturnCreatedChatDto() throws Exception {
        ChatRequestDto chatRequestDto = new ChatRequestDto(PARTNER_ID);

        ChatDto chatDto = createChatDto();

        when(chatService.save(chatRequestDto.getPartnerId())).thenReturn(chatDto);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(chatDto.getId()));
    }


    @WithMockUser
    @ValueSource(longs = {-1L, 0L})
    @ParameterizedTest
    void create_whenPartnerIdNonPositive_thenReturnBadRequest(long invalidPartnerId) throws Exception {
        ChatRequestDto chatRequestDto = new ChatRequestDto(invalidPartnerId);

        createBadRequestResponse(mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRequestDto))));
    }

    @WithMockUser
    @Test
    void create_whenPartnerIdIsNull_thenReturnBadRequest() throws Exception {
        ChatRequestDto chatRequestDto = new ChatRequestDto(null);

        createBadRequestResponse(mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRequestDto))));
    }

    @Test
    void create_whenNotAuthenticated_thenReturnUnauthorized() throws Exception {
        ChatRequestDto chatRequestDto = new ChatRequestDto(PARTNER_ID);

        createUnauthorizedResponse(mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRequestDto))));
    }

    private ChatDto createChatDto() {
        return ChatDto.builder()
                .id(CHAT_ID)
                .build();
    }
}