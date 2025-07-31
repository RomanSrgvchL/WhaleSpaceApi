package ru.forum.whale.space.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.dto.ChatMsgDto;
import ru.forum.whale.space.api.dto.request.MessageRequestDto;
import ru.forum.whale.space.api.exception.IllegalOperationException;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.mapper.ChatMsgMapper;
import ru.forum.whale.space.api.model.Chat;
import ru.forum.whale.space.api.model.ChatMsg;
import ru.forum.whale.space.api.model.User;
import ru.forum.whale.space.api.repository.ChatMsgRepository;
import ru.forum.whale.space.api.repository.ChatRepository;
import ru.forum.whale.space.api.util.FileUtil;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.forum.whale.space.api.util.TestUtil.*;

@ExtendWith(MockitoExtension.class)
class ChatMsgServiceTest {
    @Mock
    private ChatMsgRepository chatMsgRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private SessionUtilService sessionUtilService;

    @Mock
    private MinioService minioService;

    @Mock
    private ChatMsgMapper chatMsgMapper;

    @InjectMocks
    private ChatMsgService chatMsgService;

    @Test
    void save_whenChatNotFound_thenResourceNotFoundException() {
        User currentUser = createUser(CURRENT_USER_ID);

        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(chatRepository.findById(CHAT_ID)).thenReturn(Optional.empty());

        try (MockedStatic<FileUtil> mockedFileUtil = mockStatic(FileUtil.class)) {
            ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                    () -> chatMsgService.save(CHAT_ID, null, null));

            mockedFileUtil.verify(() -> FileUtil.validateFiles(any()));

            assertEquals("Чат с указанным ID не найден", e.getMessage());
        }
    }

    @Test
    void save_whenUserNotInChat_thenThrowIllegalOperationException() {
        User currentUser = createUser(CURRENT_USER_ID);
        User user1 = createUser(2L);
        User user2 = createUser(3L);

        Chat chat = createChat(user1, user2);

        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(chatRepository.findById(CHAT_ID)).thenReturn(Optional.of(chat));

        try (MockedStatic<FileUtil> mockedFileUtil = mockStatic(FileUtil.class)) {
            IllegalOperationException e = assertThrows(IllegalOperationException.class,
                    () -> chatMsgService.save(CHAT_ID, null, null));

            mockedFileUtil.verify(() -> FileUtil.validateFiles(any()));

            assertEquals("Доступ к чужому чату запрещён", e.getMessage());
        }
    }

    @Test
    void save_thenReturnChatMsgDto() {
        MessageRequestDto messageRequestDto = new MessageRequestDto("new message");

        List<MultipartFile> files = createMockFiles(2);

        List<String> imageFileNames = List.of("file1", "file2");

        User currentUser = createUser(CURRENT_USER_ID);
        User partner = createUser(3L);

        Chat chat = Chat.builder()
                .id(CHAT_ID)
                .user1(currentUser)
                .user2(partner)
                .build();

        ChatMsg chatMsg = ChatMsg.builder()
                .content(messageRequestDto.getContent())
                .sender(currentUser)
                .chat(chat)
                .imageFileNames(List.copyOf(imageFileNames))
                .build();

        ChatMsgDto expected = new ChatMsgDto();

        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(chatRepository.findById(CHAT_ID)).thenReturn(Optional.of(chat));
        when(minioService.uploadImages(eq(ChatMsgService.CHAT_MESSAGES_BUCKET), eq(files), anyString()))
                .thenReturn(imageFileNames);
        when(chatMsgRepository.save(chatMsg)).thenReturn(chatMsg);
        when(chatMsgMapper.chatMsgToChatMsgDto(chatMsg)).thenReturn(expected);

        try (MockedStatic<FileUtil> mockedFileUtil = mockStatic(FileUtil.class)) {
            ChatMsgDto result = chatMsgService.save(CHAT_ID, messageRequestDto, files);

            ArgumentCaptor<ChatMsg> chatMsgCaptor = ArgumentCaptor.forClass(ChatMsg.class);

            mockedFileUtil.verify(() -> FileUtil.validateFiles(files));
            verify(chatMsgRepository).save(chatMsgCaptor.capture());

            ChatMsg savedMessage = chatMsgCaptor.getValue();

            assertEquals(expected, result);
            assertEquals(chat, savedMessage.getChat());
            assertEquals(currentUser, savedMessage.getSender());
            assertEquals(messageRequestDto.getContent(), savedMessage.getContent());
            assertEquals(imageFileNames, savedMessage.getImageFileNames());
        }
    }
}