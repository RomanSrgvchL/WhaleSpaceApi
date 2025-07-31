package ru.forum.whale.space.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.dto.DiscussionMsgDto;
import ru.forum.whale.space.api.dto.request.MessageRequestDto;
import ru.forum.whale.space.api.exception.ResourceNotFoundException;
import ru.forum.whale.space.api.mapper.DiscussionMsgMapper;
import ru.forum.whale.space.api.model.*;
import ru.forum.whale.space.api.repository.DiscussionMsgRepository;
import ru.forum.whale.space.api.repository.DiscussionRepository;
import ru.forum.whale.space.api.util.FileUtil;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.forum.whale.space.api.util.TestUtil.DISCUSSION_ID;
import static ru.forum.whale.space.api.util.TestUtil.createMockFiles;

@ExtendWith(MockitoExtension.class)
class DiscussionMsgServiceTest {
    @Mock
    private DiscussionMsgRepository discussionMsgRepository;

    @Mock
    private DiscussionRepository discussionRepository;

    @Mock
    private SessionUtilService sessionUtilService;

    @Mock
    private MinioService minioService;

    @Mock
    private DiscussionMsgMapper discussionMsgMapper;

    @InjectMocks
    private DiscussionMsgService discussionMsgService;

    @Test
    void save_whenDiscussionNotFound_thenResourceNotFoundException() {
        User currentUser = new User();

        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(discussionRepository.findById(DISCUSSION_ID)).thenReturn(Optional.empty());

        try (MockedStatic<FileUtil> mockedFileUtil = mockStatic(FileUtil.class)) {
            ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                    () -> discussionMsgService.save(DISCUSSION_ID, null, null));

            mockedFileUtil.verify(() -> FileUtil.validateFiles(any()));

            assertEquals("Обсуждение с указанным ID не найдено", e.getMessage());
        }
    }

    @Test
    void save_thenReturnDiscussionMsgDto() {
        MessageRequestDto messageRequestDto = new MessageRequestDto("new message");

        List<MultipartFile> files = createMockFiles(2);

        List<String> imageFileNames = List.of("file1", "file2");

        User currentUser = new User();

        Discussion discussion = Discussion.builder()
                .id(DISCUSSION_ID)
                .creator(currentUser)
                .build();

        DiscussionMsg discussionMsg = DiscussionMsg.builder()
                .content(messageRequestDto.getContent())
                .sender(currentUser)
                .discussion(discussion)
                .imageFileNames(List.copyOf(imageFileNames))
                .build();

        DiscussionMsgDto expected = new DiscussionMsgDto();

        when(sessionUtilService.findCurrentUser()).thenReturn(currentUser);
        when(discussionRepository.findById(DISCUSSION_ID)).thenReturn(Optional.of(discussion));
        when(minioService.uploadImages(eq(DiscussionMsgService.DISCUSSION_MESSAGES_BUCKET), eq(files), anyString()))
                .thenReturn(imageFileNames);
        when(discussionMsgRepository.save(discussionMsg)).thenReturn(discussionMsg);
        when(discussionMsgMapper.discussionMsgToDiscussionMsgDto(discussionMsg)).thenReturn(expected);

        try (MockedStatic<FileUtil> mockedFileUtil = mockStatic(FileUtil.class)) {
            DiscussionMsgDto result = discussionMsgService.save(DISCUSSION_ID, messageRequestDto, files);

            ArgumentCaptor<DiscussionMsg> discussionMsgCaptor = ArgumentCaptor.forClass(DiscussionMsg.class);

            mockedFileUtil.verify(() -> FileUtil.validateFiles(files));
            verify(discussionMsgRepository).save(discussionMsgCaptor.capture());

            DiscussionMsg savedMessage = discussionMsgCaptor.getValue();

            assertEquals(expected, result);
            assertEquals(discussion, savedMessage.getDiscussion());
            assertEquals(currentUser, savedMessage.getSender());
            assertEquals(messageRequestDto.getContent(), savedMessage.getContent());
            assertEquals(imageFileNames, savedMessage.getImageFileNames());
        }
    }
}