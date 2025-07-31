package ru.forum.whale.space.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.forum.whale.space.api.dto.ChatDto;
import ru.forum.whale.space.api.dto.ChatMsgDto;
import ru.forum.whale.space.api.dto.ChatWithLastMsgDto;
import ru.forum.whale.space.api.exception.IllegalOperationException;
import ru.forum.whale.space.api.exception.ResourceAlreadyExistsException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.mapper.ChatMapper;
import ru.forum.whale.space.api.mapper.ChatMsgMapper;
import ru.forum.whale.space.api.model.Chat;
import ru.forum.whale.space.api.model.ChatMsg;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.ChatRepository;
import ru.forum.whale.space.api.repository.UserRepository;
import ru.forum.whale.space.api.util.SessionUtil;
import ru.forum.whale.space.api.util.TestUtil;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.forum.whale.space.api.util.TestUtil.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionUtilService sessionUtilService;

    @Mock
    private ChatMapper chatMapper;

    @Mock
    private ChatMsgMapper chatMsgMapper;

    @InjectMocks
    private ChatService chatService;

    @Test
    void findAll_thenReturnChatWithLastMsgDtoWithSortedMessagesListSortedByLastMessage() {
        User user1 = createUser(2L);
        User user2 = createUser(3L);
        User user3 = createUser(4L);

        ChatMsg msgA = createChatMessage("2025-01-02T10:00:00Z");
        ChatMsg msgB1 = createChatMessage("2025-01-01T10:00:00Z");
        ChatMsg msgB2 = createChatMessage("2025-01-03T10:00:00Z");

        Chat chat1 = createChat(1L, user1, user2, List.of(msgA));
        Chat chat2 = createChat(2L, user1, user3, List.of(msgB1, msgB2));

        ChatWithLastMsgDto dto1 = new ChatWithLastMsgDto();
        ChatWithLastMsgDto dto2 = new ChatWithLastMsgDto();

        ChatMsgDto msgDtoA = createChatMessageDto(msgA.getCreatedAt());
        ChatMsgDto msgDtoB2 = createChatMessageDto(msgB2.getCreatedAt());

        dto1.setLastMessage(msgDtoA);
        dto2.setLastMessage(msgDtoB2);

        when(chatRepository.findAllByUserIdWithMessages(CURRENT_USER_ID)).thenReturn(List.of(chat1, chat2));
        when(chatMapper.chatToChatWithLastMsgDto(chat1)).thenReturn(dto1);
        when(chatMapper.chatToChatWithLastMsgDto(chat2)).thenReturn(dto2);
        when(chatMsgMapper.chatMsgToChatMsgDto(msgA)).thenReturn(msgDtoA);
        when(chatMsgMapper.chatMsgToChatMsgDto(msgB2)).thenReturn(msgDtoB2);

        try (MockedStatic<SessionUtil> mockedSessionUtil = mockStatic(SessionUtil.class)) {
            mockedSessionUtil.when(SessionUtil::getCurrentUserId).thenReturn(CURRENT_USER_ID);

            List<ChatWithLastMsgDto> result = chatService.findAll();

            assertEquals(2, result.size());
            assertEquals(msgDtoB2.getCreatedAt(), result.get(0).getLastMessage().getCreatedAt());
            assertEquals(msgDtoA.getCreatedAt(), result.get(1).getLastMessage().getCreatedAt());
        }
    }

    @Test
    void findById_whenChatNotFound_thenThrowResourceNotFoundException() {
        when(chatRepository.findByIdWithMessages(CHAT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> chatService.findById(CHAT_ID));

        assertEquals("Чат с указанным ID не найден", e.getMessage());
    }

    @Test
    void findById_whenUserNotInChat_thenThrowIllegalOperationException() {
        User user1 = createUser(2L);
        User user2 = createUser(3L);

        Chat chat = TestUtil.createChat(user1, user2);

        when(chatRepository.findByIdWithMessages(CHAT_ID)).thenReturn(Optional.of(chat));

        try (MockedStatic<SessionUtil> mockedSessionUtil = mockStatic(SessionUtil.class)) {
            mockedSessionUtil.when(SessionUtil::getCurrentUserId).thenReturn(CURRENT_USER_ID);

            IllegalOperationException e = assertThrows(IllegalOperationException.class,
                    () -> chatService.findById(CHAT_ID));

            assertEquals("Доступ к чужому чату запрещён", e.getMessage());
        }
    }

    @Test
    void findById_thenReturnChatDtoWithSortedMessages() {
        User user1 = createUser(CURRENT_USER_ID);
        User user2 = createUser(2L);

        ChatMsg msg1 = createChatMessage("2025-01-02T10:00:00Z");
        ChatMsg msg2 = createChatMessage("2025-01-01T10:00:00Z");

        Chat chat = Chat.builder()
                .user1(user1)
                .user2(user2)
                .messages(new ArrayList<>(List.of(msg1, msg2)))
                .build();

        ChatDto expected = new ChatDto();

        when(chatRepository.findByIdWithMessages(CHAT_ID)).thenReturn(Optional.of(chat));
        when(chatMapper.chatToChatDto(chat)).thenReturn(expected);

        try (MockedStatic<SessionUtil> mockedSessionUtil = mockStatic(SessionUtil.class)) {
            mockedSessionUtil.when(SessionUtil::getCurrentUserId).thenReturn(CURRENT_USER_ID);

            ChatDto result = chatService.findById(CHAT_ID);

            List<ChatMsg> messages = chat.getMessages();

            assertEquals(msg2, messages.get(0));
            assertEquals(msg1, messages.get(1));
            assertEquals(expected, result);
        }
    }

    @Test
    void findWithUser_whenSameUser_thenThrowIllegalOperationException() {
        User currentUser = createUser(CURRENT_USER_ID);

        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);

        IllegalOperationException e = assertThrows(IllegalOperationException.class,
                () -> chatService.findWithUser(CURRENT_USER_ID));

        assertEquals("Нельзя получить чат с самим собой", e.getMessage());
    }

    @Test
    void findWithUser_whenPartnerNotFound_thenThrowResourceNotFoundException() {
        User currentUser = createUser(CURRENT_USER_ID);

        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(PARTNER_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> chatService.findWithUser(PARTNER_ID));

        assertEquals("Пользователь с указанным ID не найден", e.getMessage());
    }


    @Test
    void findWithUser_whenChatNotFound_thenThrowResourceNotFoundException() {
        User currentUser = createUser(CURRENT_USER_ID);
        User partner = createUser(PARTNER_ID);

        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(PARTNER_ID)).thenReturn(Optional.of(partner));
        when(chatRepository.findByUser1AndUser2(currentUser, partner)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> chatService.findWithUser(PARTNER_ID));

        assertEquals("Чат с указанным пользователем не найден", e.getMessage());
    }


    @Test
    void findWithUser_whenCurrentUserIdLessThanPartnerId_thenUsersAreSortedById() {
        User currentUser = createUser(CURRENT_USER_ID);
        User partner = createUser(PARTNER_ID);

        Chat chat = TestUtil.createChat(currentUser, partner);

        ChatDto expected = new ChatDto();

        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(PARTNER_ID)).thenReturn(Optional.of(partner));
        when(chatRepository.findByUser1AndUser2(currentUser, partner)).thenReturn(Optional.of(chat));
        when(chatMapper.chatToChatDto(chat)).thenReturn(expected);

        ChatDto result = chatService.findWithUser(PARTNER_ID);

        ArgumentCaptor<User> userCaptor1 = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<User> userCaptor2 = ArgumentCaptor.forClass(User.class);

        verify(chatRepository).findByUser1AndUser2(userCaptor1.capture(), userCaptor2.capture());

        User first = userCaptor1.getValue();
        User second = userCaptor2.getValue();

        assertEquals(expected, result);
        assertTrue(first.getId() < second.getId());
    }

    @Test
    void findWithUser_whenPartnerIdLessThanCurrentUserId_thenUsersAreSortedById() {
        long currentUserId = 2L;
        long partnerId = 1L;

        User currentUser = createUser(currentUserId);
        User partner = createUser(partnerId);

        Chat chat = TestUtil.createChat(partner, currentUser);

        ChatDto expected = new ChatDto();

        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(partnerId)).thenReturn(Optional.of(partner));
        when(chatRepository.findByUser1AndUser2(partner, currentUser)).thenReturn(Optional.of(chat));
        when(chatMapper.chatToChatDto(chat)).thenReturn(expected);

        ChatDto result = chatService.findWithUser(partnerId);

        ArgumentCaptor<User> userCaptor1 = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<User> userCaptor2 = ArgumentCaptor.forClass(User.class);

        verify(chatRepository).findByUser1AndUser2(userCaptor1.capture(), userCaptor2.capture());

        User first = userCaptor1.getValue();
        User second = userCaptor2.getValue();

        assertEquals(expected, result);
        assertTrue(first.getId() < second.getId());
    }

    @Test
    void findWithUser_thenReturnChatDto() {
        User currentUser = createUser(CURRENT_USER_ID);
        User partner = createUser(PARTNER_ID);

        Chat chat = TestUtil.createChat(currentUser, partner);

        ChatDto expected = new ChatDto();

        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(PARTNER_ID)).thenReturn(Optional.of(partner));
        when(chatRepository.findByUser1AndUser2(currentUser, partner)).thenReturn(Optional.of(chat));
        when(chatMapper.chatToChatDto(chat)).thenReturn(expected);

        ChatDto result = chatService.findWithUser(PARTNER_ID);

        assertEquals(expected, result);
    }

    @Test
    void save_whenSameUser_thenThrowIllegalOperationException() {
        User currentUser = createUser(CURRENT_USER_ID);

        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);

        IllegalOperationException e = assertThrows(IllegalOperationException.class,
                () -> chatService.save(CURRENT_USER_ID));

        assertEquals("Нельзя создать чат с самим собой", e.getMessage());
    }

    @Test
    void save_whenChatExists_thenThrowResourceAlreadyExistsException() {
        User currentUser = createUser(CURRENT_USER_ID);
        User partner = createUser(PARTNER_ID);

        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(PARTNER_ID)).thenReturn(Optional.of(partner));
        when(chatRepository.existsByUser1AndUser2(currentUser, partner)).thenReturn(true);

        ResourceAlreadyExistsException e = assertThrows(ResourceAlreadyExistsException.class,
                () -> chatService.save(PARTNER_ID));

        assertEquals("Чат с указанным пользователем уже существует", e.getMessage());
    }

    @Test
    void save_thenReturnCreatedChatDto() {
        User currentUser = createUser(CURRENT_USER_ID);
        User partner = createUser(PARTNER_ID);

        Chat chat = Chat.builder()
                .user1(currentUser)
                .user2(partner)
                .build();

        ChatDto expected = new ChatDto();

        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(PARTNER_ID)).thenReturn(Optional.of(partner));
        when(chatRepository.existsByUser1AndUser2(currentUser, partner)).thenReturn(false);
        when(chatRepository.save(chat)).thenReturn(chat);
        when(chatMapper.chatToChatDto(chat)).thenReturn(expected);

        ChatDto result = chatService.save(PARTNER_ID);

        ArgumentCaptor<Chat> chatCaptor = ArgumentCaptor.forClass(Chat.class);

        verify(chatRepository).save(chatCaptor.capture());

        Chat savedChat = chatCaptor.getValue();

        assertEquals(expected, result);
        assertEquals(CURRENT_USER_ID, savedChat.getUser1().getId());
        assertEquals(PARTNER_ID, savedChat.getUser2().getId());
    }

    private Chat createChat(long chatId, User user1, User user2, List<ChatMsg> messages) {
        return Chat.builder()
                .id(chatId)
                .user1(user1)
                .user2(user2)
                .messages(messages)
                .build();
    }

    private ChatMsg createChatMessage(String createdAt) {
        return ChatMsg.builder()
                .createdAt(ZonedDateTime.parse(createdAt))
                .build();
    }

    private ChatMsgDto createChatMessageDto(ZonedDateTime createdAt) {
        return ChatMsgDto.builder()
                .createdAt(createdAt)
                .build();
    }
}
